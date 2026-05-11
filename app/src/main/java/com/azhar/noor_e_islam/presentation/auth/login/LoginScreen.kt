package com.azhar.noor_e_islam.presentation.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.security.AdminConfig
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.presentation.auth.AuthScaffold
import com.azhar.noor_e_islam.ui.theme.Gold500

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit,
    onAdminLoggedIn: () -> Unit = onLoggedIn,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var passVisible by remember { mutableStateOf(false) }
    LaunchedEffect(state.success) {
        if (state.success) {
            // Email entered into the form is the source of truth: the admin
            // is whoever signs in with the well-known admin address.
            if (AdminConfig.isAdminEmail(state.email)) onAdminLoggedIn() else onLoggedIn()
        }
    }

    AuthScaffold(
        title = "Assalāmu ʿAlaykum",
        subtitle = "Sign in to continue your journey",
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.dont_have_account), color = Color.White.copy(alpha = 0.85f))
                TextButton(onClick = onRegister) {
                    Text(stringResource(R.string.register), color = Gold500, fontWeight = FontWeight.Bold)
                }
            }
        },
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmail,
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(Icons.Filled.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPassword,
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(Icons.Filled.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { passVisible = !passVisible }) {
                    Icon(if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                }
            },
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = onForgot, modifier = Modifier.align(Alignment.End)) {
            Text(stringResource(R.string.forgot_password), color = Gold500, fontWeight = FontWeight.SemiBold)
        }

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            GoldButton(
                text = stringResource(R.string.login),
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
