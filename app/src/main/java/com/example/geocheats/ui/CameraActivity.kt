package com.example.geocheats.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.geocheats.R
import com.example.geocheats.database.GuessDatabase
import com.example.geocheats.databinding.ActivityCameraBinding
import com.example.geocheats.ml.Planet
import com.example.geocheats.utils.copyBGRToBuffer
import com.example.geocheats.utils.resize
import com.example.geocheats.utils.toBitmap
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.common.geometry.S2CellId
import kotlinx.android.synthetic.main.activity_camera.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


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
        binding.lifecycleOwner = this


        viewModelFactory = CameraViewModelFactory(GuessDatabase.getInstance(application).dao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CameraViewModel::class.java)

        binding.viewModel = viewModel
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?


        viewModel.state.observe(this) {
            if (it == "MAP") {
                mapFragment?.getMapAsync { googleMap ->
                    val guess = viewModel.guess.value?.lat?.let { it1 -> viewModel.guess.value?.lng?.let { it2 -> LatLng(it1, it2) } }

                    if (guess != null) {
                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(guess).title("My best guess!"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(guess))
                    }

                }

                mapView.isVisible = true
                cameraView.isGone = true
            } else if (it == "CAMERA") {
                cameraView.isVisible = true
                mapView.isGone = true
            }
        }

        viewModel.capturedImage.observe(this, androidx.lifecycle.Observer {
            binding.capturedImage.setImageBitmap(it)
        })


        cameraCaptureButton.setOnClickListener {
            processPhoto()
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
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

//                var imageBitmap = BitmapFactory.decodeResource(resources, R.mipmap.hasseris)
                var imageBitmap = image.toBitmap()
                viewModel.onCapture(imageBitmap.resize(640, 480))

                imageBitmap = imageBitmap.resize(299, 299)
                val imageBuffer = imageBitmap.copyBGRToBuffer()
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 299, 299, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(imageBuffer)

                val outputs = planet.process(inputFeature0)
                val outputsAsTensorBuffer = outputs.outputFeature0AsTensorBuffer
                val index = outputsAsTensorBuffer.floatArray.indices.maxByOrNull { outputsAsTensorBuffer.floatArray[it] }

                Timber.i("Output flat size: %s".format(outputsAsTensorBuffer.flatSize.toString()))
                Timber.i("Output at 0, 1, 2: %f %f %f".format(outputsAsTensorBuffer.floatArray[0], outputsAsTensorBuffer.floatArray[1], outputsAsTensorBuffer.floatArray[2]))
                Timber.i("Maximum index, maximum value: %d %f", index, outputsAsTensorBuffer.floatArray[index!!])

                var geolocationToken = ""
                csvReader().open(resources.openRawResource(R.raw.planet_v2_labelmap)) {
                    readAllAsSequence().forEach { row: List<String> ->
                        if (row[0] == geolocationToken) {
                            geolocationToken = row[1]
                        }
                    }
                }

//                val s2LatLng = S2CellId.fromToken(geolocationToken).toLatLng()
//                val latLng = Pair(s2LatLng.lat().degrees(), s2LatLng.lng().degrees())
                val latLng = Pair(57.059299, 9.935725)


                viewModel.onGuessed(latLng, 1.0f)


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
                Timber.i("Photo capture failed: ${exc.message}")
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

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Timber.i("Use case binding failed: ${exc.message}")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onBackPressed() {
        if (viewModel.state.value == "MAP") {
            viewModel.onReturnToPreview()
        } else {
            super.onBackPressed()
        }
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



