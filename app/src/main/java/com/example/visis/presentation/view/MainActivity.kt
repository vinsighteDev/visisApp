package com.example.visis.presentation.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.visis.R
import com.example.visis.utils.Recognizer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wonderkiln.camerakit.*
import java.util.*

class MainActivity : AppCompatActivity(),  CameraKitEventListener, TextToSpeech.OnInitListener{
    private var cameraView: CameraView? = null
    private var cameraButton: Button? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var flashSwitch: Switch? = null
    private val mDetector: GestureDetectorCompat? = null

    private var textToSpeech: TextToSpeech? = null

    private var recognizer: Recognizer? = null

    private val mySpeechRecognizer: SpeechRecognizer? = null
    private val DEBUG_TAG = "Gestures"
    private var speech_recog = false
    private var voice_text_recog = false
    private val voice_scene_recog = false
    private val voice_object_recog = false
    private val voice_color_recog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initRecognitionElements()
    }

    private fun initUI() {
        setContentView(R.layout.bottom_nav)
        flashSwitch = findViewById(R.id.flashSwitch)
        flashSwitch?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                speak("flashlight is on")
            } else {
                speak("flashlight is off")
            }
        })
        initNav()
        initCamera()
    }

    private fun initNav() {
        bottomNavigationView = findViewById(R.id.bottomBar)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_text
        )
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavigationView!!, navController)
        bottomNavigationView?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            speak(item.toString())
            true
        })
    }

    private fun initCamera() {
        cameraButton = findViewById(R.id.cameraBtn)
        cameraView = findViewById(R.id.camView)
        cameraButton?.setOnClickListener(

            {
                if (textToSpeech != null) {
                    textToSpeech?.stop()
                }
                cameraView?.start()

                if (cameraButton?.getText() == "Stop Scan") {
                    speak("Stopping text scan")
                    cameraButton?.setText("Scan Text")
                } else
                {
                    speak("Scanning")
                    cameraButton?.setText("Stop Scan")
                }
                if (flashSwitch!!.isChecked) {
                    cameraView?.setFlash(CameraKit.Constants.FLASH_ON)
                } else {
                    cameraView?.setFlash(CameraKit.Constants.FLASH_OFF)
                }
                cameraView?.captureImage()
            })
        cameraButton?.setOnLongClickListener(OnLongClickListener {
            textToSpeech!!.stop()
            true
        })
    }

    private fun initRecognitionElements() {
        initTextToSpeech()
        recognizer = Recognizer(this)
        cameraView!!.addCameraKitListener(this)
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }


    override fun onImage(cameraKitImage: CameraKitImage) {
        var bitmap = cameraKitImage.bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap!!, cameraView!!.width, cameraView!!.height, false)

        val number = bottomNavigationView!!.selectedItemId

        if (!speech_recog) {
            cameraView!!.stop()

            if (number == R.id.navigation_text) {
                recognizer!!.doTextRecognition(bitmap)
            }
            if (number == R.id.navigation_scene) {
                recognizer!!.doSceneRecognition(bitmap)
            }
            if (number == R.id.navigation_object) {
                recognizer!!.doObjectRecognition(bitmap)
            }
            if (number == R.id.navigation_color) {
                recognizer!!.doColorRecognition(bitmap)
            }
        } else {
            if (voice_text_recog) {
                recognizer!!.doTextRecognition(bitmap)
                voice_text_recog = false
            }
            if (voice_scene_recog) {
                recognizer!!.doSceneRecognition(bitmap)
                voice_text_recog = false
            }
            if (voice_object_recog) {
                recognizer!!.doObjectRecognition(bitmap)
                voice_text_recog = false
            }
            if (voice_color_recog) {
                recognizer!!.doColorRecognition(bitmap)
                voice_text_recog = false
            }
            speech_recog = false
        }
    }

    override fun onInit(status: Int) {
        val lanResult = textToSpeech!!.setLanguage(Locale.US)

        if (lanResult == TextToSpeech.LANG_MISSING_DATA || lanResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("Language", "This Language is not supported")
        } else {
            Log.i("Language", "Language set to english")
        }

        if (status == TextToSpeech.ERROR) {
            Log.e("Initialization", "Text to speech initialization Failed!")
        } else {
            Log.i("Initialization", "Text to speech was initialized.")
        }
        speak(
            "Welcome to the visis app! I am your virtual assistant, and i can help you recognize texts. " +
                    " tap on the screen once to activate text scan and tap on the screen the second time to deactivate text scan"
        )
    }

    fun speak(text: String) {
        if (textToSpeech == null || "" == text) {
            assert(textToSpeech != null)
            textToSpeech!!.speak(
                "Please try again",
                TextToSpeech.QUEUE_FLUSH,
                null,
                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
            )
        } else {
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
            )
        }
    }


    override fun onResume() {
        super.onResume()
        cameraView!!.start()
        cameraButton!!.text = "Tap to scan"
    }

    override fun onPause() {
        cameraView!!.stop()
        if (textToSpeech != null) {
            textToSpeech?.stop()
        }
        super.onPause()
    }

    override fun onEvent(cameraKitEvent: CameraKitEvent?) {}

    override fun onError(cameraKitError: CameraKitError?) {}

    override fun onVideo(cameraKitVideo: CameraKitVideo?) {}
}