package dev.codeismail.linkcapture

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CaptureViewModel : ViewModel() {

    private val imageUri = MutableLiveData<Uri>()
    private val linkString = MutableLiveData<String>()

    fun passLinkData(link: String){
        linkString.value = link
    }

    fun getLinkString(): LiveData<String> = linkString

    fun passImageData(uri: Uri){
        imageUri.value = uri
    }

    fun getImageUri(): LiveData<Uri> = imageUri
}