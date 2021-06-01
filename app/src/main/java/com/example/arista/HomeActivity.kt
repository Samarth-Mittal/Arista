package com.example.arista

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.HandlerThread
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.arista.data.model.SendHelp
import com.example.arista.ui.main.viewmodel.MainViewModel
import com.example.arista.utils.Constants.Companion.USER_LOCATION
import com.example.arista.utils.Status
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val PERMISSION_ID = 200
    private var shouldRecognize = true
    lateinit var reason: String
    lateinit var viewModel: MainViewModel
    lateinit var userLocation: String
    lateinit var speechRecognizer: SpeechRecognizer


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = token.edit()

        var panicText = token.getString("panic_word", "help")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        initSpinner()

        sendHelpBtnCard.setOnClickListener(){
            sendHelp(token)
        }

        addContactsBtnCard.setOnClickListener(){
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        panicWord.setOnClickListener(){
            MyCustomDialog().show(supportFragmentManager, "MyCustomFragment")
        }

        logoutBtnCard.setOnClickListener(){
            editor.clear()
            editor.putString("isLoggedIn", "")
            editor.commit()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(baseContext)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        btnVoiceRecognition.setOnClickListener {
            if(shouldRecognize){
                if(checkAudioPermission()) {
                    btnVoiceRecognition.text = "Stop listening"
                    speechRecognizer.startListening(speechRecognizerIntent)
                    shouldRecognize = !shouldRecognize
                }else{
                    requestPermissions()
                }
            }else{
                btnVoiceRecognition.text = "Voice Recognition"
                speechRecognizer.stopListening()
                shouldRecognize = !shouldRecognize
            }
        }

        speechRecognizer.setRecognitionListener(object: RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onResults(results: Bundle?) {
                btnVoiceRecognition.text = "Voice Recognition"
                speechRecognizer.stopListening()
                shouldRecognize = !shouldRecognize
                val data =
                    results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, data.toString())
                panicText = token.getString("panic_word", "help")
                if(data?.get(0)?.contains(panicText.toString())!!){
                    sendHelp(token, "I am in trouble. Screaming for help.")
                }
            }

        })
    }

    private fun checkAudioPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun sendHelp(token: SharedPreferences, emergencyReason: String = reason) {
        Log.i("LOGTAG: ", "SEND btn tapped.")
        //getLocation(token)
        userLocation = USER_LOCATION
        Log.i("LOGTAG: ", "https://www.google.com/maps/search/?api=1&query="+userLocation)
        val userId = getUserId(token.getString("user_id", "0.0"))
        val sendHelp = SendHelp(userId, emergencyReason, "https://www.google.com/maps/search/?api=1&query="+userLocation)
        sendHelpApiCall(sendHelp, token)
    }

    private fun getUserId(userId: String?): Long {
        return userId?.substring(0,userId.indexOf("."))!!.toLong()
    }

    private fun sendHelpApiCall(sendHelp: SendHelp, token: SharedPreferences) {
        viewModel = MainViewModel()
        viewModel.sendHelp(sendHelp).observe(this, Observer { networkResource ->
            when (networkResource.status) {
                Status.LOADING -> {
                    Toast.makeText(this, "Sending SOS", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    val message = networkResource.data
                    message?.let {
                        Toast.makeText(this, "SOS Sent", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(this, "SOS not sent", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(this, R.array.Reasons, android.R.layout.simple_spinner_item)
                .also { arrayAdapter ->
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    reasonSpinner.adapter = arrayAdapter
                }
        reasonSpinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        reason = parent?.selectedItem.toString()
    }

    //function to get the last location of the user
    @SuppressLint("MissingPermission")
    private fun getLocation(token: SharedPreferences){
        Log.i("LOGTAG: ", "Inside GetLocation function.")
        if(checkPermissions()){
            if(isLocationServiceEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if(location == null){
                        Log.i("LOGTAG", "loc is null, getNewLocation().")
                        //function to get new location
                        getNewLocation()
                    }else{
                        userLocation = location.latitude.toString()+location.longitude.toString()
                        Toast.makeText(this, userLocation, Toast.LENGTH_SHORT).show()
                        val userId = getUserId(token.getString("user_id", "0.0"))
                        val sendHelp = SendHelp(userId, reason, userLocation)
                        Toast.makeText(this, sendHelp.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this, "Enable Location Service", Toast.LENGTH_SHORT).show()
            }
        }else{
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        Log.i("LOGTAG: ", "Inside getNewLocation().")
        val handlerThread = HandlerThread("RequestLocation")
        handlerThread.start()
        val locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                //super.onLocationResult(locationResult)
                Log.i("LOGTAG: ", "onLocationResult Callback")
                val lastLocation = locationResult.lastLocation
                userLocation = lastLocation.latitude.toString()+lastLocation.longitude.toString()
                Log.i("LOGTAG:", "LOCATION: "+userLocation)
                Toast.makeText(baseContext.applicationContext, userLocation, Toast.LENGTH_SHORT).show()
                val token = getSharedPreferences("User", Context.MODE_PRIVATE)
                val userId = getUserId(token.getString("user_id", "0.0"))
                val sendHelp = SendHelp(userId, reason, userLocation)
                Toast.makeText(baseContext.applicationContext, sendHelp.toString(), Toast.LENGTH_LONG).show()
                fusedLocationProviderClient.removeLocationUpdates(this)
                handlerThread.quit()
            }
        }
        Log.i("LOGTAG: ", "BREAKPOINT")
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, handlerThread.looper
        )
        Log.i("LOGTAG: ", "RequestLocationUpdate()")
    }

    //function to check if permissions have been granted or not
    private fun checkPermissions(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //function to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.RECORD_AUDIO),
            PERMISSION_ID
        )
    }

    //function to check if location service is enabled or not
    private fun isLocationServiceEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("debug: ", "Permissions have been granted.")
            }
        }
    }

    fun setCountdownTimer(view: View) {
        startActivity(Intent(this, TimerActivity::class.java))
    }
}