package dev.talut.jetpackcomposeform.form

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import dev.talut.jetpackcomposeform.formField.FormField
import io.konform.validation.Validation
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
    private fun <T> createFields(data: T): Map<String, FormField<*>> {
        val items = data?.let { clazz ->
            clazz::class.java.declaredFields.filter {
                it.name != "\$stable"
            }.map { field ->
                when (field.type) {
                    String::class.java -> FormField(
                        field.name,
                        initialIsDirty = (readInstanceProperty<String?>(
                            clazz,
                            field.name
                        )) != null && (readInstanceProperty<String?>(
                            clazz,
                            field.name
                        )) != "",
                        initialValue = readInstanceProperty<String?>(
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

                    Int::class.java -> FormField(
                        field.name,
                        initialIsDirty = (readInstanceProperty<Int?>(
                            clazz,
                            field.name
                        )) != null && (readInstanceProperty<Int?>(
                            clazz,
                            field.name
                        )) != 0,
                        initialValue = readInstanceProperty<Int?>(
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

                    else -> FormField(
                        field.name,
                        initialValue = readInstanceProperty(clazz, field.name),
                        initialIsDirty = (readInstanceProperty<Any?>(clazz, field.name)) != null,
                        initialIsTouched = false,
                        initialIsValid = false,
                        initialHasFocus = false,
                        initialBounds = Rect.Zero,
                        initialErrors = emptyList(),
                        initialValidator = null,
                    )
                }
            }
        }
        return items?.associateBy { it.name } ?: emptyMap()
    }


    /** validate
     * @return true if all fields are valid
     */
    override fun validate(): Boolean {
        var isValid = true
        formFields.forEach { (_, field) ->
            field.validate()
            if (field.errors.isNotEmpty()) {
                isValid = false
            }
        }
        return isValid
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
        val firstError =
            formFieldsOrder.firstOrNull { formFields[it]?.errors?.isNotEmpty() == true }
        return formFields[firstError]?.fieldBounds ?: Rect.Zero
    }

    override fun handleSubmit(onSubmit: (Map<String, *>) -> Unit): Boolean {
        val isValid = validate()
        if (isValid) {
            onSubmit(getValues())
        }
        return isValid
    }

}


/**
 * Read the value of a property from an instance of a class.
 * @param instance The instance of the class to read.
 * @param propertyName The name of the property to read.
 * @return The value of the property.
 */
@Suppress("UNCHECKED_CAST")
private fun <IType> readInstanceProperty(instance: Any?, propertyName: String): IType {
    if (instance == null) {
        throw Exception("Instance is null")
    }
    val property = instance::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<Any, *>
    // force a invalid cast exception if incorrect type here
    return property.get(instance) as IType
}
