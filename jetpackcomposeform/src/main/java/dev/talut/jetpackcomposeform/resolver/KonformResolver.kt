package dev.talut.jetpackcomposeform.resolver

import android.util.Log
import io.konform.validation.Validation

class KonformResolver<T>(private val schema: T) {


    fun <VType> validate(values: VType) {
        val validation = Validation<VType> {
            schema
        }
        val result = validation.validate(values)
        Log.d("KonformResolver", "$result")
    }

}
