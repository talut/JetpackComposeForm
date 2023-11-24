package dev.talut.jetpackcomposeform.form

import androidx.compose.ui.geometry.Rect
import dev.talut.jetpackcomposeform.formField.FormField

interface ComposeFormState<T> {

    /**
     * Get a [FormField] by its name
     *
     * @param propertyName The name of the field
     * @return The [FormField] with the given name
     */
    fun <VType> getField(propertyName: String): FormField<VType>

    /** validate
     * @return true if all fields are valid
     */
    fun validate(): Boolean

    /**
     * Read the values of the properties of the given instance.
     *
     * @return The value of the property.
     */
    fun getValues(): Map<String, *>


    /**
     * Read the value of a property from an instance of a class.
     *
     * @param propertyName The name of the property to read.
     * @return The value of the property.
     */
    fun <VType> getValue(propertyName: String): VType


    /**
     * Get the first field with error
     *
     * @return The bound as [Rect] of the field
     */
    fun getFirstErrorBounds(): Rect


    /**
     * Submit the form
     * @param onSubmit The callback to be called when the form is submitted
     * @return true if the form is valid
     */
    fun handleSubmit(onSubmit: (Map<String, *>) -> Unit): Boolean

    /**
     * Handle the validation of the form
     * @param errors The errors map
     * @return true if the form is valid
     */
    fun handleValidation(errors: Map<String, String>): Boolean
}


