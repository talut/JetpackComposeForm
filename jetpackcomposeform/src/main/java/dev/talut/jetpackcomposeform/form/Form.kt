package dev.talut.jetpackcomposeform.form

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Rect
import dev.talut.jetpackcomposeform.formField.FormField
import kotlin.reflect.KProperty1

@Stable
class Form<T>(values: T) : FormState<T> {

    private var formFields: Map<String, FormField<*>> = emptyMap()

    private var formFieldsOrder: List<String> = emptyList()

    init {
        if (formFields.isEmpty()) {
            formFields = createFields(values)
            formFields.map {
                Log.d("Form", "Field: ${it.key}")
            }
        }
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
                        ".${field.name}",
                        isDirty = mutableStateOf(
                            (readInstanceProperty<String?>(
                                clazz,
                                field.name
                            )) != null && (readInstanceProperty<String?>(
                                clazz,
                                field.name
                            )) != ""
                        ),
                        fieldValue = mutableStateOf(readInstanceProperty<String?>(clazz, field.name)),
                        errors = mutableStateOf(emptyList()),
                        isTouched = mutableStateOf(false),
                        validator = mutableStateOf(null),
                        isValid = mutableStateOf(false),
                        hasFocus = mutableStateOf(false),
                        bounds = mutableStateOf(Rect.Zero),
                    )
                    else -> FormField(
                        ".${field.name}",
                        isDirty = mutableStateOf((readInstanceProperty<Any?>(clazz, field.name)) != null),
                        fieldValue = mutableStateOf(readInstanceProperty(clazz, field.name)),
                        errors = mutableStateOf(emptyList()),
                        isTouched = mutableStateOf(false),
                        validator = mutableStateOf(null),
                        isValid = mutableStateOf(false),
                        hasFocus = mutableStateOf(false),
                        bounds = mutableStateOf(Rect.Zero),
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
            if (field.errors.value.isNotEmpty()) {
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
    override fun getValues(): Map<String, Any?> {
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
        val firstError = formFieldsOrder.firstOrNull { formFields[it]?.hasError == true }
        return formFields[firstError]?.bounds?.value ?: Rect.Zero
    }
}


/**
 * Read the value of a property from an instance of a class.
 * @param instance The instance of the class to read.
 * @param propertyName The name of the property to read.
 * @return The value of the property.
 */
@Suppress("UNCHECKED_CAST")
fun <IType> readInstanceProperty(instance: Any?, propertyName: String): IType {
    if (instance == null) {
        throw Exception("Instance is null")
    }
    val property = instance::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<Any, *>
    // force a invalid cast exception if incorrect type here
    return property.get(instance) as IType
}
