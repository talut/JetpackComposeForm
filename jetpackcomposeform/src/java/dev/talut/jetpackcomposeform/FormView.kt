package dev.talut.jetpackcomposeform

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import io.konform.validation.Validation

private typealias FormViewContent = @Composable (
    register: (fieldName: String) -> FormManagerItem,
    onFocusRemoved: (fieldName: String) -> Unit,
    onFieldLayoutChange: (fieldName: String, bounds: Rect) -> Unit,
    onSubmit: () -> Unit,
) -> Unit


@Composable
fun <T> FormView(
    values: T,
    validationSchema: Validation<T>,
    scrollToFirstError: ((fieldBounds: Rect) -> Unit)? = null,
    onSubmit: (T) -> Unit,
    content: FormViewContent,
) {

    var formFields: List<FormManagerItem> by remember { mutableStateOf(createFormManagerList(values)) }
    var fieldOrder by remember { mutableStateOf(emptyList<String>()) }
    var invalidFields by remember { mutableStateOf(emptyList<HashMap<String, String>>()) }
    var fieldCoordinates by remember { mutableStateOf<Map<String, Rect>>(emptyMap()) }

    val register: (fieldName: String) -> FormManagerItem = { fieldName ->
        if (fieldName !in fieldOrder) {
            fieldOrder = fieldOrder + fieldName
        }
        formFields.get(fieldName)?.copy(
            errors = invalidFields.getFieldErrors(fieldName),
        ) ?: throw IllegalArgumentException("Field $fieldName not found")
    }

    val onFocusRemoved: (fieldName: String) -> Unit = { fieldName ->
        invalidFields = validationSchema.validate(values).invalidFields(formFields)
        formFields = formFields.removeFocus(fieldName)
    }
    val onFieldLayoutChange: (fieldName: String, bounds: Rect) -> Unit = { fieldName, bounds ->
        fieldCoordinates = fieldCoordinates.toMutableMap().apply {
            put(fieldName, bounds)
        }
    }

    val handleSubmit = {
        invalidFields = validationSchema.validate(values).invalidFields().reOrderBy(fieldOrder)
        formFields = formFields.removeAllFocus()

        if (invalidFields.isEmpty()) {
            onSubmit(values)
        } else {
            scrollToFirstError?.let {
                if (fieldCoordinates.isNotEmpty()) {
                    val firstInvalidField = invalidFields.first()["field"]
                    val coordinate = fieldCoordinates[firstInvalidField]
                    coordinate?.let {
                        scrollToFirstError(coordinate)
                    }
                }
            }
        }
    }

    content(
        register,
        onFocusRemoved,
        onFieldLayoutChange,
        handleSubmit,
    )

}
