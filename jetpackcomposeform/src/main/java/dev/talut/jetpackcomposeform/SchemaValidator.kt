package dev.talut.jetpackcomposeform

import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder
import io.konform.validation.ValidationResult
import java.util.*


fun <T> ValidationResult<T>.invalidFields(): List<HashMap<String, String>> {
    return if (this.errors.isEmpty()) {
        emptyList()
    } else {
        val errorItems = mutableListOf<HashMap<String, String>>()
        this.errors.forEach { error ->
            val errorItem = HashMap<String, String>()
            errorItem["field"] = error.dataPath
            errorItem["error"] = error.message
            errorItems.add(errorItem)
        }
        return errorItems
    }
}

fun <T> ValidationResult<T>.invalidFields(formFields: List<FormManagerItem>): List<HashMap<String, String>> {
    return if (this.errors.isEmpty()) {
        emptyList()
    } else {
        val errorItems = mutableListOf<HashMap<String, String>>()
        this.errors.forEach { error ->
            val errorItem = HashMap<String, String>()
            errorItem["field"] = error.dataPath
            errorItem["error"] = error.message
            if (formFields.any { (it.fieldName == error.dataPath && it.focusRemoved) }) {
                errorItems.add(errorItem)
            }
        }
        return errorItems
    }
}

fun List<HashMap<String, String>>.reOrderBy(order: List<String>): List<HashMap<String, String>> {
    val orderedList = mutableListOf<HashMap<String, String>>()
    order.forEach { field ->
        this.forEach { item ->
            if (item["field"] == field) {
                orderedList.add(item)
            }
        }
    }
    return orderedList
}

fun List<HashMap<String, String>>.getFieldErrors(fieldName: String): MutableList<String> {
    val errorItems = mutableListOf<String>()
    this.forEach { item ->
        if (item["field"] == fieldName) {
            item["error"]?.let { errorItems.add(it) }
        }
    }
    return errorItems
}


fun ValidationBuilder<Date>.minAge(min: Int): Constraint<Date> {
    val date = Date()
    val currentCal = Calendar.getInstance(TimeZone.getDefault())
    currentCal.time = date
    val currentYear = currentCal[Calendar.YEAR]

    return addConstraint("minAge") {
        try {
            val cal = Calendar.getInstance(TimeZone.getDefault())
            cal.time = it
            val year = cal[Calendar.YEAR]
            val age = currentYear - year
            return@addConstraint age >= min
        } catch (e: Exception) {
            return@addConstraint false
        }
    }
}

fun ValidationBuilder<Date>.maxAge(max: Int): Constraint<Date> {
    val date = Date()
    val currentCal = Calendar.getInstance(TimeZone.getDefault())
    currentCal.time = date
    val currentYear = currentCal[Calendar.YEAR]

    return addConstraint("maxAge") {
        try {
            val cal = Calendar.getInstance(TimeZone.getDefault())
            cal.time = it
            val year = cal[Calendar.YEAR]
            return@addConstraint currentYear - year <= max
        } catch (e: Exception) {
            return@addConstraint false
        }
    }
}


