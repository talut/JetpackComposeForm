package dev.talut.jetpackcomposeform.formField

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Rect
import io.konform.validation.Validation

@Stable
class FormField<T>(
    val name: String,
    initialErrors: List<String>,
    initialBounds: Rect,
    initialValidator: Validation<T>?,
    initialValue: T,
    initialIsTouched: Boolean,
    initialIsDirty: Boolean,
    initialHasFocus: Boolean,
    initialIsValid: Boolean
) {


    var errors by mutableStateOf(initialErrors)
    var fieldBounds by mutableStateOf(initialBounds)
    var fieldValidator by mutableStateOf(initialValidator)
    var value by mutableStateOf(initialValue)
    var isTouched by mutableStateOf(initialIsTouched)
    var isDirty by mutableStateOf(initialIsDirty)
    var hasFocus by mutableStateOf(initialHasFocus)
    var isValid by mutableStateOf(initialIsValid)

    /**
     * Validate the field value
     */
    fun validate() {
        value.let { fieldValue ->
            fieldValidator?.let {
                val result = it.validate(fieldValue)
                if (result.errors.isNotEmpty()) {
                    result.errors.map { err ->
                        isValid = false
                        if (!errors.contains(err.message)) {
                            errors = errors.plus(err.message)
                        }
                    }
                } else {
                    isValid = true
                    errors = emptyList()
                }
            }
        }
    }

    /**
     * Called when the field focus state changes.
     */
    fun onFocusChange(state: FocusState) {
        hasFocus = state.isFocused
        if (state.isFocused) {
            isTouched = true
        } else {
            if (isTouched) {
                this.onBlur()
            }
        }
    }

    /**
     * Called when the field is blurred
     */
    private fun onBlur() {
        this.validate()
    }

    /**
     * Called when the field value changes
     */
    fun onValueChange(newValue: T, validateOnChange: Boolean = false) {
        value = newValue
        isDirty = true
        if (validateOnChange) {
            validate()
        }
    }
}
