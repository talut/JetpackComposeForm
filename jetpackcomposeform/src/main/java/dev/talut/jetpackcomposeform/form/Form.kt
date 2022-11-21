package dev.talut.jetpackcomposeform.form

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.talut.jetpackcomposeform.field.Field
import kotlin.reflect.KProperty1


/**
 * Create and [remember] the [Form] based on the currently appropriate scroll
 * configuration to allow changing scroll position or observing scroll behavior.
 *
 */

@Stable
class Form<T>(values: T) {

    var formFields: Map<String, Field<*>> = emptyMap()

    init {
        if (formFields.isEmpty()) {
            formFields = createFields(values)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <VType> getField(name: String): Field<VType>? {
        return formFields[name] as? Field<VType>
    }

    private fun <T> createFields(data: T): Map<String, Field<*>> {
        val items = data?.let { clazz ->
            clazz::class.java.declaredFields.filter {
                it.name != "\$stable"
            }.map { field ->
                when (field.type) {
                    String::class.java -> Field<String>(
                        ".${field.name}",
                        isDirty = mutableStateOf(
                            (readInstanceProperty(clazz, field.name) as String?) != null && (readInstanceProperty(
                                clazz,
                                field.name
                            ) as String?) != ""
                        ),
                        fieldValue = mutableStateOf(readInstanceProperty(clazz, field.name)),
                        error = mutableStateOf(emptyList()),
                        isTouched = mutableStateOf(false),
                        validator = mutableStateOf(null),
                        isValid = mutableStateOf(false),
                        hasFocus = mutableStateOf(false)
                    )
                    else -> Field<Any>(
                        ".${field.name}",
                        isDirty = mutableStateOf((readInstanceProperty(clazz, field.name) as Any?) != null),
                        fieldValue = mutableStateOf(readInstanceProperty(clazz, field.name)),
                        error = mutableStateOf(emptyList()),
                        isTouched = mutableStateOf(false),
                        validator = mutableStateOf(null),
                        isValid = mutableStateOf(false),
                        hasFocus = mutableStateOf(false)
                    )
                }
            }
        }
        return items?.associateBy { it.name } ?: emptyMap()
    }


    fun validate(): Boolean {
        var isValid = true
        formFields.forEach { (_, field) ->
            field.validate()
            if (field.error.value.isNotEmpty()) {
                isValid = false
            }
        }
        return isValid
    }


}


@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
    val property = instance::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<Any, *>
    // force a invalid cast exception if incorrect type here
    return property.get(instance) as R
}
