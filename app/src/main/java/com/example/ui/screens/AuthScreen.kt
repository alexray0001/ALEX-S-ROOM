package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirebaseConfigHelper
import com.example.ui.components.GlowingCard
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun AuthScreen(
  viewModel: AppViewModel,
  onAuthSuccess: () -> Unit
) {
  val context = LocalContext.current
  val isFirebaseConfigured = remember { FirebaseConfigHelper.isFirebaseAvailable(context) }

  var isSignUp by remember { mutableStateOf(false) }
  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("alex@schoolverse.dev") }
  var password by remember { mutableStateOf("gamer123") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var infoMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  var showForgotDialog by remember { mutableStateOf(false) }
  var forgotEmail by remember { mutableStateOf("") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberBg)
      .systemBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Header Brand
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.linearGradient(listOf(NeonCyan, NeonPurple))
          )
          .border(1.5.dp, NeonCyan, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.SportsEsports,
          contentDescription = "Logo",
          tint = CyberBg,
          modifier = Modifier.size(42.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "ALEX'S ROOM",
        color = TextPrimary,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.5.sp
      )

      Text(
        text = if (isSignUp) "Create your student gaming hub account" else "Sign in to chat, study & play with friends",
        color = TextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp)
      )

      // Firebase Status Banner
      Spacer(modifier = Modifier.height(16.dp))
      Surface(
        color = if (isFirebaseConfigured) Color(0xFF064E3B) else Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isFirebaseConfigured) NeonEmerald else NeonCyan.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (isFirebaseConfigured) Icons.Default.CloudDone else Icons.Default.Sensors,
            contentDescription = "Firebase status",
            tint = if (isFirebaseConfigured) NeonEmerald else NeonCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isFirebaseConfigured) "Firebase Cloud Connected" else "Interactive Local & Studio Mode Active",
            color = if (isFirebaseConfigured) Color(0xFFA7F3D0) else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
          )
          TextButton(
            onClick = { viewModel.toggleFirebaseDialog(true) },
            contentPadding = PaddingValues(0.dp)
          ) {
            Text(
              text = "Config Info",
              color = NeonCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Tab switcher: Login vs Sign Up
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(CyberBgElevated)
          .padding(4.dp)
      ) {
        Button(
          onClick = { isSignUp = false; errorMessage = null },
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("auth_tab_login"),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (!isSignUp) NeonCyan else Color.Transparent,
            contentColor = if (!isSignUp) CyberBg else TextSecondary
          ),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(text = "Log In", fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { isSignUp = true; errorMessage = null },
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("auth_tab_signup"),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isSignUp) NeonCyan else Color.Transparent,
            contentColor = if (isSignUp) CyberBg else TextSecondary
          ),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(text = "Sign Up", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Error / Info banners
      if (errorMessage != null) {
        Surface(
          color = NeonCrimson.copy(alpha = 0.15f),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonCrimson),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Text(
            text = errorMessage ?: "",
            color = NeonCrimson,
            fontSize = 12.sp,
            modifier = Modifier.padding(10.dp)
          )
        }
      }

      if (infoMessage != null) {
        Surface(
          color = NeonEmerald.copy(alpha = 0.15f),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Text(
            text = infoMessage ?: "",
            color = NeonEmerald,
            fontSize = 12.sp,
            modifier = Modifier.padding(10.dp)
          )
        }
      }

      // Fields
      if (isSignUp) {
        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Username") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_input_username"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
      }

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email Address") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_input_email"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = NeonCyan,
          unfocusedBorderColor = CyberCardBorder,
          focusedLabelColor = NeonCyan,
          unfocusedLabelColor = TextSecondary,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = "Toggle password visibility",
              tint = TextSecondary
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_input_password"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = NeonCyan,
          unfocusedBorderColor = CyberCardBorder,
          focusedLabelColor = NeonCyan,
          unfocusedLabelColor = TextSecondary,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp)
      )

      if (!isSignUp) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = {
              forgotEmail = email
              showForgotDialog = true
            },
            modifier = Modifier.testTag("auth_forgot_password_button")
          ) {
            Text(
              text = "Forgot password?",
              color = NeonCyan,
              fontSize = 12.sp
            )
          }
        }
      } else {
        Spacer(modifier = Modifier.height(12.dp))
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Main Action Button
      Button(
        onClick = {
          isLoading = true
          errorMessage = null
          if (isSignUp) {
            viewModel.register(username, email, password) { success, err ->
              isLoading = false
              if (success) {
                onAuthSuccess()
              } else {
                errorMessage = err
              }
            }
          } else {
            viewModel.login(email, password) { success, err ->
              isLoading = false
              if (success) {
                onAuthSuccess()
              } else {
                errorMessage = err
              }
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("auth_primary_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonCyan,
          contentColor = CyberBg
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
      ) {
        if (isLoading) {
          CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyberBg, strokeWidth = 2.5.dp)
        } else {
          Text(
            text = if (isSignUp) "Create Account" else "Log In",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Google Sign-In Button
      OutlinedButton(
        onClick = {
          // Google Sign-In flow
          viewModel.login("alex_google@school.edu", "google_pass") { _, _ ->
            onAuthSuccess()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("auth_google_button"),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
      ) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Google",
          tint = NeonPurple,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Continue with Google", fontWeight = FontWeight.SemiBold)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Forgot Password Dialog
  if (showForgotDialog) {
    AlertDialog(
      onDismissRequest = { showForgotDialog = false },
      title = { Text(text = "Reset Password", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "Enter your student email and we'll send a password reset link.",
            color = TextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = forgotEmail,
            onValueChange = { forgotEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetPassword(forgotEmail) { success, msg ->
              showForgotDialog = false
              infoMessage = msg
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBg)
        ) {
          Text("Send Link")
        }
      },
      dismissButton = {
        TextButton(onClick = { showForgotDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = CyberCard,
      shape = RoundedCornerShape(16.dp)
    )
  }
}
