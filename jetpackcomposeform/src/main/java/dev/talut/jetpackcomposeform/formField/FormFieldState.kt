package dev.talut.jetpackcomposeform.formField

import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Rect
import io.konform.validation.Validation


interface FormFieldState<T> {


    /**
     * Input's name being registered.
     */
    val name: String

    /**
     * Input's value.
     */
    val value: T

    /**
     * Input has value or not.
     */
    val dirty: Boolean

    /**
     * Input has focus or not.
     */
    val focused: Boolean


    /**
     * Input is valid or not.
     */
    val valid: Boolean

    /**
     * Input had focus and lost it.
     */
    val touched: Boolean


    /**
     * Input has errors or not.
     */
    val hasError: Boolean

    /**
     * Input's bound as [Rect] value related to the parent view.
     */
    val bounds: Rect


    /**
     * Input errors.
     */
    val errors: List<String>


    /**
     * A function which sends the input's value to the library.
     */
    fun onValueChange(value: T)

    /**
     * A function which sends the input's focus state to the library.
     */
    fun onFocusChange(state: FocusState)

    /**
     * A function sets bounds as [Rect] of the input.
     */
    fun setBounds(rect: Rect)

    /**
     * A function sets validation rules for the input.
     */
    fun setValidator(validator: Validation<T>)

}

