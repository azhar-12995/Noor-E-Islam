package com.azhar.noor_e_islam.presentation.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.success) { if (state.success) onRegistered() }

    Box(modifier = Modifier.fillMaxSize().background(NoorGradients.EmeraldDeep)) {
        GeometricPatternBg(modifier = Modifier.fillMaxSize())
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.register), color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).padding(24.dp).verticalScroll(rememberScrollState())
            ) {
                IslamicCard(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = state.name, onValueChange = viewModel::onName,
                        label = { Text(stringResource(R.string.full_name)) },
                        singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = state.email, onValueChange = viewModel::onEmail,
                        label = { Text(stringResource(R.string.email)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = state.password, onValueChange = viewModel::onPassword,
                        label = { Text(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = state.confirmPassword, onValueChange = viewModel::onConfirm,
                        label = { Text(stringResource(R.string.confirm_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true, modifier = Modifier.fillMaxWidth())
                    state.errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(16.dp))
                    if (state.isLoading)
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    else
                        GoldButton(stringResource(R.string.register), onClick = viewModel::submit, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

