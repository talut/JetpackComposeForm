package dev.talut.jetpackcomposeform

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned

private typealias FormFieldGroupContent = @Composable (register: (field: FormManagerItem) -> FormManagerItem, error: String?) -> Unit

@Composable
fun FormFieldGroup(
    modifier: Modifier = Modifier,
    field: FormManagerItem,
    onFocusRemoved: ((fieldName: String) -> Unit)? = null,
    onLayoutChange: ((fieldName: String, bounds: Rect) -> Unit)? = null,
    onError: (field: FormManagerItem, groupFields: List<FormManagerItem>) -> String?,
    content: FormFieldGroupContent,
) {
    var groupFields by remember { mutableStateOf(emptyList<FormManagerItem>()) }

    val register: (registeredField: FormManagerItem) -> FormManagerItem = { registeredField ->
        groupFields.find { it.fieldName == registeredField.fieldName }?.let {
            groupFields = groupFields.map { if (it.fieldName == registeredField.fieldName) registeredField else it }
        } ?: run {
            groupFields = groupFields + registeredField
        }
        registeredField
    }

    groupFields.map {
        if (it.focusRemoved) {
            onFocusRemoved?.invoke(field.fieldName)
        }
    }

    FormFieldLayout(
        modifier = Modifier
            .then(modifier)
            .onGloballyPositioned {
                onLayoutChange?.invoke(field.fieldName, it.boundsInParent())
            },
    ) {
        content(register, onError(field, groupFields))
    }

}


