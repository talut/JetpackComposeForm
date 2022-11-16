package dev.talut.jetpackcomposeform

import androidx.compose.ui.geometry.Rect
import java.util.*


data class FormManagerItem(
    val fieldName: String,
    val layoutPosition: Rect = Rect.Zero,
    val focusRemoved: Boolean = false,
    val errors: List<String> = emptyList(),
)

fun <T> createFormManagerList(fieldClass: T): List<FormManagerItem> {
    val formManagerList = mutableListOf<FormManagerItem>()
    fieldClass?.let { clazz ->
        clazz::class.java.declaredFields.filter {
            it.name != "\$stable"
        }.map {
            when (it.type) {
                String::class.java -> formManagerList.add(FormManagerItem(".${it.name}"))
                Boolean::class.java -> formManagerList.add(FormManagerItem(".${it.name}"))
                Int::class.java -> formManagerList.add(FormManagerItem(".${it.name}"))
                Date::class.java -> formManagerList.add(FormManagerItem(".${it.name}"))
                else -> {
                    it.type.declaredFields.filter { f ->
                        f.name != "\$stable"
                    }.map { f ->
                        formManagerList.add(FormManagerItem(".${it.name}.${f.name}"))
                    }
                }
            }
        }
    }
    return formManagerList
}

fun List<FormManagerItem>.get(fieldName: String): FormManagerItem? {
    return find { it.fieldName == fieldName }
}


fun List<FormManagerItem>.containsError(): Boolean {
    if (isEmpty()) return false
    return any { it.errors.isNotEmpty() }
}

fun List<FormManagerItem>.allFieldHasError(): Boolean {
    if (this.isEmpty()) return false
    return filter { it.hasError() }.size == this.size
}

fun List<FormManagerItem>.allFocusRemoved(): Boolean {
    if (this.isEmpty()) return false
    return filter { it.focusRemoved }.size == this.size
}

fun FormManagerItem.hasError(): Boolean {
    return errors.isNotEmpty()
}


fun List<FormManagerItem>.isFocusRemoved(vararg fields: String): Boolean {
    if (fields.isEmpty()) return false
    return fields.map { get(it) }.all { it?.focusRemoved == true }
}

fun List<FormManagerItem>.removeAllFocus(): List<FormManagerItem> {
    return this.map {
        it.copy(focusRemoved = true)
    }
}

fun List<FormManagerItem>.removeFocus(fieldName: String): List<FormManagerItem> {
    val items = this.map {
        if (it.fieldName == fieldName) {
            it.copy(focusRemoved = true)
        } else {
            it
        }
    }
    return items
}

fun List<FormManagerItem>.updatePosition(fieldName: String, position: Rect): List<FormManagerItem> {
    val items = this.map {
        if (it.fieldName == fieldName) {
            it.copy(layoutPosition = position)
        } else {
            it
        }
    }
    return items
}



