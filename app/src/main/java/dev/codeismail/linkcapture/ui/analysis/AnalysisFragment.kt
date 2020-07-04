package dev.codeismail.linkcapture.ui.analysis

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import coil.api.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import dev.codeismail.linkcapture.R
import dev.codeismail.linkcapture.ui.SharedViewModel
import dev.codeismail.linkcapture.adapter.Link
import dev.codeismail.linkcapture.ui.capture.CaptureFragment
import dev.codeismail.linkcapture.utils.Manager
import dev.codeismail.linkcapture.utils.NetworkResult
import kotlinx.android.synthetic.main.fragment_display.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AnalysisFragment : Fragment() {

    private lateinit var networkState: NetworkResult
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: SharedViewModel by activityViewModels()
    private val networkManager by lazy { Manager(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkManager.result.observe(viewLifecycleOwner, Observer {
            Log.d("Hello", "Value Set ${it.name}")
            networkState = it
        })
        cameraExecutor = Executors.newSingleThreadExecutor()
        backBtn.setOnClickListener {
            Navigation.findNavController(requireActivity(),
                R.id.host_fragment
            ).navigateUp()
        }
        viewModel.getImageUri().observe(viewLifecycleOwner, Observer {
            imageDisplay.load(it)
            val processingDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.layout_dialog_processing).show()
            startImageAnalysis(it, processingDialog)
        })
    }

    override fun onStart() {
        super.onStart()
        networkManager.registerCallback()
    }

    override fun onStop() {
        super.onStop()
        networkManager.unregisterCallback()
    }
    private fun startImageAnalysis(imageUri: Uri, processingDialog: AlertDialog) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Image Analysis
            imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, TextImageAnalyzer(imageUri, processingDialog))
                }

            // Select back camera
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(CaptureFragment.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processTextBlock(result: FirebaseVisionText) {
        val linkList = ArrayList<Link>()
        for (block in result.textBlocks) {
            for (line in block.lines) {
                linkList.addAll(line.elements.filter { element ->
                    val pattern: Regex = if (element.text.startsWith("@")){
                        "^[@][a-zA-Z0-9_.]+$".toRegex()
                    }else{
                        "^((http:/{2})|(HTTP:/{2}))?((https:/{2})|(HTTPS:/{2}))?((w{3}.)|(W{3}.))?([a-zA-Z0-9]*[.])*[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}(/[a-zA-Z0-9]*.*-*)*$".toRegex()
                    }
                    pattern.matches(element.text)
                }.map { element ->
                    var resultText = element.text
                    if(!resultText.startsWith("@")){
                        if (!resultText.startsWith("https://") && !resultText.startsWith("http://")) {
                            resultText = "http://$resultText"
                        }
                    }
                    Link(linkString = resultText)
                })

            }

        }

        if (linkList.isNotEmpty()) {
            val size = linkList.filter { link ->
                link.linkString.startsWith("@")
            }.size
            viewModel.passLinkData(linkList)
            if (size != 0){
                findNavController().navigate(R.id.action_displayFragment_to_socialFragment)
            }else{
                findNavController().navigate(R.id.action_displayFragment_to_actionDialogFragment)
            }
        } else {
            Toast.makeText(requireContext(), "No url found!", Toast.LENGTH_LONG).show()
        }

    }

    private inner class TextImageAnalyzer(private val uri: Uri, private val dialog: AlertDialog) :
        ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = FirebaseVisionImage.fromFilePath(requireContext(), uri)
                val detector: FirebaseVisionTextRecognizer = if (networkState == NetworkResult.CONNECTED ){
                    Log.d("Hello", "Using on device")
                    FirebaseVision.getInstance()
                        .onDeviceTextRecognizer
                }else{
                    Log.d("Hello", "Using cloud")
                    FirebaseVision.getInstance()
                        .cloudTextRecognizer
                }
                detector.processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        dialog.dismiss()
                        processTextBlock(firebaseVisionText)
                    }
                    .addOnFailureListener {
                        Log.d(CaptureFragment.TAG, "Exception thrown: ${it.message}")
                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Image analysis failed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            }
        }
    }

    companion object {
        val TAG = AnalysisFragment::class.java.simpleName
    }
}