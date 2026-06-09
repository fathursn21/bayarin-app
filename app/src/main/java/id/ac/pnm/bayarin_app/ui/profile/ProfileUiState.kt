package id.ac.pnm.bayarin_app.ui.profile

import id.ac.pnm.bayarin_app.data.model.Users

data class ProfileUiState(
    val isLogout : Boolean = false,
    val user: Users = Users()
)
