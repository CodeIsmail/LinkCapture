package dev.codeismail.linkcapture

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import coil.api.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import dev.codeismail.linkcapture.adapter.Link
import kotlinx.android.synthetic.main.fragment_display.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DisplayFragment : Fragment() {

    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        return inflater.inflate(R.layout.fragment_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        backBtn.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.host_fragment).navigateUp()
        }
        viewModel.getImageUri().observe(viewLifecycleOwner, Observer {
            imageDisplay.load(it)
            val processingDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.layout_dialog_processing).show()
            startImageAnalysis(it, processingDialog)
        })
    }

    private fun startImageAnalysis(imageUri : Uri, processingDialog: AlertDialog){
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
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalyzer)
            } catch(exc: Exception) {
                Log.e(CaptureFragment.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processTextBlock(result: FirebaseVisionText) {
        var resultText: String
        val linkList = ArrayList<Link>()
        for (block in result.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val elementText = element.text

                    val pattern = "^[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$".toRegex()
                    if (pattern.matches(elementText)) {
                        resultText = elementText
                        if (!resultText.startsWith("https://") && !resultText.startsWith("http://")){
                            resultText = "http://$resultText"
                        }
                        linkList.add(Link(linkString = resultText))
                    }
                }
            }

        }

        viewModel.passLinkData(linkList)
        findNavController().navigate(R.id.action_displayFragment_to_actionDialogFragment)
    }

    private inner class TextImageAnalyzer(private val uri: Uri, private val dialog: AlertDialog) : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = FirebaseVisionImage.fromFilePath(requireContext(), uri)
                val detector = FirebaseVision.getInstance()
                    .onDeviceTextRecognizer
                detector.processImage(image)
                    .addOnSuccessListener {firebaseVisionText->
                        dialog.dismiss()
                        processTextBlock(firebaseVisionText)
                    }
                    .addOnFailureListener {
                        Log.d(CaptureFragment.TAG, "Exception thrown: ${it.message}")
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Image analysis failed!", Toast.LENGTH_LONG).show()
                    }

            }
        }
    }
    companion object {
    }
}