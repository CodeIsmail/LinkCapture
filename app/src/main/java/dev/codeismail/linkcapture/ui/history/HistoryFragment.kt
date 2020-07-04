package dev.codeismail.linkcapture.ui.history

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.codeismail.linkcapture.ui.MainActivity
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.adapter.LinkHistoryAdapter
import dev.codeismail.linkcapture.data.AppDatabase
import dev.codeismail.linkcapture.data.DbLink
import dev.codeismail.linkcapture.state.Loading
import dev.codeismail.linkcapture.state.Success
import dev.codeismail.linkcapture.utils.hide
import dev.codeismail.linkcapture.utils.show
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.layout_empty_state.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() =
            HistoryFragment()
    }

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private lateinit var linkHistoryAdapter: LinkHistoryAdapter
    private val dbViewModel: HistoryViewModel by activityViewModels {
        HistoryFactory(
            AppDatabase.getInstance(
                requireContext()
            ).linkDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setSupportActionBar(historyToolbar)
        historyToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        linkHistoryAdapter = LinkHistoryAdapter()
        historyListView.apply {
            adapter = linkHistoryAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
        dbViewModel.savedLinks().observe(viewLifecycleOwner, Observer {viewState->
            when(viewState){
                Loading-> {
                    displayLoading()
                }
                is Success->{
                    linkHistoryAdapter.submitList(viewState.links)
                    displayUrls(viewState.links, false)

                }
            }
        })

        linkHistoryAdapter.setOnItemClickListener { position ->
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = current.format(formatter)
            dbViewModel.saveLink(
                listOf(
                    DbLink(
                        linkHistoryAdapter.getItem(position).id,
                        linkHistoryAdapter.getItem(position).linkString,
                        formattedDate
                    )
                )
            )
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(linkHistoryAdapter.getItem(position).linkString)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        historyToolbar.inflateMenu(R.menu.menu_history)
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager =
            activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView?
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                processUrlSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                processUrlSearch(newText)
                return false
            }

        }
        searchView?.setOnQueryTextListener(queryTextListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search -> {
                return false
            }

        }
        searchView?.setOnQueryTextListener(queryTextListener)
        return super.onOptionsItemSelected(item)
    }

    private fun processUrlSearch(query: String){
        dbViewModel.searchUrl("%$query%").observe(viewLifecycleOwner, Observer {viewState->
            when(viewState){
                Loading-> {
                    displayLoading()
                }
                is Success->{
                    linkHistoryAdapter.submitList(viewState.links)
                    displayUrls(viewState.links, true)

                }
            }

        })
    }
    private fun displayUrls(links: List<Link>, isSearch: Boolean){
        progressBar.hide()
        if (links.isEmpty()){
            historyListView.hide()
            emptyView.setImageResource(if (isSearch) R.drawable.ic_empty_search else
                R.drawable.ic_empty
            )
            emptyTv.text = if (isSearch) getString(R.string.search_empty_list_message) else
                getString(R.string.empty_list_message)
            emptyState.show()
        }else{
            emptyState.hide()
            historyListView.show()
        }
    }

    private fun displayLoading(){
        historyListView.hide()
        emptyState.hide()
        progressBar.show()
    }

}