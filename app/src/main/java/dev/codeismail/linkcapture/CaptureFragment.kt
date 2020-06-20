package dev.codeismail.linkcapture

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.capture_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private const val REQUEST_CODE_PERMISSIONS = 10

private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class CaptureFragment : Fragment() {

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private val viewModel: SharedViewModel by activityViewModels()


    companion object {
        fun newInstance() = CaptureFragment()
        val TAG = CaptureFragment::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.capture_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        captureBtn.setOnClickListener {
            // Create timestamped output file to hold the image
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg")
            takePhoto(photoFile)

        }
        settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_captureFragment_to_settingFragment)
        }

        historyBtn.setOnClickListener {
            findNavController().navigate(R.id.action_captureFragment_to_historyFragment)
        }

        flashBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Log.d("Hello", "Hello $isChecked")
                imageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
            }else{
                Log.d("Hello", "Hello $isChecked")
                imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                activity!!.finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()
            //Image Capture
            imageCapture = ImageCapture.Builder()
                .build()

            val orientationEventListener = object : OrientationEventListener(requireContext()) {
                override fun onOrientationChanged(orientation : Int) {
                    // Monitors orientation values to determine the target rotation value
                    val rotation : Int = when (orientation) {
                        in 45..134 -> Surface.ROTATION_270
                        in 135..224 -> Surface.ROTATION_180
                        in 225..314 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }

                    imageCapture!!.targetRotation = rotation
                }
            }
            orientationEventListener.enable()

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
                applyTapToFocus()
                setUpPinchToZoom()
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun applyTapToFocus(){
        viewFinder.setOnTouchListener { view, event ->
            if (event.action != MotionEvent.ACTION_UP) {
                return@setOnTouchListener false
            }
            val meteringPoint = SurfaceOrientedMeteringPointFactory(
                view.width.toFloat(),
                view.height.toFloat()
            ).createPoint(event.x, event.y)
            Log.d("Hello", "Hello, Action Up")
            val action = FocusMeteringAction.Builder(meteringPoint).build()
            camera!!.cameraControl.startFocusAndMetering(action)
            return@setOnTouchListener true
        }
    }

    private fun setUpPinchToZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio: Float = camera!!.cameraInfo.zoomState.value?.zoomRatio ?: 0F
                val delta = detector.scaleFactor
                camera!!.cameraControl.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(context, listener)

        viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private fun takePhoto(photoFile: File) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    viewModel.passImageData(savedUri)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(TAG, msg)
                    findNavController().navigate(R.id.action_captureFragment_to_displayFragment)
                }
            })

    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }
    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context!!, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}
