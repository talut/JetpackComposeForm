package dev.talut.jetpackcomposeform

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import dev.talut.jetpackcomposeform.form.Form
import dev.talut.jetpackcomposeform.ui.theme.JetpackcomposeformTheme
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minLength
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
    val formState = remember { Form(TestForm()) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Field<String>(
            field = formState.getField<String>(".buildingName"),
            validator = Validation {
                if (formState.getValue<String>(".buildingNumber").isEmpty()) {
                    minLength(1)
                    pattern("[a-zA-Z]+")
                }
            }
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.hasError,
                onValueChange = {
                    field.onValueChange(it)
                    formState.getField<String>(".buildingNumber").validate()
                },
            )
            Text(text = "Building name: ${field.value}")
            Text(text = "Building name has focus: ${field.focused}")
            Text(text = "Building name has error: ${field.hasError}")
            Text(text = "Building name is valid: ${field.valid}")
            Text(text = "Building name is dirty: ${field.dirty}")
            Text(text = "Building name is touched: ${field.touched}")
        }

        Field<String>(
            field = formState.getField<String>(".buildingNumber"),
            validator = Validation {
                if (formState.getValue<String>(".buildingName").isEmpty()) {
                    minLength(1)
                    pattern("[a-zA-Z]+")
                }
            }
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.hasError,
                onValueChange = {
                    field.onValueChange(it)
                    formState.getField<String>(".buildingName").validate()
                },
            )
            Text(text = "Building number: ${field.value}")
            Text(text = "Building number has focus: ${field.focused}")
            Text(text = "Building number has error: ${field.hasError}")
            Text(text = "Building number is valid: ${field.valid}")
            Text(text = "Building number is dirty: ${field.dirty}")
            Text(text = "Building number is touched: ${field.touched}")
        }

        Spacer(modifier = Modifier.height(1000.dp))

        Button(onClick = {
            if (formState.validate()) {
                Log.d("Form", "Form is valid & ${formState.getValues()}")
            } else {
                coroutineScope.launch {
                    val firstInvalidField = formState.getFirstErrorBounds().top
                    scrollState.animateScrollTo(firstInvalidField.toInt())
                }
            }
        }) {
            Text(text = "Submit")
        }
    }
}

data class TestForm(
    val buildingName: String = "",
    val buildingNumber: String = "",
)
