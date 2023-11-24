package dev.talut.jetpackcomposeform

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import dev.talut.jetpackcomposeform.formField.FormField
import io.konform.validation.Validation

@Composable
fun <VType> Field(
    field: FormField<VType>,
    modifier: Modifier = Modifier,
    onBlur: () -> Unit = {},
    onFocus: () -> Unit = {},
    validator: Validation<VType>? = null,
    content: @Composable (FormField<VType>) -> Unit
) {
    LaunchedEffect(validator) {
        validator?.let { field.fieldValidator = it }
    }
    val onBlurred = !field.hasFocus && field.isTouched
    LaunchedEffect(onBlurred) {
        if (onBlurred) {
            onBlur()
        }
    }

    LaunchedEffect(field.hasFocus) {
        if (field.hasFocus) {
            onFocus()
        }
    }


    Column(
        modifier = Modifier
            .then(modifier)
            .onGloballyPositioned { fieldPosition ->
                if (field.fieldBounds.isEmpty) {
                    field.fieldBounds = fieldPosition.boundsInParent()
                }
            }
    ) {
        content(field)
    }
}

