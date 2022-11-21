package dev.talut.jetpackcomposeform.field

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusState
import io.konform.validation.Validation


/**
 * Create and [remember] the [Field] based on the currently appropriate scroll
 * configuration to allow changing scroll position or observing scroll behavior.
 *
 * @param name  The name of the field
 * @param fieldValue The value of the field
 * @param isDirty The dirty state of the field
 * @param isTouched The touched state of the field
 *
 */


@Stable
class Field<T>(
    val name: String,
    val fieldValue: MutableState<T>,
    val isTouched: MutableState<Boolean>,
    val error: MutableState<List<String>>,
    val isDirty: MutableState<Boolean>,
    val hasFocus: MutableState<Boolean>,
    val isValid: MutableState<Boolean>,
    val validator: MutableState<Validation<T>?>,
) {
    val value: T
        get() = fieldValue.value


    val dirty: Boolean
        get() {
            return isDirty.value
        }

    val focused: Boolean
        get() {
            return hasFocus.value
        }

    val valid: Boolean
        get() {
            return isValid.value
        }

    val touched: Boolean
        get() {
            return isTouched.value
        }

    val hasError: Boolean
        get() {
            return error.value.isNotEmpty()
        }


    fun validate() {
        validator.value?.let {
            val result = it.validate(value)
            if (result.errors.isNotEmpty()) {
                result.errors.map { err ->
                    isValid.value = false
                    if (!error.value.contains(err.message)) {
                        error.value = error.value.plus(err.message)
                    }
                }
            } else {
                isValid.value = true
                error.value = emptyList()
            }
        }

    }

    fun onFocusChange(state: FocusState) {
        hasFocus.value = state.isFocused
        if (state.isFocused) {
            isTouched.value = true
        } else {
            if (isTouched.value) {
                this.onBlur()
            }
        }
    }

    private fun onBlur() {
        if (error.value.isEmpty()) {
            this.validate()
        }
    }

    fun setValidator(validator: Validation<T>) {
        this.validator.value = validator
    }

    fun onValueChange(value: T) {
        fieldValue.value = value
        if (!isDirty.value) {
            isDirty.value = true
        }
        if (error.value.isNotEmpty()) {
            this.validate()
        }
    }


}
