package dev.talut.jetpackcomposeform

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun FormField(
    modifier: Modifier = Modifier,
    field: FormManagerItem,
    onFocusRemoved: ((fieldName: String) -> Unit)? = null,
    onLayoutChange: ((fieldName: String, bounds: Rect) -> Unit)? = null,
    content: @Composable (field: FormManagerItem, onFocusEvent: (FocusState) -> Unit, error: String?) -> Unit,
) {
    var hadFocus by remember { mutableStateOf(false) }
    var isFocusRemoved by remember { mutableStateOf(false) }
    val formField by remember(field) { mutableStateOf(field) }


    val onFocusEvent: (FocusState) -> Unit = { fState ->
        if (fState.hasFocus) {
            hadFocus = true
            isFocusRemoved = false
        }
        if (!fState.hasFocus && hadFocus) {
            isFocusRemoved = true
        }
    }

    if (isFocusRemoved) {
        onFocusRemoved?.invoke(formField.fieldName)
    }

    val error = if (formField.hasError()) formField.errors.firstOrNull() else null


    Column(
        modifier = Modifier
            .then(modifier)
            .onGloballyPositioned {
                onLayoutChange?.invoke(formField.fieldName, it.boundsInParent())
            }
    ) {
        content(formField, onFocusEvent, error)
    }
}


