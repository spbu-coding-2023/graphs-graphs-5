package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import viewmodel.EdgeViewModel
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun <V> DirectedEdgeView(
    viewModel: EdgeViewModel<V>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val start = Offset(
            viewModel.u.x.toPx() + viewModel.u.radius.toPx(),
            viewModel.u.y.toPx() + viewModel.u.radius.toPx(),
        )
        val end = Offset(
            viewModel.v.x.toPx() + viewModel.v.radius.toPx(),
            viewModel.v.y.toPx() + viewModel.v.radius.toPx(),
        )
        val arrowSize = 15f // Size of the arrowhead
        val angle = atan2(end.y - start.y, end.x - start.x)
        val alpha = PI / 6
        val midX = (start.x + end.x) / 2
        val midY = (start.y + end.y) / 2
        val arrowPoint1X = midX - arrowSize * cos(angle + alpha)
        val arrowPoint1Y = midY - arrowSize * sin(angle + alpha)
        val arrowPoint2X = midX - arrowSize * cos(angle - alpha)
        val arrowPoint2Y = midY - arrowSize * sin(angle - alpha)
        val path = Path().apply {
            moveTo(start.x, start.y)
            lineTo(end.x, end.y)
            moveTo(midX, midY)
            lineTo(arrowPoint1X.toFloat(), arrowPoint1Y.toFloat())
            moveTo(midX, midY)
            lineTo(arrowPoint2X.toFloat(), arrowPoint2Y.toFloat())
        }
        drawPath(
            path = path,
            color = BlackAndWhite20,
            style = Stroke(width = 1.3.dp.toPx())
        )
    }
    if (viewModel.labelVisible) {
        Text(
            modifier = Modifier
                .offset(
                    viewModel.u.x + (viewModel.v.x - viewModel.u.x) / 2,
                    viewModel.u.y + (viewModel.v.y - viewModel.u.y) / 2
                ),
            text = viewModel.label,
        )
    }
}

@Composable
fun <V> UndirectedEdgeView(
    viewModel: EdgeViewModel<V>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {

        drawLine(
            start = Offset(
                viewModel.u.x.toPx() + viewModel.u.radius.toPx(),
                viewModel.u.y.toPx() + viewModel.u.radius.toPx(),
            ),
            end = Offset(
                viewModel.v.x.toPx() + viewModel.v.radius.toPx(),
                viewModel.v.y.toPx() + viewModel.v.radius.toPx(),
            ),
            color = BlackAndWhite20,
            strokeWidth = 1.3.dp.toPx()
        )

    }
    if (viewModel.labelVisible) {
        Text(
            modifier = Modifier
                .offset(
                    viewModel.u.x + (viewModel.v.x - viewModel.u.x) / 2,
                    viewModel.u.y + (viewModel.v.y - viewModel.u.y) / 2
                ),
            text = viewModel.label,
        )
    }
}