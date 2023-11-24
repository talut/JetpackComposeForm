package dev.talut.jetpackcomposeform

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dev.talut.jetpackcomposeform.form.rememberForm
import dev.talut.jetpackcomposeform.formField.Field
import dev.talut.jetpackcomposeform.ui.theme.JetpackcomposeformTheme
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.minimum
import io.konform.validation.jsonschema.pattern
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val focusManager = LocalFocusManager.current
            val interactionSource = remember { MutableInteractionSource() }
            JetpackcomposeformTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                focusManager.clearFocus(true)
                            },
                            indication = null,
                            interactionSource = interactionSource
                        ),
                    color = MaterialTheme.colors.background
                ) {
                    FormTest()
                }
            }
        }
    }
}


@Composable
fun FormTest() {
    val form = rememberForm(TestForm())
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Log.d("Form", "Form RENDERED")

        Field<String?>(
            field = form.getField("buildingName"),
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value ?: "",
                onValueChange = field::onValueChange,
            )
            Text(text = "Name: ${field.value}")
            Text(text = "Name has focus: ${field.hasFocus}")
            Text(text = "Name is valid: ${field.isValid}")
            Text(text = "Name is dirty: ${field.isDirty}")
            Text(text = "Name is touched: ${field.isTouched}")
        }

        Field<String>(
            field = form.getField("buildingNumber"),
            onBlur = {
                Log.d("Form", "Building number blurred")
            },
            onFocus = {
                Log.d("Form", "Building number focused")
            },
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.errors.isNotEmpty(),
                onValueChange = field::onValueChange,
            )
            Text(text = "Number: ${field.value}")
            Text(text = "Number has focus: ${field.hasFocus}")
            Text(text = "Number has errors: ${field.errors}")
            Text(text = "Number is valid: ${field.isValid}")
            Text(text = "Number is dirty: ${field.isDirty}")
            Text(text = "Number is touched: ${field.isTouched}")
        }

        Field<Int?>(
            field = form.getField("age"),
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value?.takeIf { it > 0 }?.toString() ?: "",
                isError = field.errors.isNotEmpty(),
                onValueChange = { value ->
                    field.onValueChange(value.toIntOrNull() ?: 0)
                },
            )

            Text(text = "Age: ${field.value}")
            Text(text = "Age has focus: ${field.hasFocus}")
            Text(text = "Age has errors: ${field.errors}")
            Text(text = "Age is valid: ${field.isValid}")
            Text(text = "Age is dirty: ${field.isDirty}")
            Text(text = "Age is touched: ${field.isTouched}")
        }

        Field(
            field = form.getField("sendEmail"),
        ) { field ->
            Switch(
                checked = field.value,
                onCheckedChange = field::onValueChange,
            )

            Text(text = "Send Email: ${field.value}")
        }

        Spacer(modifier = Modifier.height(1000.dp))

        Button(onClick = {
            form.handleSubmit { formValues ->
                val values = Gson().fromJson(Gson().toJson(formValues), TestForm::class.java)
                val validationResult: Map<String, String> = validate(values).errors.associate {
                    it.dataPath.split(".").last() to it.message
                }
                val isValid = form.handleValidation(validationResult)

                if (isValid) {
                    Log.d("Form", values.toString())
                } else {
                    coroutineScope.launch {
                        val firstInvalidField = form.getFirstErrorBounds().top
                        scrollState.animateScrollTo(firstInvalidField.toInt())
                    }
                }
            }
        }) {
            Text(text = "Submit")
        }
    }
}

data class TestForm(
    @SerializedName("buildingName")
    val buildingName: String? = null,
    @SerializedName("buildingNumber")
    val buildingNumber: String = "",
    @SerializedName("age")
    val age: Number? = null,
    @SerializedName("sendEmail")
    val sendEmail: Boolean = false,
)

val validate = Validation {
    TestForm::age required {
        minimum(18)
    }
    TestForm::buildingName ifPresent {
        minLength(2)
        pattern("[a-zA-Z]+")
    }
    TestForm::buildingNumber required {
        minLength(2)
    }
}

