package com.xardev.userapp.fragments

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.xardev.userapp.R
import com.xardev.userapp.RegisterActivity
import com.xardev.userapp.data.User
import com.xardev.userapp.databinding.FragmentRegisterMainBinding
import com.xardev.userapp.databinding.FragmentRegisterPhotoBinding
import com.xardev.userapp.databinding.FragmentWelcomeBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

private const val TAG = "here"

class RegisterPhotoFragment : Fragment() {

    var user : User? = null

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
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_photo, container, false)

        if (user != null){

            binder.name.text = user!!.name

        }else {
            findNavController().navigateUp()
        }

        setClickListeners()


        return binder.root
    }

    private fun setClickListeners() {

        binder.camera.setOnClickListener {

            if(!isCameraAccessAllowed()){
                checkPermissionAccess(arrayOf(android.Manifest.permission.CAMERA), 1)
            }else {
                showCamera()
            }

        }

        binder.gallery.setOnClickListener {
            if(!isStorageAccessAllowed()){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    checkPermissionAccess(arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE), 2)
                }else {
                    checkPermissionAccess(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        2)
                }

            }else {
                showGallery()
            }
        }

        binder.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binder.btnNext.setOnClickListener {
            val bundle = bundleOf("user" to user)

            findNavController().navigate(
                R.id.registerInfoFragment,
                bundle
            )
        }
    }

    private fun isCameraAccessAllowed() : Boolean{
        val a = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "isCameraAccessAllowed: $a")
        return a

    }

    private fun isStorageAccessAllowed() : Boolean{

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun checkPermissionAccess(permissions: Array<String>, requestCode: Int){
        Log.d(TAG, "checkPermissionAccess: ")
        if (ContextCompat.checkSelfPermission(
                requireContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED
        ){

            if (shouldShowRequestPermissionRationale(permissions[0])){
                Log.d(TAG, "shouldShowRequestPermissionRationale: ")
                AlertDialog.Builder(context)
                    .setTitle("Permission")
                    .setMessage(getMessage(requestCode))
                    .setPositiveButton("Ok"){ d: DialogInterface, i: Int ->
                        d.dismiss()
                        requestPermission(permissions, requestCode)
                    }
                    .setNegativeButton("No thanks"){ d: DialogInterface, i: Int ->
                        d.dismiss()
                    }.create()
                    .show()

            }else {
                requestPermission(permissions, requestCode)
            }

        }
    }

    private fun getMessage(requestCode: Int): String {

        return when(requestCode){
            1 -> "Camera access is required to take photos."
            2 -> "Storage access is required to select photos."
            else -> ""
        }

    }

    private fun requestPermission(permission: Array<String>, requestCode: Int){
        requestPermissions(permission, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showCamera()
                }
                return
            }

            2 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showGallery()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
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
        Log.d(TAG, "showCamera()")

        val takeImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takeImage, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){

            1 -> {
                if (resultCode == RESULT_OK){
                    if (data != null){

                        try {

                            val bitmap = data.extras?.get("data") as Bitmap

                            val file = createImageFile()
                            saveImageFile(file, bitmap)

                            user?.img_url = file.absolutePath
                            binder.image.setImageURI(Uri.parse(user?.img_url))
                            binder.imageLayout.visibility = View.GONE
                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            Log.d(TAG, "onActivityResult: ${ex.message}")
                        }

                    }


                }else {
                    Log.d(TAG, "image: ${data?.data}")
                }
            }

        }
    }

    val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
        Log.d(TAG, "getContent image: ${uri}")

        val bitmap: Bitmap = getBitmap(context?.contentResolver, uri)

        val file = createImageFile()
        saveImageFile(file, bitmap)

        user?.img_url = file.absolutePath
        binder.image.setImageURI(Uri.parse(user?.img_url))
        binder.imageLayout.visibility = View.GONE
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
    private fun saveImageFile(file: File, bitmap: Bitmap){
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.flush()
        stream.close()
    }
}