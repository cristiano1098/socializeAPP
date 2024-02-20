package com.example.cmu_g10.Services.Camara

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil.compose.rememberImagePainter  // Import do Coil
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import coil.annotation.ExperimentalCoilApi
import com.example.camerax.CameraView
import com.example.cmu_g10.Services.AutoComplete.GeoapifyApiService
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.SocializeDatabase
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.cmu_g10.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CamaraActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    //Initialize database
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            SocializeDatabase::class.java,
            "socialize.db"
        ).fallbackToDestructiveMigration().build()
    }

    //Initialize retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val geoapifyApi = retrofit.create(GeoapifyApiService::class.java)

    //Initialize viewmodel
    private val expenseViewmodel by viewModels<ExpenseViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(db.expenseDao, db.expenseParticipantsDao, geoapifyApi) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (shouldShowCamera.value) {
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture
                ) { Log.e("kilo", "View error:", it) }
            }

            if (shouldShowPhoto.value) {
                MyImageComponent(uri = photoUri)
            }
        }

        requestCameraPermission()


        outputDirectory = getOutputDirectory()
        Log.d("OutputDirectory", "Photos are saved in: $outputDirectory")
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Requests the camera permission if not already granted.
     */
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Handles the image capture.
     *
     * @param uri The URI of the captured image.
     */
    private fun handleImageCapture(uri: Uri) {
        shouldShowCamera.value = false

        photoUri = uri

        shouldShowPhoto.value = true

        expenseViewmodel.setPhotoUri(photoUri.toString())
    }

    /**
     * Returns the output directory for photos.
     *
     * @return The output directory for photos.
     */
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    /**
     * Composable function representing the image captured by the camera.
     *
     * @param uri The URI of the captured image.
     */
    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun MyImageComponent(uri: Uri) {
        Image(
            painter = rememberImagePainter(uri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }

    //onDestroy
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

