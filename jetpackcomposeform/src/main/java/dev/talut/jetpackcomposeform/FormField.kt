package dev.talut.jetpackcomposeform

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import dev.talut.jetpackcomposeform.field.Field
import io.konform.validation.Validation

@Composable
inline fun <T> FormField(
    field: Field<T>?,
    modifier: Modifier = Modifier,
    validator: Validation<T>? = null,
    content: @Composable (
        field: Field<T>,
    ) -> Unit,
) {
    val f = remember(field) { field }
    if (f != null) {
        validator?.let {
            f.setValidator(it)
        }
        Column(
            modifier = Modifier
                .then(modifier)
                .onGloballyPositioned {
                }
        ) {
            content(f)
        }
    }
}


