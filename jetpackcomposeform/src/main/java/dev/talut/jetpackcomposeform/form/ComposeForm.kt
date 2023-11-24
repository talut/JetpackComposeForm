package dev.talut.jetpackcomposeform.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import dev.talut.jetpackcomposeform.formField.FormField
import kotlin.reflect.KProperty1

@Stable
class ComposeForm<T>(values: T) : ComposeFormState<T> {

    private var formFields: Map<String, FormField<*>> = mutableMapOf()

    private var formFieldsOrder: List<String> = emptyList()

    init {
        formFields = createFields(values).toMutableMap()
    }

    /**
     * Get a [FormField] by its name
     *
     * @param propertyName The name of the field
     * @return The [FormField] with the given name
     */
    @Suppress("UNCHECKED_CAST")
    override fun <VType> getField(propertyName: String): FormField<VType> {
        if (!formFieldsOrder.contains(propertyName)) {
            formFieldsOrder = formFieldsOrder + propertyName
        }
        if (formFields[propertyName] == null) {
            throw IllegalArgumentException("Field $propertyName not found")
        }
        try {
            return formFields[propertyName] as FormField<VType>
        } catch (e: Exception) {
            throw IllegalArgumentException("Field $propertyName is not correct type for cast")
        }
    }


    /**
     * Create a map of fields from the given values
     */
    private fun <T> createFields(data: T): Map<String, FormField<Any?>> {
        val items = data?.let { clazz ->
            clazz::class.java.declaredFields.filter {
                it.name != "\$stable"
            }.map { field ->
                try {
                    FormField(
                        field.name,
                        initialIsDirty = (readInstanceProperty(
                            clazz,
                            field.name
                        )) != null,
                        initialValue = readInstanceProperty(
                            clazz,
                            field.name
                        ),
                        initialIsTouched = false,
                        initialIsValid = false,
                        initialHasFocus = false,
                        initialBounds = Rect.Zero,
                        initialErrors = emptyList(),
                        initialValidator = null,
                    )
                } catch (e: Exception) {
                    throw Exception("Field ${field.name} is not correct type for cast")
                }
            }
        }
        return items?.associateBy { it.name } ?: emptyMap()
    }


    /** validate
     * @return true if all fields are valid
     */
    override fun validate(): Boolean {
        formFields.forEach { (_, field) ->
            field.errors = emptyList()
            if (field.fieldValidator == null) {
                field.isValid = true
            } else {
                field.validate()
                if (field.errors.isNotEmpty()) {
                    field.isValid = false
                }
            }
        }
        return formFields.all { (_, field) ->
            field.isValid
        }
    }

    /**
     * Read the values of the properties of the given instance.
     *
     * @return The value of the property.
     */
    override fun getValues(): Map<String, *> {
        val values = formFields.map { (key, value) ->
            key to value.value
        }.toMap()
        return values
    }


    /**
     * Read the value of a property from an instance of a class.
     *
     * @param propertyName The name of the property to read.
     * @return The value of the property.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <VType> getValue(propertyName: String): VType {
        formFields[propertyName]?.let {
            return it.value as VType
        } ?: run {
            throw Exception("Field $propertyName not found")
        }
    }

    /**
     * Get the first field with error
     *
     * @return The bound as [Rect] of the field
     */
    override fun getFirstErrorBounds(): Rect {
        val fieldKey =
            formFieldsOrder.firstOrNull { formFields[it]?.errors?.isNotEmpty() == true }
        return formFields[fieldKey]?.fieldBounds ?: Rect.Zero
    }

    override fun handleSubmit(onSubmit: (Map<String, *>) -> Unit): Boolean {
        val isValid = validate()
        if (isValid) {
            onSubmit(getValues())
        }
        return isValid
    }

    override fun handleValidation(errors: Map<String, String>): Boolean {
        errors.forEach { (key, value) ->
            formFields[key]?.errors = formFields[key]?.errors?.plus(value) ?: emptyList()
        }
        return formFields.all { (_, field) ->
            field.errors.isEmpty()
        }
    }

}


/**
 * Create a [ComposeForm] and remember it
 * @param initial The initial value of the form
 * @return The [ComposeForm] object
 */
@Composable
fun <VType> rememberForm(initial: VType): ComposeForm<VType> {
    return remember(initial) {
        ComposeForm(initial)
    }
}


/**
 * Read the value of a property from an instance of a class.
 * @param instance The instance of the class to read.
 * @param propertyName The name of the property to read.
 * @return The value of the property.
 */
@Suppress("UNCHECKED_CAST")
private fun readInstanceProperty(instance: Any?, propertyName: String): Any? {
    if (instance == null) {
        throw IllegalArgumentException("Instance cannot be null")
    }
    val property = instance::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<Any, *>
    // force a invalid cast exception if incorrect type here
    return property.get(instance)
}
