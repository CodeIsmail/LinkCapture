package dev.codeismail.linkcapture

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.api.load
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.capture_fragment.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DisplayFragment : Fragment() {

    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: CaptureViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ImageView(context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        viewModel.getImageUri().observe(viewLifecycleOwner, Observer {
            (view as ImageView).load(it)
            startImageAnalysis(it)
        })
    }

    private fun startImageAnalysis(imageUri : Uri){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Image Analysis
            imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, TextImageAnalyzer(imageUri))
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
        // [START mlkit_process_text_block]
        val resultText = result.text
        Log.d("Hello", "Hello text $resultText")
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementConfidence = element.confidence
                    val elementLanguages = element.recognizedLanguages
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
        viewModel.passLinkData(resultText)
        findNavController().navigate(R.id.action_displayFragment_to_actionDialogFragment)
    }

    private inner class TextImageAnalyzer(private val uri: Uri) : ImageAnalysis.Analyzer {
        //val rotation = getRotationCompensation(CameraSelector.LENS_FACING_BACK, activity!!, requireContext())
        override fun analyze(imageProxy: ImageProxy) {
            Log.d("Hello", "Hello in image analyzer")
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = FirebaseVisionImage.fromFilePath(requireContext(), uri)
                val detector = FirebaseVision.getInstance()
                    .onDeviceTextRecognizer
                detector.processImage(image)
                    .addOnSuccessListener {firebaseVisionText->
                        processTextBlock(firebaseVisionText)
                    }
                    .addOnFailureListener {
                        Log.d(CaptureFragment.TAG, "Exception thrown: ${it.message}")
                    }

            }
        }
    }
    companion object {
    }
}