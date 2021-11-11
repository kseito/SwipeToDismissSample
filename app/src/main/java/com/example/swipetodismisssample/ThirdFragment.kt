package com.example.swipetodismisssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ThirdFragment : Fragment() {

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Surface(
                    modifier = Modifier
                        .background(Color.Yellow)

                ) {
                    Box {
                        HorizontalPager(count = 5) {
                            SlideshowPage()
                        }

//                        Button(
//                            onClick = { println("クリックした") },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(100.dp)
//                            ) {
//                            (Text(text = "押すな危険"))
//                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SlideshowPage() {
        val height = remember { mutableStateOf(0) }
        val offsetY = remember { mutableStateOf(0f) }
        val coroutineScope = rememberCoroutineScope()
        val draggableState = rememberDraggableState { delta ->
            println("draggableState$delta")
            offsetY.value += delta
        }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    height.value = coordinates.size.height
                }
                .offset {
                    IntOffset(
                        0,
                        offsetY.value.roundToInt()
                    )
                }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = draggableState,
                    onDragStopped = {
                        if (offsetY.value < height.value / 3) {
                            coroutineScope.launch {
                                androidx.compose.animation.core.animate(offsetY.value, 0f) { value, _ ->
                                    offsetY.value = value
                                }
                            }
                        } else {
                            findNavController().popBackStack()
                        }
                    }
                )
//                            .nestedScroll(
//                                connection = object : NestedScrollConnection {
//                                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                                        val delta = available.y
//
//                                        return if (scrollState.value == 0 && delta > 0) {
//                                            println("hoge$delta")
//                                            coroutineScope2.launch {
//                                                draggableState.drag {
//                                                    this.dragBy(delta)
//                                                }
//                                            }
//                                            available
//                                        } else {
//                                            println("fuga$delta")
//                                            return Offset.Zero
//                                        }
//                                    }
//                                }
//                            )
                .background(color = Color.Cyan)
        ) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .pointerInput(1) {
                        forEachGesture {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                println("$pan, $zoom")
                                if (scale == 1f) {
                                    offsetY.value += pan.y
                                } else {
                                    offset += pan
                                }
                                scale *= zoom
                            }
                        }
                    }
                    .pointerInput(2) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = if (scale > 1f) {
                                    1f
                                } else {
                                    2f
                                }
                                println(scale)
                            }
                        )
                    }
            )
            Text(
                text = "タイトル"
            )
            Text(
                text = "なんかいろいろ説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n説明\n",
                modifier = Modifier
                    .verticalScroll(scrollState)
            )
        }
    }
}
