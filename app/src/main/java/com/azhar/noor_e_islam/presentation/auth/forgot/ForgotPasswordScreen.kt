package com.azhar.noor_e_islam.presentation.auth.forgot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.usecase.ForgotPasswordUseCase
import com.azhar.noor_e_islam.presentation.auth.AuthScaffold
import com.azhar.noor_e_islam.ui.theme.Gold500
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgot: ForgotPasswordUseCase,
) : ViewModel() {
    val state = MutableStateFlow(ForgotState())
    fun onEmail(v: String) = state.update { it.copy(email = v, message = null) }
    fun submit() {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true) }
            when (val r = forgot(state.value.email.trim())) {
                is Resource.Success -> state.update { it.copy(isLoading = false, message = "Reset link sent. Please check your email.") }
                is Resource.Error   -> state.update { it.copy(isLoading = false, message = r.message) }
                else -> Unit
            }
        }
    }
}

data class ForgotState(val email: String = "", val isLoading: Boolean = false, val message: String? = null)

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    AuthScaffold(
        title = "Reset your password",
        subtitle = "Enter your email and we'll send you a secure reset link",
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Remembered it?", color = Color.White.copy(alpha = 0.85f))
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.login), color = Gold500, fontWeight = FontWeight.Bold)
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
        state.message?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(20.dp))
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            GoldButton(
                text = stringResource(R.string.send_reset_link),
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
