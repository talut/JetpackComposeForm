package dev.talut.jetpackcomposeform

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import dev.talut.jetpackcomposeform.formField.FormField
import io.konform.validation.Validation

@Composable
inline fun <VType> Field(
    field: FormField<VType>?,
    modifier: Modifier = Modifier,
    validator: Validation<VType>? = null,
    content: @Composable (
        field: FormField<VType>,
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
                    f.setBounds(it.boundsInParent())
                }
        ) {
            content(f)
        }
    }
}


