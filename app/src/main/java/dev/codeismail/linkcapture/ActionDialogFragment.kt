package dev.codeismail.linkcapture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.codeismail.linkcapture.adapter.LinkAdapter
import dev.codeismail.linkcapture.data.AppDatabase
import dev.codeismail.linkcapture.data.DbLink
import kotlinx.android.synthetic.main.fragment_action_dialog.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ActionDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private val dbViewModel: HistoryViewModel by activityViewModels {
        HistoryFactory(AppDatabase.getInstance(requireContext()).linkDao())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linkAdapter = LinkAdapter()
        linkRv.apply {
            adapter = linkAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
        viewModel.getLinks().observe(viewLifecycleOwner, Observer {
            linkAdapter.submitList(it)

        })
        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        linkAdapter.setOnItemClickListener {position ->
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = current.format(formatter)
            dbViewModel.saveLink(
                listOf(DbLink(
                    linkAdapter.getItem(position).id,
                    linkAdapter.getItem(position).linkString,
                    formattedDate
                )))
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(linkAdapter.getItem(position).linkString)
            })
        }
    }

    companion object {

    }
}