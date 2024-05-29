package view.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import viewmodel.graph.VertexViewModel

@Composable
fun <V> VertexView(
    viewModel: VertexViewModel<V>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier
        .size(viewModel.radius * 2, viewModel.radius * 2)
        .offset(viewModel.x, viewModel.y)
//        .animateContentSize(animationSpec = tween(durationMillis = 500))
        .border(
            width = 5.dp,
            color = viewModel.color,
            shape = CircleShape
        )
        .background(
            color = Color.White,
            shape = CircleShape
        )
        .pointerInput(viewModel) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                viewModel.onDrag(dragAmount)
            }
        }
    ) {
        if (viewModel.labelVisible) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(0.dp, 0.dp),
                text = viewModel.label,
            )
        }
    }
}