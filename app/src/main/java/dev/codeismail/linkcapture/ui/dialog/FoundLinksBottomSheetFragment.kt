package dev.codeismail.linkcapture.ui.dialog

import android.content.Intent
import android.content.SharedPreferences
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
import dagger.hilt.android.AndroidEntryPoint
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.adapter.LinkAdapter
import dev.codeismail.linkcapture.ui.SharedViewModel
import dev.codeismail.linkcapture.ui.history.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_found_links.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class FoundLinksBottomSheetFragment : BottomSheetDialogFragment() {

    @Inject lateinit var linkAdapter: LinkAdapter
    @Inject lateinit var sharedPreference: SharedPreferences

    private val viewModel: SharedViewModel by activityViewModels()
    private val dbViewModel: HistoryViewModel by activityViewModels ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_found_links, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shouldSave = sharedPreference.getBoolean(getString(R.string.key_save_label), false)

        linkRv.apply {
            adapter = linkAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
        viewModel.getLinks().observe(viewLifecycleOwner, Observer {
            linkAdapter.submitList(it)
            if (shouldSave) {
                dbViewModel.saveLink(
                    it)
            }

        })
        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        linkAdapter.setOnItemClickListener { position ->
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = current.format(formatter)
            dbViewModel.saveLink(
                listOf(
                    Link(
                        linkAdapter.getItem(position).id,
                        linkAdapter.getItem(position).linkString,
                        formattedDate
                    )
                )
            )
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(linkAdapter.getItem(position).linkString)
            })
        }
    }

    companion object {
        val TAG = FoundLinksBottomSheetFragment::class.java.simpleName
    }
}