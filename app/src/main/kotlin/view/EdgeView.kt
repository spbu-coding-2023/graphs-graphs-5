package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import viewmodel.EdgeViewModel
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun <V> EdgeView(
    viewModel: EdgeViewModel<V>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (viewModel.isDirected) {
            val start = Offset(
                viewModel.u.x.toPx() + viewModel.u.radius.toPx(),
                viewModel.u.y.toPx() + viewModel.u.radius.toPx(),
            )
            val end = Offset(
                viewModel.v.x.toPx() + viewModel.v.radius.toPx(),
                viewModel.v.y.toPx() + viewModel.v.radius.toPx(),
            )
            val arrowheadSize = 15f
            val angleRadians = PI / 6
            val path = Path().apply {
                moveTo(start.x, start.y)
                lineTo(end.x, end.y)
                val angle = atan2(end.y - start.y, end.x - start.x)
                val x1 = end.x - arrowheadSize * cos(angle - angleRadians)
                val y1 = end.y - arrowheadSize * sin(angle - angleRadians)
                val x2 = end.x - arrowheadSize * cos(angle + angleRadians)
                val y2 = end.y - arrowheadSize * sin(angle + angleRadians)
                lineTo(x1.toFloat(), y1.toFloat())
                moveTo(end.x, end.y)
                lineTo(x2.toFloat(), y2.toFloat())
            }
            drawPath(
                path = path,
                /* change color when applying view and theme */
                color = Color.Black,
                style = Stroke(width = 0.8.dp.toPx())
            )
        } else {
            drawLine(
                start = Offset(
                    viewModel.u.x.toPx() + viewModel.u.radius.toPx(),
                    viewModel.u.y.toPx() + viewModel.u.radius.toPx(),
                ),
                end = Offset(
                    viewModel.v.x.toPx() + viewModel.v.radius.toPx(),
                    viewModel.v.y.toPx() + viewModel.v.radius.toPx(),
                ),
                /* change color when applying view and theme */
                color = Color.Black,
            )
        }
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