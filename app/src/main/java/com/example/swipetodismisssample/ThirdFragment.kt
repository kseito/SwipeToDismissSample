package com.example.swipetodismisssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ThirdFragment : Fragment() {

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                var height by remember { mutableStateOf(0) }
                var offsetY by remember { mutableStateOf(0f) }
                val coroutineScope = rememberCoroutineScope()
                val draggableState = rememberDraggableState { delta ->
                    offsetY += delta
                }

                Surface(
                    modifier = Modifier
                        .background(Color.Yellow)
                ) {
                    val pagerState = rememberPagerState()
                    //HorizontalPagerのcurrentPageに変化があった時にコールバックを返す
                    LaunchedEffect(Unit) {
                        snapshotFlow { pagerState.currentPage }
                            .collectLatest {
                                println("今のページは$it")
                            }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                height = coordinates.size.height
                            }
                            .draggable(
                                orientation = Orientation.Vertical,
                                state = draggableState,
                                onDragStopped = {
                                    if (offsetY < height / 3) {
                                        coroutineScope.launch {
                                            animate(offsetY, 0f) { value, _ ->
                                                offsetY = value
                                            }
                                        }
                                    } else {
                                        findNavController().popBackStack()
                                    }
                                }
                            )
                    ) {
                        HorizontalPager(
                            count = 5,
                            state = pagerState
                        ) {
                            SlideshowPage(offsetY)
                        }
                    }
                }
            }
        }
    }

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @Composable
    private fun SlideshowPage(offsetY: Float) {
        val width = remember { mutableStateOf(0) }
        val scrollState = rememberScrollState()
        var scale by remember { mutableStateOf(1f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    width.value = coordinates.size.width
                }
                .offset {
                    IntOffset(0, offsetY.roundToInt())
                }
                .background(color = Color.Black)
        ) {
            val offsetX = remember { Animatable(0f) }
            val offsetY = remember { Animatable(0f) }
            var width by remember { mutableStateOf(0) }
            var height by remember { mutableStateOf(0) }
            val coroutinesScope = rememberCoroutineScope()

            Image(
                painter = painterResource(id = R.drawable.puipui_test),
                contentDescription = null,
                modifier = Modifier
                    .onGloballyPositioned {
                        width = it.size.width
                        height = it.size.height
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX.value,
                        translationY = offsetY.value
                    )
                    //画像ズームっぽい動き
                    .pointerInput(1) {
                        println("あ")
                        forEachGesture {
                            println("い")
                            awaitPointerEventScope {
                                println("う${scale}")
                                awaitFirstDown(false)
                                do {
                                    val event = awaitPointerEvent()
                                    val canceled = event.changes.any { it.positionChangeConsumed() }
                                    if (!canceled) {
                                        scale *= event.calculateZoom()
                                        val newOffset = event.calculatePan() * scale
                                        val leftBound = -width * (scale - 1) / 2
                                        val rightBound = width * (scale) / 4
                                        println(
                                            "offsetXは" + (offsetX.value + newOffset.x).coerceIn(
                                                leftBound,
                                                rightBound
                                            )
                                        )
                                        if (scale > 1f) {
                                            coroutinesScope.launch {
                                                offsetX.snapTo((offsetX.value + newOffset.x).coerceIn(leftBound, rightBound))
                                                offsetY.snapTo(offsetY.value + newOffset.y)
                                            }
                                        }
                                        if ((leftBound < offsetX.value && offsetX.value < rightBound) || scale > 1f) {
                                            event.changes.forEach { it.consumeAllChanges() }
                                        }
                                    }

                                } while (event.changes.any { it.pressed })
                            }
                        }
                    }
                    .pointerInput(2) {
                        detectTapGestures(
                            onDoubleTap = {
                                coroutinesScope.launch {
                                    if (scale > 1f) {
                                        launch {
                                            animate(
                                                initialValue = scale,
                                                targetValue = 1f
                                            ) { value, velocity ->
                                                scale = value
                                            }
                                        }
                                        launch {
                                            offsetX.animateTo(targetValue = 0f)
                                        }
                                        launch {
                                            offsetY.animateTo(targetValue = 0f)
                                        }
                                    } else {
                                        animate(
                                            initialValue = 1f,
                                            targetValue = 2f
                                        ) { value, velocity ->
                                            scale = value
                                        }
                                    }
                                }

                                println(scale)
                            }
                        )
                    }
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
            AnimatedVisibility(
                visible = scale <= 1f,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = "タイトル",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "なんかいろいろ説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}