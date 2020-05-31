package dev.codeismail.linkcapture

import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.codeismail.linkcapture.adapter.Link

class SharedViewModel : ViewModel() {

    private val imageUri = MutableLiveData<Uri>()
    private val imageProxy = MutableLiveData<ImageProxy>()
    private val link = MutableLiveData<List<Link>>()

    fun passLinkData(links: List<Link>){
        link.value = links
    }

    fun getLinks(): LiveData<List<Link>> = link

    fun passImageData(uri: Uri){
        imageUri.value = uri
    }

    fun passImageData(image: ImageProxy){
        imageProxy.value = image
    }

    fun getImageProxy() : LiveData<ImageProxy> = imageProxy

    fun getImageUri(): LiveData<Uri> = imageUri
}