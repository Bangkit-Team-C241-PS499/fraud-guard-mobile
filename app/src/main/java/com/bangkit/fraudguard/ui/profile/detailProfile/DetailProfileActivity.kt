package com.bangkit.fraudguard.ui.profile.detailProfile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.dto.response.ChangePhotoResponse
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.databinding.ActivityDetailProfileBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.editPassword.ChangePasswordActivity
import com.bangkit.fraudguard.ui.profile.editProfile.EditProfileActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class DetailProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        checkUserSession()
        observeProfile()
        setupView()
        setupAction()
    }

    private fun checkUserSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.btnEditProfile.setOnClickListener {
            var intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }

        binding.btnChangePhoto.setOnClickListener {
            checkMediaPermissions()


        }

        binding.changePasswordCard.setOnClickListener {
            var intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        observeProfile()
    }
    private fun checkMediaPermissions() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        } else {
            changePhotoProfile()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                changePhotoProfile()
            } else {
                showCustomToast(this, "Permission Denied", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun changePhotoProfile(){
        val options = arrayOf<CharSequence>("Ambil dengan Kamera", "Pilih dari galeri", "Batal")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Foto Profile")

        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Ambil dengan Kamera" -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePictureIntent, TAKE_PHOTO)
                }
                options[item] == "Pilih dari galeri" -> {
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickPhotoIntent.type = "image/*"
                    startActivityForResult(Intent.createChooser(pickPhotoIntent, "Select Picture"), PICK_IMAGE)
                }
                options[item] == "Batal" -> {
                    dialog.dismiss()
                }
            }
        }


        builder.show()
    }
    private fun observeProfile() {
        viewModel.showProfile().observe(this, Observer { response ->
            if (response.isSuccessful) {
                val profile: ProfileResponse? = response.body()
                // Update UI dengan data profile
                binding.profileEmail.text = profile?.email
                binding.tvDescriptionEmail.text = profile?.email
                binding.profileName.text = profile?.name
                binding.tvDescriptionName.text = profile?.name

                if (profile?.photoUrl != null) {
                    Glide.with(this).load(profile.photoUrl).into(binding.profileImage)
                }
            } else {
                showCustomToast(this, "Gagal memuat profile data", Toast.LENGTH_SHORT)
            }
        })
    }

    private fun setupView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    data?.data?.let { imageUri ->
                        // Lakukan sesuatu dengan imageUri, misalnya tampilkan di ImageView
                        findViewById<ImageView>(R.id.profile_image).setImageURI(imageUri)
                        uploadImageToServer(imageUri)
                    }
                }
                TAKE_PHOTO -> {

                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val imageUri = getImageUri(this, imageBitmap)
                    findViewById<ImageView>(R.id.profile_image).setImageBitmap(imageBitmap)
                    uploadImageToServer(imageUri)
                }
            }
        }
    }

    private fun uploadImageToServer(imageUri: Uri) {
        lifecycleScope.launch {
            val compressedFile = compressImage(imageUri)
            val requestFile = compressedFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", compressedFile.name, requestFile)

            try {
                viewModel.updateProfilePicture(body).observe(this@DetailProfileActivity){ response ->
                    if (response.isSuccessful){
                        showCustomToast(this@DetailProfileActivity, "Ubah Profile Berhasil!", Toast.LENGTH_SHORT)
                        observeProfile()
                    } else {
                        var msg = extractErrorMessage(response)
                        showCustomToast(this@DetailProfileActivity, msg, Toast.LENGTH_SHORT)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showCustomToast(this@DetailProfileActivity, "Terjadi Error: ${e.message}", Toast.LENGTH_SHORT)
            }
        }
    }


    private fun compressImage(imageUri: Uri): File {
        val filePath = getPathFromUri(imageUri)
        val originalFile = File(filePath)
        val compressedFile = File(applicationContext.cacheDir, originalFile.name)

        if (!compressedFile.exists()) {
            compressedFile.createNewFile()
        }

        val bitmap = BitmapFactory.decodeFile(originalFile.path)
        var quality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            quality -= 5
            compressedFile.writeBytes(bmpPicByteArray)
        } while (streamLength >= 1024 * 1024)

        return compressedFile
    }
    private fun extractErrorMessage(response: Response<ChangePhotoResponse>): String {
        return try {
            val json = response.errorBody()?.string()
            val jsonObject = JSONObject(json)
            jsonObject.getString("error")
        } catch (e: Exception) {
            e.message ?: "An error occurred"
        }
    }

    private fun getPathFromUri(uri: Uri): String {
        var filePath = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst().apply {
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            filePath = cursor?.getString(columnIndex ?: 0) ?: ""
            cursor?.close()
        }
        return filePath
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }



    companion object {
        private const val PICK_IMAGE = 100
        private const val TAKE_PHOTO = 101
        private const val REQUEST_CODE = 102
    }

}