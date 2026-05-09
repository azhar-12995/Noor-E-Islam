package com.azhar.noor_e_islam.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.ui.components.GeometricPatternBg
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.ui.theme.NoorGradients

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit,
    onForgot: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.success) { if (state.success) onLoggedIn() }

    Box(modifier = Modifier.fillMaxSize().background(NoorGradients.EmeraldDeep)) {
        GeometricPatternBg(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displaySmall, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.app_tagline), color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(24.dp))

            IslamicCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.email, onValueChange = viewModel::onEmail,
                    label = { Text(stringResource(R.string.email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password, onValueChange = viewModel::onPassword,
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                state.errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
                if (state.isLoading) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    GoldButton(
                        text = stringResource(R.string.login),
                        onClick = viewModel::submit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onForgot, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(R.string.forgot_password))
                }
                TextButton(onClick = viewModel::guest, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(R.string.continue_as_guest))
                }
                TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(R.string.dont_have_account))
                }
            }
        }
    }
}

