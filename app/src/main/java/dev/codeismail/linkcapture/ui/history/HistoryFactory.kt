package dev.codeismail.linkcapture.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.codeismail.linkcapture.data.Repository
import javax.inject.Inject

class HistoryFactory @Inject constructor(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
