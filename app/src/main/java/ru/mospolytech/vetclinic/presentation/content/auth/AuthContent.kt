package ru.mospolytech.vetclinic.presentation.content.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mospolytech.vetclinic.presentation.theme.VetClinicTheme

@Composable
fun AuthContent(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    val minHeight = 68.dp

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Авторизация",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(AuthViewModel.Event.EmailChanged(it)) },
                label = { Text("Email") },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(AuthViewModel.Event.PasswordChanged(it)) },
                label = { Text("Пароль") },
                shape = MaterialTheme.shapes.medium,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .padding(top = 8.dp)
            )

            Button(
                onClick = { viewModel.onEvent(AuthViewModel.Event.AuthButtonClick) },
                enabled = !state.isLoading,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .padding(top = 12.dp)
            ) {
                AnimatedContent(
                    targetState = state.isLoading
                ) {
                    if (it) {
                        CircularProgressIndicator()
                    } else {
                        Text("Войти")
                    }
                }
            }

            AnimatedVisibility(
                visible = state.error != null,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthContentPreview() {
    VetClinicTheme {
        AuthContent(
            viewModel = object : AuthViewModel() {
                override val state: StateFlow<State> = MutableStateFlow(
                    State(
                        email = "test@example.com",
                        password = "password",
                        isLoading = false,
                        error = null
                    )
                )

                override fun onEvent(event: Event) {}
            },
        )
    }
}