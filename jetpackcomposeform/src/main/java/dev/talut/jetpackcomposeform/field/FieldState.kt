package dev.talut.jetpackcomposeform.field

import androidx.compose.runtime.MutableState
import io.konform.validation.Validation


interface FieldState {

    /**
     * Input's name being registered.
     */
    val name: String

    /**
     * A function which sends the input's value to the library.
     */
    fun onValueChange(value: Any)

    /**
     * The current value of the controlled component.
     */
    val fieldValue: MutableState<Any?>

    /**
     * Dirty state for current controlled input.
     */
    val isDirty: MutableState<Boolean>

    /**
     * Invalid state for current input.
     */
    val validator: MutableState<Validation<Any>?>

    /**
     * Touched state for current controlled input.
     */
    val isTouched: MutableState<Boolean>

    /**
     * Error message for current controlled input.
     */
    val error: MutableList<String>

}

