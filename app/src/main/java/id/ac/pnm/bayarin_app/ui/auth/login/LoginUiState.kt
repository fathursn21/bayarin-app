package id.ac.pnm.bayarin_app.ui.auth.login

data class LoginUiState(
    val isInputUsernameEmpty : Boolean = false,
    val isInputPasswordEmpty : Boolean = false,
    val passwordVisible : Boolean = false,
    val isError : Boolean = false,
    val errorMessage : String = "",
)
