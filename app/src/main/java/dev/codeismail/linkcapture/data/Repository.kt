package dev.codeismail.linkcapture.data

import dev.codeismail.linkcapture.adapter.Link
import kotlinx.coroutines.flow.Flow

class Repository(private val linkDao: LinkDao){
    fun search(query: String): Flow<List<DbLink>> {
        return linkDao.search(query)
    }

    fun loadLinks(): Flow<List<DbLink>> {
        return linkDao.getLinks()
    }

    suspend fun saveLinks(links: List<Link>) {
        linkDao.insertAll(links.map { link ->
            DbLink(
                link.id,
                link.linkString,
                "-"
            )
        })
    }

}