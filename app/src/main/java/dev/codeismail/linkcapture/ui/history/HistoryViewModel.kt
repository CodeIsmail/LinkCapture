package dev.codeismail.linkcapture.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.data.DbLink
import dev.codeismail.linkcapture.data.LinkDao
import dev.codeismail.linkcapture.state.Loading
import dev.codeismail.linkcapture.state.Success
import dev.codeismail.linkcapture.state.ViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryViewModel(private val linkDao: LinkDao) : ViewModel() {

    private val viewStateMutableLiveData = MutableLiveData<ViewState>()
    private val viewStateSearch = MutableLiveData<ViewState>()

    fun saveLink(links: List<DbLink>){
        viewModelScope.launch {
            linkDao.insertAll(links)
        }
    }

    fun savedLinks(): LiveData<ViewState> {
        viewStateMutableLiveData.value = Loading
        viewModelScope.launch {
            linkDao.getLinks().collect {
                viewStateMutableLiveData.value = Success(it.map { dbLink ->
                    Link(dbLink.id, dbLink.linkString, dbLink.lastVisit)
                })
            }
        }
        return  viewStateMutableLiveData
    }

    fun searchUrl(query: String): LiveData<ViewState> {
        viewStateSearch.value = Loading
        viewModelScope.launch { linkDao.search(query).collect {
            viewStateSearch.value = Success(it.map {dbLink ->
                Link(dbLink.id, dbLink.linkString, dbLink.lastVisit)
            })
        } }
        return viewStateSearch
    }

}