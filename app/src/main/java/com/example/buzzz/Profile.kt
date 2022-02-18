
package com.example.buzzz

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.buzzz.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_profile.*
import java.net.URI

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    val gallery = 1001
    val img_profile by lazy {
        findViewById<ImageView>(R.id.img_profile)
    }
    val button_continue_profile by lazy{
        findViewById<Button>(R.id.button_continue_profile)
    }

    val usernameET by lazy{
        findViewById<EditText>(R.id.usernameET)
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var auth:FirebaseAuth
    lateinit var downloadUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=Firebase.auth
        img_profile.setOnClickListener{
            checkForPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,gallery)
            opengallery()
        }
        button_continue_profile.setOnClickListener{
            if(usernameET.text.isEmpty()){
                Toast.makeText(applicationContext,"Username cannot be EMPTY!",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val user = User(usernameET.text.toString(),downloadUrl,auth.uid!!)
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Data Uploaded...",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,Bio::class.java)
                    startActivity(intent)
                }.addOnFailureListener{
                    Toast.makeText(applicationContext,"Error...Try Again!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkForPermissions(permission: String,requestCode: Int){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                when{
                    ContextCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED ->{
                        Toast.makeText(applicationContext,"Permission Granted",Toast.LENGTH_SHORT).show()
                    }
                    shouldShowRequestPermissionRationale(permission)-> showdialog(permission,requestCode)

                    else -> ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isEmpty() || grantResults[0]!=PackageManager.PERMISSION_GRANTED)
            Toast.makeText(applicationContext,"Permission Refused",Toast.LENGTH_SHORT).show()
        else{
            Toast.makeText(applicationContext,"Permission Granted",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showdialog(permission: String, requestCode: Int) {
           val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to Access your gallery is required for this app.")
            setTitle("Permission Required")
            setPositiveButton("OK"){dialog,which->
                ActivityCompat.requestPermissions(this@Profile, arrayOf(permission),requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun opengallery() {
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {
            intent?.data?.let { image ->
                img_profile.setImageURI(image)
                uploadimage(image)
        }
    }
        

}

    private fun uploadimage(image: Uri) {
            button_continue_profile.isEnabled = false
            val ref = FirebaseStorage.getInstance().reference.child("Profile_image_uploads/").child(auth.currentUser!!.uid)
            var uploadTask = ref.putFile(image)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    //Handle error
                    Toast.makeText(applicationContext,"Error...",Toast.LENGTH_SHORT).show()
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener{ task ->
                button_continue_profile.isEnabled = true
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"File Uploaded Successfully...",Toast.LENGTH_SHORT).show()
                    downloadUrl = task.result.toString()
                }
            }.addOnFailureListener{
                button_continue_profile.isEnabled = true
            }

    }
}