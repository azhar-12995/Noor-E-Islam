package com.azhar.noor_e_islam.presentation.auth.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhar.noor_e_islam.R
import com.azhar.noor_e_islam.core.ui.components.GeometricPatternBg
import com.azhar.noor_e_islam.core.ui.components.GoldButton
import com.azhar.noor_e_islam.core.ui.components.IslamicCard
import com.azhar.noor_e_islam.core.util.Resource
import com.azhar.noor_e_islam.domain.usecase.ForgotPasswordUseCase
import com.azhar.noor_e_islam.ui.theme.NoorGradients
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
                is Resource.Success -> state.update { it.copy(isLoading = false, message = "Reset link sent") }
                is Resource.Error   -> state.update { it.copy(isLoading = false, message = r.message) }
                else -> Unit
            }
        }
    }
}

data class ForgotState(val email: String = "", val isLoading: Boolean = false, val message: String? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Box(modifier = Modifier.fillMaxSize().background(NoorGradients.EmeraldDeep)) {
        GeometricPatternBg(modifier = Modifier.fillMaxSize())
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.reset_password), color = Color.White) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(24.dp)) {
                IslamicCard(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.email, onValueChange = viewModel::onEmail,
                        label = { Text(stringResource(R.string.email)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    state.message?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(16.dp))
                    if (state.isLoading)
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    else
                        GoldButton(stringResource(R.string.send_reset_link), onClick = viewModel::submit, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

