// ui/viewmodels/BookDetailsViewModel.kt
package com.speechify.composeuichallenge.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speechify.composeuichallenge.data.Book
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val uiState: StateFlow<BookDetailsUiState> = _uiState.asStateFlow()

    init {
        // ✅ FIX: Use safe call and if-else instead of early return
        val bookId = savedStateHandle.get<String>("bookId")
        if (bookId != null) {
            loadBookDetails(bookId)
        } else {
            _uiState.value = BookDetailsUiState.Error("Book ID not found")
        }
    }

    private fun loadBookDetails(bookId: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailsUiState.Loading
            try {
                val book = repository.getBook(bookId)
                if (book != null) {
                    _uiState.value = BookDetailsUiState.Success(book)
                } else {
                    _uiState.value = BookDetailsUiState.Error("Book not found")
                }
            } catch (e: Exception) {
                _uiState.value = BookDetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class BookDetailsUiState {
    object Loading : BookDetailsUiState()
    data class Success(val book: Book) : BookDetailsUiState()
    data class Error(val message: String) : BookDetailsUiState()
}