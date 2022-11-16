package dev.talut.jetpackcomposeform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

@Composable
fun FormFieldLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { m, c ->
        val p = m.map { measurable ->
            measurable.measure(c)
        }
        layout(c.maxWidth, c.maxHeight) {
            var yPosition = 0
            p.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}
