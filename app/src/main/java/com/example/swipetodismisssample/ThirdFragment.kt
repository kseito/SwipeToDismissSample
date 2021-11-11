package com.example.swipetodismisssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ThirdFragment : Fragment() {

    @ExperimentalMaterialApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Surface(
                    modifier = Modifier
                        .background(Color.Yellow)

                ) {
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
                        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                            println("$zoomChange, $offsetChange")
                            scale *= zoomChange
                            offset += offsetChange
                        }

                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                // add transformable to listen to multitouch transformation events
                                // after offset
                                .transformable(state = state)
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
        }
    }
}
