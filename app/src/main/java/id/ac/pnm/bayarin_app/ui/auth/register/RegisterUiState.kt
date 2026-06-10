package id.ac.pnm.bayarin_app.ui.auth.register

data class RegisterUiState(
    val isInputNameEmpty: Boolean = false,
    val isInputEmailEmpty: Boolean = false,
    val IsInputTelpEmpty: Boolean = false,
    val IsInputPasswordEmpty: Boolean = false,
    val passwordVisible: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)