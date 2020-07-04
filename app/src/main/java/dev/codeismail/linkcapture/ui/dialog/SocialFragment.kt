package dev.codeismail.linkcapture.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.adapter.SocialAdapter
import dev.codeismail.linkcapture.ui.SharedViewModel
import dev.codeismail.linkcapture.utils.CustomSocialCheckComponent
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SocialFragment : DialogFragment() {

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tempList = mutableListOf<Link>()
        val socialAdapter = SocialAdapter()
        socialViewPage.apply {
            adapter = socialAdapter
        }
        socialViewPage.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
        viewModel.getLinks().observe(viewLifecycleOwner, Observer {
            socialAdapter.data = it.filter { link -> link.linkString.startsWith("@") }
            tempList = it.filter { link -> !link.linkString.startsWith("@") }.toMutableList()
        })
        socialAdapter.setOnItemClickListener { position, viewItem ->
            when(viewItem.id){
                R.id.twitterComponent -> {
                    val link = socialAdapter.data[position]
                    if ((view as CustomSocialCheckComponent).getCheckState()){
                        val linkString =  "http://twitter.com/${link.linkString.removePrefix("@")}"
                        val newLink = Link(id =  link.id, linkString = linkString)
                        tempList.add(newLink)
                    }else{
                        tempList.removeIf {
                            it.id == link.id
                        }
                    }
                }
                R.id.instagramComponent -> {
                    val link = socialAdapter.data[position]
                    if ((view as CustomSocialCheckComponent).getCheckState()){
                        val linkString =  "http://instagram.com/${link.linkString.removePrefix("@")}"
                        val newLink = Link(id =  link.id, linkString = linkString)
                        tempList.add(newLink)
                    }else{
                        tempList.removeIf {
                            it.id == link.id
                        }
                    }
                }
                R.id.githubComponent -> {
                    val link = socialAdapter.data[position]
                    if ((view as CustomSocialCheckComponent).getCheckState()){
                        val linkString =  "http://github.com/${link.linkString.removePrefix("@")}"
                        val newLink = Link(id =  link.id, linkString = linkString)
                        tempList.add(newLink)
                    }else{
                        tempList.removeIf {
                            it.id == link.id
                        }
                    }
                }

            }
        }

        dialogCloseBtn.setOnClickListener {
            dialog?.dismiss()
        }
        dialogDoneBtn.setOnClickListener {
            dialog?.dismiss()
            lifecycleScope.launch {
                withContext(IO){
                    viewModel.passLinkDataFromIO(tempList.toList())
                    findNavController().navigate(R.id.action_socialFragment_to_actionDialogFragment)
                }

            }
        }
    }

    companion object {
    }
}