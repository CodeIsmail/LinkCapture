package dev.codeismail.linkcapture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.codeismail.linkcapture.data.LinkDao

class HistoryFactory(private val dao: LinkDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
