package id.ac.pnm.bayarin_app.ui.tambahGroup

import id.ac.pnm.bayarin_app.data.model.Users

data class TambahGroupUiState(
    val allFriends: List<Users> = emptyList(),
    val filteredFriends: List<Users> = emptyList(),
    val selectedFriends: List<Users> = emptyList(),
    val isLoadingFriends: Boolean = false,
    val isCreatingGroup: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = ""
)