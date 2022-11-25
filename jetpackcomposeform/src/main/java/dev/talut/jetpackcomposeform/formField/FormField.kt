package dev.talut.jetpackcomposeform.formField

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Rect
import io.konform.validation.Validation

@Stable
class FormField<T>(
    override val name: String,
    override val errors: MutableState<List<String>>,
    override val bounds: MutableState<Rect>,
    override val validator: MutableState<Validation<T>?>,
    private val fieldValue: MutableState<T>,
    private val isTouched: MutableState<Boolean>,
    private val isDirty: MutableState<Boolean>,
    private val hasFocus: MutableState<Boolean>,
    private val isValid: MutableState<Boolean>,
) : FormFieldState<T> {

    override val value: T
        get() = fieldValue.value


    override val dirty: Boolean
        get() {
            return isDirty.value
        }

    override val focused: Boolean
        get() {
            return hasFocus.value
        }

    override val valid: Boolean
        get() {
            return isValid.value
        }

    override val touched: Boolean
        get() {
            return isTouched.value
        }

    override val hasError: Boolean
        get() {
            return errors.value.isNotEmpty()
        }

    /**
     * Set bound as [Rect] of the field
     */
    override fun setBounds(rect: Rect) {
        bounds.value = rect
    }

    /**
     * Validate the field value
     */
    fun validate() {
        validator.value?.let {
            val result = it.validate(value)
            if (result.errors.isNotEmpty()) {
                result.errors.map { err ->
                    isValid.value = false
                    if (!errors.value.contains(err.message)) {
                        errors.value = errors.value.plus(err.message)
                    }
                }
            } else {
                isValid.value = true
                errors.value = emptyList()
            }
        }

    }

    /**
     * Called when the field focus state changes.
     */
    override fun onFocusChange(state: FocusState) {
        hasFocus.value = state.isFocused
        if (state.isFocused) {
            isTouched.value = true
        } else {
            if (isTouched.value) {
                this.onBlur()
            }
        }
    }

    /**
     * Called when the field is blurred
     */
    private fun onBlur() {
        if (errors.value.isEmpty()) {
            this.validate()
        }
    }

    /**
     * Sets validation for the field
     */
    override fun setValidator(validator: Validation<T>) {
        this.validator.value = validator
    }

    /**
     * Called when the field value changes
     */
    override fun onValueChange(value: T) {
        fieldValue.value = value
        if (!isDirty.value) {
            isDirty.value = true
        }
        if (errors.value.isNotEmpty()) {
            this.validate()
        }
    }


}
