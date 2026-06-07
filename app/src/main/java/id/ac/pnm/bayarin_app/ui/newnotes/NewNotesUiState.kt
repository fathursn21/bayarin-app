package id.ac.pnm.bayarin_app.ui.newnotes

import id.ac.pnm.bayarin_app.data.model.Notes

data class NewNotesUiState(
    val isInputNominalEmpty : Boolean = false,
    val isInputCategoryEmpty : Boolean = false,
    val isInputDateEmpty : Boolean = false,
    val isSuccessfully : Boolean = false,
    val error : String = "",
    val notes: List<Notes> = emptyList(),
    val isLoading : Boolean = false,
)
