package com.example.geocheats.ui

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.geocheats.R
import com.example.geocheats.database.CountryDatabase
import com.example.geocheats.databinding.ActivityCameraBinding
import com.example.geocheats.ml.Planet
import com.example.geocheats.toBitmap
import com.example.geocheats.toByteArray
import kotlinx.android.synthetic.main.activity_camera.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import timber.log.Timber
import java.io.File
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
typealias LandmarkListener = (landmark: Bitmap, label: String) -> Unit

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private val planet by lazy {
        Planet.newInstance(this)
    }


    private lateinit var binding: ActivityCameraBinding
    private lateinit var viewModelFactory: CameraViewModelFactory
    private lateinit var viewModel: CameraViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        binding.setLifecycleOwner(this)

        viewModelFactory = CameraViewModelFactory(CountryDatabase.getInstance(application).dao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CameraViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.capturedImage.observe(this, androidx.lifecycle.Observer {
            Timber.i("Setting the image to ${it?.height}, ${it?.width} sized bitmap.")
            binding.imageView.setImageBitmap(it)
        })


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }



        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener {
            processPhoto()
        }


        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }




    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    private fun processPhoto() {

        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                Timber.i("Dims: ${image.width}, ${image.height}")

                val imageBitmap = image.toBitmap()
                viewModel.onCapture(imageBitmap)


                Timber.i("Byte count: %d", imageBitmap.byteCount)
                Timber.i("Byte count: %d", imageBitmap.allocationByteCount)
                Timber.i("Bitmap shape: %d %d %d", imageBitmap.width, imageBitmap.height, imageBitmap.density)

                val buffer: ByteBuffer = ByteBuffer.allocate(299*299*3)
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 299, 299, 3), DataType.FLOAT32)

                Timber.i("Expected size: %s", inputFeature0.flatSize.toString())
                inputFeature0.loadBuffer(buffer)

                // Runs model inference and gets result.
                val outputs = planet.process(inputFeature0).outputFeature0AsTensorBuffer
                val index = outputs.floatArray.indices.maxByOrNull { outputs.floatArray[it] }

                Timber.v("Maximum index, maximum value: %d %f", index, outputs.floatArray[index!!])


                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }
        })
    }

    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return


        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")



        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Timber.i( "Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Timber.i(msg)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()
            val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, LandmarkAnalyzer(planet) { bitmap, label ->
                            viewModel.onCapture(bitmap)
                            viewModel.onCountryGuessed(label, 1.0f)
                           Timber.i("Landmark guess: $label")
                        })
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Timber.i("Use case binding failed: ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(this))
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        planet.close()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}


private class LandmarkAnalyzer(private val model: Planet,
                                 private val listener: LandmarkListener) : ImageAnalysis.Analyzer {


    override fun analyze(image: ImageProxy) {

        val image_bitmap = image.toBitmap()
//
//        val outputs = model.process(TensorImage.fromBitmap(image_bitmap))
//        val probability = outputs.probabilityAsCategoryList

//        val prediction = probability.maxByOrNull { it.score }!!


//        listener(image_bitmap, prediction.label)


        image.close()
    }


}



