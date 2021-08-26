package dev.codeismail.linkcapture.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.data.Repository
import dev.codeismail.linkcapture.state.Loading
import dev.codeismail.linkcapture.state.Success
import dev.codeismail.linkcapture.state.ViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private val viewStateMutableLiveData = MutableLiveData<ViewState>()
    private val viewStateSearch = MutableLiveData<ViewState>()

    fun saveLink(links: List<Link>){
        viewModelScope.launch {
            repo.saveLinks(links)
        }
    }

    fun savedLinks(): LiveData<ViewState> {
        viewStateMutableLiveData.value = Loading
        viewModelScope.launch {
            repo.loadLinks().collect {
                viewStateMutableLiveData.value = Success(it.map { dbLink ->
                    Link(dbLink.id, dbLink.linkString, dbLink.lastVisit)
                })
            }
        }
        return  viewStateMutableLiveData
    }

    fun searchUrl(query: String): LiveData<ViewState> {
        viewStateSearch.value = Loading
        viewModelScope.launch { repo.search(query).collect {
            viewStateSearch.value = Success(it.map {dbLink ->
                Link(dbLink.id, dbLink.linkString, dbLink.lastVisit)
            })
        } }
        return viewStateSearch
    }

}