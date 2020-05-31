package dev.codeismail.linkcapture

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.codeismail.linkcapture.adapter.LinkHistoryAdapter
import dev.codeismail.linkcapture.data.AppDatabase
import kotlinx.android.synthetic.main.history_fragment.*

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private val dbViewModel: HistoryViewModel by activityViewModels {
        HistoryFactory(AppDatabase.getInstance(requireContext()).linkDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linkHistoryAdapter = LinkHistoryAdapter()
        historyListView.apply {
            adapter = linkHistoryAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
        dbViewModel.getSavedLink().observe(viewLifecycleOwner, Observer {
            linkHistoryAdapter.submitList(it)
        })

    }

}