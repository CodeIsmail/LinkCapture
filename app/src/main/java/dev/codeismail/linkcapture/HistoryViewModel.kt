package dev.codeismail.linkcapture

import androidx.lifecycle.*
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.data.DbLink
import dev.codeismail.linkcapture.data.LinkDao
import kotlinx.coroutines.launch

class HistoryViewModel(private val linkDao: LinkDao) : ViewModel() {

    fun saveLink(links: List<DbLink>){
        viewModelScope.launch {
            linkDao.insertAll(links)
        }
    }

    fun getSavedLink(): LiveData<List<Link>> = linkDao.getLinks().asLiveData().map {
        it.map {dbLink ->
            Link(dbLink.id, dbLink.linkString, dbLink.lastVisit)
        }
    }

}