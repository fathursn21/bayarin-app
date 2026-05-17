package id.ac.pnm.bayarin_app.ui.auth.login

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.R
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterInput
import id.ac.pnm.bayarin_app.ui.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
){
    val loginUiState by loginViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.app_name),
            style = typography.titleLarge,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding),
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LoginInput(
                userTypeUsername = loginViewModel.userTypeUsername,
                onUserUsernameChanged = { loginViewModel.updateTypeUsername(it) },
                isInputUsernameEmpty = loginUiState.isInputUsernameEmpty,
                userTypePassword = loginViewModel.userTypePassword,
                onUserPasswordChanged = { loginViewModel.updateTypePassword(it) },
                isInputPasswordEmpty = loginUiState.isInputPasswordEmpty,
                passwordVisible = loginUiState.passwordVisible,
                loginViewModel = loginViewModel,
            )

            Text(
                text = "Belum punya akun?",
                fontSize = 16.sp
            )

            TextButton(
                onClick = {
                    navController.navigate(Routes.REGISTER)
                }
            ) {
                Text("Daftar Sekarang")
            }

        }

    }
}

@Composable
fun LoginInput(
    userTypeUsername : String,
    onUserUsernameChanged : (String) -> Unit,
    isInputUsernameEmpty : Boolean,
    userTypePassword : String,
    onUserPasswordChanged : (String) -> Unit,
    isInputPasswordEmpty : Boolean,
    passwordVisible : Boolean,
    loginViewModel: LoginViewModel
){
    OutlinedTextField(
        value = userTypeUsername,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surface,
        ),
        onValueChange = onUserUsernameChanged,
        label = {
            if (isInputUsernameEmpty) {
                Text("Inputan username / Email kosong")
            } else {
                Text("Username / Email")
            }
        } ,
        isError = isInputUsernameEmpty,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {  }
        )
    )

    OutlinedTextField(
        value = userTypePassword,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surface,
        ),
        onValueChange = onUserPasswordChanged,
        label = {
            if (isInputPasswordEmpty) {
                Text("Inputan password kosong")
            } else {
                Text("Password")
            }
        } ,
        isError = isInputPasswordEmpty,
        visualTransformation =
            if (passwordVisible){
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        ,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {  }
        ),
        trailingIcon = {
            val icon = if (passwordVisible){
                Icons.Default.Favorite
            } else {
                Icons.Default.FavoriteBorder
            }

            IconButton(
                onClick = { loginViewModel.updateVisiblePassword(!passwordVisible) }
            ) {
                Icon(imageVector = icon, contentDescription = "Visibility")
            }
        }
    )

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { loginViewModel.loginUser(userTypeUsername, userTypePassword) }
    ) {
        Text(
            text = stringResource(R.string.login_button),
            fontSize = 16.sp
        )
    }

}