package com.example.recipes.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.recipes.BuildConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CameraUtils {
    fun openCamera(context: Context, cameraLauncher: ActivityResultLauncher<Intent>):File? {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Create the File where the photo should go
            val photoFile: File? = try {
                createNewImageFile(context).apply {
                    // Save a file: path for use with ACTION_VIEW intents
                    absolutePath
                }
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                try{
                    cameraLauncher.launch(takePictureIntent)
                }catch (_: ActivityNotFoundException){}
            }
            return photoFile
        }
    }

    fun openImageFiles(imageFilesLauncher: ActivityResultLauncher<Intent>) {
        Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickFromImagesIntent ->
            pickFromImagesIntent.type = "image/*"
            try{
                imageFilesLauncher.launch(pickFromImagesIntent)
            }catch (_: ActivityNotFoundException){}
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createNewImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askForCameraAndMediaImagesPermissions(activity: Activity, requestCodePermissions:Int){
        ActivityCompat.requestPermissions(activity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            ), requestCodePermissions
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askForMediaImagesPermissions(activity: Activity, requestCodePermissions:Int){
        ActivityCompat.requestPermissions(activity,
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            ), requestCodePermissions
        )
    }

    fun askForCameraAndExternalStoragePermissions(activity: Activity, requestCodePermissions:Int){
        ActivityCompat.requestPermissions(activity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), requestCodePermissions
        )
    }

    fun askForExternalStoragePermissions(activity: Activity, requestCodePermissions:Int){
        ActivityCompat.requestPermissions(activity,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), requestCodePermissions
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun areCameraAndMediaImagePermissionsGranted(context:Context):Boolean{
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
        == PackageManager.PERMISSION_GRANTED)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun areMediaImagePermissionsGranted(context:Context):Boolean{
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED)
    }


    fun areCameraAndExternalStoragePermissionsGranted(context:Context):Boolean{
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun areExternalStoragePermissionsGranted(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }
}