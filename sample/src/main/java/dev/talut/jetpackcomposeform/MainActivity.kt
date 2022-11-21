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
import androidx.compose.runtime.*
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
    val values by remember { mutableStateOf(TestForm()) }
    val formState = remember(values) { Form(values) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        FormField<String>(
            field = formState.getField(".firstName"),
            validator = Validation {
                minLength(3)
                pattern("[a-zA-Z]+")
            }
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.hasError,
                onValueChange = field::onValueChange,
            )
            Text(text = "First name: ${field.value}")
            Text(text = "First name has focus: ${field.focused}")
            Text(text = "First name has error: ${field.hasError}")
            Text(text = "First name is valid: ${field.valid}")
            Text(text = "First name is dirty: ${field.dirty}")
            Text(text = "First name is touched: ${field.touched}")
        }

        FormField<String>(
            field = formState.getField(".lastName"),
            validator = Validation {
                minLength(3)
            }
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.hasError,
                onValueChange = field::onValueChange,
            )
            Text(text = "Last name: ${field.value}")
            Text(text = "Last name has focus: ${field.focused}")
            Text(text = "Last name has error: ${field.hasError}")
            Text(text = "Last name is valid: ${field.valid}")
            Text(text = "Last name is dirty: ${field.dirty}")
            Text(text = "Last name is touched: ${field.touched}")
        }

        FormField<String>(
            field = formState.getField(".phoneNumber"),
            validator = Validation {
                minLength(3)
            }
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.hasError,
                onValueChange = field::onValueChange,
            )
            Text(text = "Phone number: ${field.value}")
            Text(text = "Phone number has focus: ${field.focused}")
            Text(text = "Phone number has error: ${field.hasError}")
            Text(text = "Phone number is valid: ${field.valid}")
            Text(text = "Phone number is dirty: ${field.dirty}")
            Text(text = "Phone number is touched: ${field.touched}")
        }

        FormField<String>(
            field = formState.getField(".email"),
            validator = Validation {
                minLength(3)
            }
        ) { field ->
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged(field::onFocusChange),
                value = field.value,
                isError = field.hasError,
                onValueChange = field::onValueChange,
            )
            Text(text = "Email: ${field.value}")
            Text(text = "Email has focus: ${field.focused}")
            Text(text = "Email has error: ${field.hasError}")
            Text(text = "Email is valid: ${field.valid}")
            Text(text = "Email is dirty: ${field.dirty}")
            Text(text = "Email is touched: ${field.touched}")
        }

        Spacer(modifier = Modifier.height(1000.dp))

        Button(onClick = {
            if (formState.validate()) {
                Log.d("Form", "Form is valid & ${formState.getValues()}")
            } else {
                coroutineScope.launch {
                    val firstInvalidField = formState.getFirstErrorPosition().top
                    scrollState.animateScrollTo(firstInvalidField.toInt())
                }
            }
        }) {
            Text(text = "Submit")
        }
    }
}

data class TestForm(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
)
