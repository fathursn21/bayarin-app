package id.ac.pnm.bayarin_app.ui.group

import id.ac.pnm.bayarin_app.data.model.Groups

data class GroupUiState(
    val groups: List<Groups> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)