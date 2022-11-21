package dev.talut.jetpackcomposeform.form

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import dev.talut.jetpackcomposeform.field.Field
import kotlin.reflect.KProperty1


/**
 * Create and [remember] the [Form] based on the currently appropriate scroll
 * configuration to allow changing scroll position or observing scroll behavior.
 *
 */

@Stable
class Form<T>(values: T) {

    private var formFields: Map<String, Field<*>> = emptyMap()

    private var formFieldsOrder: List<String> = emptyList()

    init {
        if (formFields.isEmpty()) {
            formFields = createFields(values)
            formFields.map {
                Log.d("Form", "Field: ${it.key}")
            }
        }
    }


    fun getFirstErrorPosition(): Rect {
        val firstError = formFieldsOrder.firstOrNull { formFields[it]?.hasError == true }
        return formFields[firstError]?.bounds?.value ?: Rect.Zero
    }

    @Suppress("UNCHECKED_CAST")
    fun <VType> getField(name: String): Field<VType>? {
        if (!formFieldsOrder.contains(name)) {
            formFieldsOrder = formFieldsOrder + name
        }
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
                        hasFocus = mutableStateOf(false),
                        bounds = mutableStateOf(Rect.Zero),
                    )
                    else -> Field<Any>(
                        ".${field.name}",
                        isDirty = mutableStateOf((readInstanceProperty(clazz, field.name) as Any?) != null),
                        fieldValue = mutableStateOf(readInstanceProperty(clazz, field.name)),
                        error = mutableStateOf(emptyList()),
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

    fun getValues(): Map<String, Any?> {
        val values = formFields.map { (key, value) ->
            key to value.fieldValue.value
        }.toMap()
        return values
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
