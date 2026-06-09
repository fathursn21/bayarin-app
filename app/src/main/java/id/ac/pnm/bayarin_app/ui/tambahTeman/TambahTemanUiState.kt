package id.ac.pnm.bayarin_app.ui.tambahTeman

import id.ac.pnm.bayarin_app.data.model.Users

data class TambahTemanUiState(
    val searchQuery: String = "",
    val searchResults: List<Users> = emptyList(),
    val friendIds: Set<String> = emptySet(),
    val pendingIds: Set<String> = emptySet(),
    val isSearching: Boolean = false,
    val isLoadingFriends: Boolean = false,
    val isAddSuccess: Boolean = false,
    val successMessage: String = "",
    val isErrorSearch: Boolean = false,
    val isErrorAddFriend: Boolean = false,
    val errorMessage: String = "",
)
