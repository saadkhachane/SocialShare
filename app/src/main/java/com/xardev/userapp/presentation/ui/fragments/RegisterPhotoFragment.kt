package com.xardev.userapp.presentation.ui.fragments

import android.animation.Animator
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.xardev.userapp.R
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.databinding.FragmentRegisterPhotoBinding
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.presentation.viewmodels.RegisterPhotoFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class RegisterPhotoFragment : Fragment() {

    var user: User? = null
    var img_path: String? = null

    @Inject
    lateinit var viewModel: RegisterPhotoFragmentViewModel

    lateinit var binder: FragmentRegisterPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = arguments?.get("user") as User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register_photo, container, false)

        if (user != null) {

            binder.name.text = user!!.name
            img_path?.let {
                binder.image.setImageURI(Uri.parse(it))
                binder.imageLayout.visibility = View.INVISIBLE
            }

        } else {
            findNavController().navigateUp()
        }

        setClickListeners()
        setResultAnimListener()
        setCollectors()

        return binder.root
    }


    private fun setClickListeners() {

        binder.camera.setOnClickListener {

            if (!isCameraAccessAllowed()) {
                checkPermissionAccess(arrayOf(android.Manifest.permission.CAMERA), 1)
            } else {
                showCamera()
            }

        }

        binder.gallery.setOnClickListener {
            if (!isStorageAccessAllowed()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    checkPermissionAccess(
                        arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                        2
                    )
                } else {
                    checkPermissionAccess(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        2
                    )
                }

            } else {
                showGallery()
            }
        }

        binder.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binder.btnNext.setOnClickListener {

            if (user != null && img_path != null) {

                val bundle = bundleOf(
                    "user" to user,
                    "img_path" to img_path
                )

                findNavController().navigate(
                    R.id.action_registerPhotoFragment_to_registerInfoFragment,
                    bundle
                )

            } else {
                Snackbar.make(binder.root, "Please choose a profile image.", Snackbar.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun setCollectors() {

        lifecycleScope.launchWhenStarted {

            viewModel.result
                .collect { result ->

                    when (result) {

                        is Result.Loading -> {
                            if (result.isLoading) {
                                binder.progressBar.show()
                                binder.resultAnim.visibility = View.INVISIBLE
                                binder.btnNext.isEnabled = false
                                binder.btnBack.isEnabled = false

                            } else {
                                binder.progressBar.hide()
                                binder.resultAnim.visibility = View.VISIBLE
                                binder.btnNext.isEnabled = true
                                binder.btnBack.isEnabled = true
                            }
                        }

                        is Result.Success -> {

                            result.value?.let { it ->

                                    Snackbar.make(binder.root, it.toString(), Snackbar.LENGTH_LONG)
                                        .show()

                                    binder.resultAnim.apply {
                                        setAnimation(R.raw.success)
                                        playAnimation()
                                    }
                            }

                        }

                        is Result.Failure -> {
                            result.error.message?.let {

                                Snackbar.make(
                                    binder.root,
                                    result.error.message.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()

                                binder.resultAnim.apply {
                                    setAnimation(R.raw.failure)
                                    playAnimation()
                                }
                            }

                        }
                    }
                }

            viewModel.result
                .collect { it ->
                    if (it is Result.Success) {
                        it.value?.let{ value ->

                            Snackbar.make(binder.root, value.toString(), Snackbar.LENGTH_LONG)
                                .show()

                            binder.resultAnim.apply {
                                setAnimation(R.raw.success)
                                playAnimation()
                            }

                        }

                    } else if (it is Result.Failure) {
                        if (it.error.message != null) {

                            Snackbar.make(
                                binder.root,
                                it.error.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()

                            binder.resultAnim.apply {
                                setAnimation(R.raw.failure)
                                playAnimation()
                            }
                        }

                    }
                }
        }

    }

    private fun setResultAnimListener() {
        binder.resultAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binder.resultAnim.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
                binder.resultAnim.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
    }

    private fun isCameraAccessAllowed(): Boolean {
        val a = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "isCameraAccessAllowed: $a")
        return a

    }

    private fun isStorageAccessAllowed(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun checkPermissionAccess(permissions: Array<String>, requestCode: Int) {
        Log.d(TAG, "checkPermissionAccess: ")
        if (ContextCompat.checkSelfPermission(
                requireContext(), permissions[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (shouldShowRequestPermissionRationale(permissions[0])) {
                Log.d(TAG, "shouldShowRequestPermissionRationale: ")
                AlertDialog.Builder(context)
                    .setTitle("Permission")
                    .setMessage(getMessage(requestCode))
                    .setPositiveButton("Ok") { d: DialogInterface, i: Int ->
                        d.dismiss()
                        requestPermission(permissions, requestCode)
                    }
                    .setNegativeButton("No thanks") { d: DialogInterface, i: Int ->
                        d.dismiss()
                    }.create()
                    .show()

            } else {
                requestPermission(permissions, requestCode)
            }

        }
    }

    private fun getMessage(requestCode: Int): String {

        return when (requestCode) {
            1 -> "Camera access is required to take photos."
            2 -> "Storage access is required to select photos."
            else -> ""
        }

    }

    private fun requestPermission(permission: Array<String>, requestCode: Int) {
        requestPermissions(permission, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    showCamera()
                }
                return
            }

            2 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    showGallery()
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showGallery() {
        Log.d(TAG, "showGallery()")

        getContent.launch("image/*")
    }

    private fun showCamera() {

        val takeImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takeImageIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            1 -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        try {

                            val bitmap = data.extras?.get("data") as Bitmap

                            val file = createImageFile()
                            saveImageFile(file, bitmap)

                            performCrop(Uri.fromFile(file))

                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            Log.d(TAG, "onActivityResult: ${ex.message}")
                        }


                    }


                } else {
                    Log.d(TAG, "image: ${data?.data}")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)

                if (resultCode == RESULT_OK) {
                    if (data != null) {

                        try {

                            val file = File(result.getUri().path)

                            user?.img = file.name
                            img_path = file.absolutePath
                            binder.image.setImageURI(Uri.parse(file.absolutePath))

                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.uploadImage(file)
                            }


                        } catch (ex: Exception) {
                            // Error occurred while creating the File
                            Toast.makeText(context, "Couldn't crop image.", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "onActivityResult: ${ex.message}")
                        }

                    }


                } else {
                    Log.d(TAG, "image: ${data?.data}")
                }

            }


        }
    }

    val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->

        if(uri != null) {

            performCrop(uri)
        }

    }

    private fun performCrop(picUri: Uri) {
        try {

            CropImage.activity(picUri)
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMinCropResultSize(100,100)
                .setMaxCropResultSize(1000,1000)
                .start(requireContext(), this)
        }
        catch (anfe: Exception) {
            // display an error message
            val errorMessage = "Couldn't edit image."
            val toast: Toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    @Throws(IOException::class)
    private fun saveImageFile(file: File, bitmap: Bitmap) {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    }
}