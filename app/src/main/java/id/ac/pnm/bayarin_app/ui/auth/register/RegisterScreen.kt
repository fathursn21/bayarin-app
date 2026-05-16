package id.ac.pnm.bayarin_app.ui.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.R

@Composable

fun RegisterScreen(
    navController: NavController,
    registerViewModel : RegisterViewModel = viewModel()
){
    val registerUiState by registerViewModel.uiState.collectAsState()
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

            RegisterInput(
                userTypeName = registerViewModel.userTypeName,
                onUserNameChanged = { registerViewModel.updateTypeName(it) },
                isInputNameEmpty = registerUiState.isInputNameEmpty,
                userTypeEmail = registerViewModel.userTypeEmail,
                onUserEmailChanged = { registerViewModel.updateTypeEmail(it) },
                isInputEmailEmpty = registerUiState.isInputEmailEmpty,
                userTypeTelp = registerViewModel.userTypeTelp,
                onUserTelpChanged = { registerViewModel.updateTypeTelp(it) },
                isInputTelpEmpty = registerUiState.IsInputTelpEmpty,
                userTypePassword = registerViewModel.userTypePassword,
                onUserPasswordChanged = { registerViewModel.updateTypePassword(it) } ,
                isInputPasswordEmpty = registerUiState.IsInputPasswordEmpty,
                registerViewModel = registerViewModel
            )

        }

    }
}

@Composable
fun RegisterInput(
    userTypeName : String,
    onUserNameChanged: (String) -> Unit,
    isInputNameEmpty : Boolean,
    userTypeEmail : String,
    onUserEmailChanged: (String) -> Unit,
    isInputEmailEmpty : Boolean,
    userTypeTelp : String,
    onUserTelpChanged: (String) -> Unit,
    isInputTelpEmpty : Boolean,
    userTypePassword : String,
    onUserPasswordChanged: (String) -> Unit,
    isInputPasswordEmpty : Boolean,
    registerViewModel: RegisterViewModel,
){
    OutlinedTextField(
        value = userTypeName,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surface,
        ),
        onValueChange = onUserNameChanged,
        label = {
            if (isInputNameEmpty) {
                Text("Inputan nama kosong")
            } else {
                Text("Nama")
            }
        } ,
        isError = isInputNameEmpty,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {  }
        )
    )

    OutlinedTextField(
        value = userTypeEmail,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surface,
        ),
        onValueChange = onUserEmailChanged,
        label = {
            if (isInputEmailEmpty) {
                Text("Inputan email kosong")
            } else {
                Text("Email")
            }
        } ,
        isError = isInputEmailEmpty,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {  }
        )
    )

    OutlinedTextField(
        value = userTypeTelp,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surface,
        ),
        onValueChange = onUserTelpChanged,
        label = {
            if (isInputTelpEmpty) {
                Text("Inputan Nomor Telephone kosong")
            } else {
                Text("Nomor Telephone")
            }
        } ,
        isError = isInputTelpEmpty,
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
            if (isInputTelpEmpty) {
                Text("Inputan Password kosong")
            } else {
                Text("Password")
            }
        } ,
        isError = isInputPasswordEmpty,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {  }
        )
    )

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { registerViewModel.regiterUser(userTypeName, userTypeEmail, userTypeTelp, userTypePassword) }
    ) {
        Text(
            text = stringResource(R.string.register_button),
            fontSize = 16.sp
        )
    }

}