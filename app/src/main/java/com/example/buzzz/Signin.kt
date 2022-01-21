package com.example.buzzz



import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.buzzz.databinding.ActivitySigninBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_signin.*
import java.util.concurrent.TimeUnit


class Signin : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]


    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var progressDialog: ProgressDialog
    private lateinit var mCounter: CountDownTimer

    private lateinit var countrycode: String
    private lateinit var phoneNumber: String

    val otp by lazy {
        findViewById<EditText>(R.id.otp)
    }
    val counterTv by lazy {
        findViewById<TextView>(R.id.counterTv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

     phoneNumberEt.addTextChangedListener{
         button_getotp.isEnabled=!(it.isNullOrEmpty() || it.length<10)
     }



        FirebaseApp.initializeApp(this)

        button_getotp.setOnClickListener{
            checkNumber()
            startPhoneNumberVerification(phoneNumber)
        }
        button_verifyotp.setOnClickListener{
            verifyPhoneNumberWithCode(storedVerificationId!!,otp.text.toString())
        }
        button_resendotp.setOnClickListener{
            checkNumber()
            startPhoneNumberVerification(phoneNumber)
        }

        auth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                Toast.makeText(applicationContext,"Welcome to BUZZZ...",Toast.LENGTH_SHORT).show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(applicationContext,"Not A Valid Phone Number...",Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.w(TAG, "sms quota exceeded", e)
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
                Toast.makeText(applicationContext,"Otp Sent...",Toast.LENGTH_SHORT).show()
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                startCounter(60000)

            }
        }
    }

    private fun startCounter(time: Long) {
        button_resendotp.isEnabled = false
        counterTv.isVisible = true
        mCounter = object : CountDownTimer(time, 1000) {
            override fun onFinish() {
                button_resendotp.isEnabled = true
                counterTv.isVisible = false
            }

            override fun onTick(timeLeft: Long) {

                counterTv.text = "Seconds Remaining : " + timeLeft / 1000
            }

        }.start()
    }

    override fun onStart() {
            super.onStart()
            val currentUser=auth.currentUser
            updateUI(currentUser)
        }

    private fun updateUI(currentUser: FirebaseUser?) {

    }

    private fun checkNumber(){
           countrycode=findViewById<CountryCodePicker>(R.id.ccp).selectedCountryCodeWithPlus
        phoneNumber=countrycode+phoneNumberEt.text.toString()
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
            // [START start_phone_auth]
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            // [END start_phone_auth]
        }

        private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
            // [START verify_with_code]
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithPhoneAuthCredential(credential)
            // [END verify_with_code]
        }
        // [START sign_in_with_phone]
        private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if(task.result?.additionalUserInfo?.isNewUser == true){
                            Log.d(TAG, "signInWithCredential:success")
                            Toast.makeText(applicationContext,"Welcome to BUZZZ...",Toast.LENGTH_SHORT).show()
                            showProfileActivity()
                            val user = task.result?.user
                        }else{
                            Log.d(TAG, "signInWithCredential:success")
                            Toast.makeText(applicationContext,"Welcome back..",Toast.LENGTH_SHORT).show()
                            showChatActivity()
                            val user = task.result?.user
                        }
                        // Sign in success, update UI with the signed-in user's information
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
//                        Toast.makeText(applicationContext,"Some error occured ,try again..",Toast.LENGTH_SHORT).show()
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(applicationContext,"The verification code entered was invalid..",Toast.LENGTH_SHORT).show()
                        }
                        // Update UI
                    }
                }
        }
// start chat activity for existing users
    private fun showChatActivity() {
        val intent = Intent(this,Chat::class.java)
        startActivity(intent)
    }

  // profile setup activity for new users
    private fun showProfileActivity() {
      val intent = Intent(this,Profile::class.java)
      startActivity(intent)
    }

    // [END sign_in_with_phone]



    companion object {
        private const val TAG = "Signin"
    }
}