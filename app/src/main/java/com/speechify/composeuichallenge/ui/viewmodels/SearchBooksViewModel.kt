// ui/viewmodels/SearchBooksViewModel.kt
package com.speechify.composeuichallenge.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speechify.composeuichallenge.data.Book
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchBooksViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchBooksUiState>(SearchBooksUiState.Loading)
    val uiState: StateFlow<SearchBooksUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadBooks()

        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .filter { query ->
                    // Don't show loading indicator during search
                    if (query.isNotEmpty()) {
                        performSearch(query)
                        false
                    } else {
                        true
                    }
                }
                .collect {
                    // Reload all books when search is cleared
                    loadBooks()
                }
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = SearchBooksUiState.Loading
            try {
                val books = repository.getBooks()
                _uiState.value = SearchBooksUiState.Success(books)
            } catch (e: Exception) {
                _uiState.value = SearchBooksUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            loadBooks()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            // Don't show loading during search
            try {
                val results = repository.searchBook(query)
                // Preserve the current state type (Success) while updating results
                val currentState = _uiState.value
                if (currentState is SearchBooksUiState.Success) {
                    _uiState.value = SearchBooksUiState.Success(results)
                } else {
                    _uiState.value = SearchBooksUiState.Success(results)
                }
            } catch (e: Exception) {
                // Don't show error during search, keep existing results
            }
        }
    }
}

sealed class SearchBooksUiState {
    object Loading : SearchBooksUiState()
    data class Success(val books: List<Book>) : SearchBooksUiState()
    data class Error(val message: String) : SearchBooksUiState()
}