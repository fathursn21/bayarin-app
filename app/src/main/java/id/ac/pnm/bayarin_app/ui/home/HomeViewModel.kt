package id.ac.pnm.bayarin_app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.pnm.bayarin_app.ContextApplication
import id.ac.pnm.bayarin_app.data.AppDatabase
import id.ac.pnm.bayarin_app.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Calendar

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val roomDb = AppDatabase.getDatabase(ContextApplication.instance)
    private val repository = NotesRepository(
        roomDb.notesDao()
    )

    init {
        observeNotes()
    }

    private fun observeNotes() {
        repository.getAllNotes(0)
            .onEach { notes ->

                Log.d("SUMMARY", "total notes = ${notes.size}")

                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)

                val monthlyNotes = notes.filter { note ->

                    val noteCalendar = Calendar.getInstance()
                    noteCalendar.timeInMillis = note.date

                    noteCalendar.get(Calendar.MONTH) == currentMonth &&
                            noteCalendar.get(Calendar.YEAR) == currentYear
                }

                val income = monthlyNotes
                    .filter { !it.expense }
                    .sumOf { it.nominal }

                val expense = monthlyNotes
                    .filter { it.expense }
                    .sumOf { it.nominal }

                _uiState.value = HomeUiState(
                    income = income,
                    expense = expense
                )
            }
            .launchIn(viewModelScope)
    }
}