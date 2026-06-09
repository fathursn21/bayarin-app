package id.ac.pnm.bayarin_app.ui.daftarTeman

import id.ac.pnm.bayarin_app.data.model.Users

data class DaftarTemanUiState(
    val friendsList: List<Users> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)