package coderpie.res.gpt_voice

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.mlkit.nl.languageid.LanguageIdentification
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var start_Button: Button
    lateinit var show_text: TextView
    lateinit var play_text: Button
    //var textToSpeech: TextToSpeech? = null
    private lateinit var textToSpeech: TextToSpeech
    lateinit var language: String


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            start_Button = findViewById(R.id.Start_Voice_Button)
            show_text = findViewById(R.id.Show_Text_EditText)
            play_text = findViewById(R.id.Play_Voice_Button)
            language = ""
            textToSpeech = TextToSpeech(this, this)
            start_Button.setOnClickListener {
                // changing the color of mic icon, which
                // indicates that it is currently listening
                start_Button.setBackgroundColor(Color.parseColor("#FF5757")) // #FF0E87E7
                start_Button.text = "Speak Now"
                startSpeechToText()
                identifyLanguageWithStringInput(show_text.text.toString())
            }

            play_text.visibility = View.GONE

            play_text.setOnClickListener {

                when(language){
                    "en" -> textToSpeech.setLanguage(Locale.ENGLISH)
                    "de" -> textToSpeech.setLanguage(Locale.GERMANY)
                }
                textToSpeech.setSpeechRate(0.9F)


                readMyText(findViewById<TextView>(R.id.Show_Text_EditText).text.toString())
                //identifyLanguageWithStringInput()
            }


            requestPermissions(
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.INTERNET
                ), 0)

        }
    //onCreate End


//Text to Speech
    private fun readMyText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
       if (status == TextToSpeech.SUCCESS){

          var result = textToSpeech.setLanguage(Locale.ENGLISH)

           if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
               Log.e("TextToSpeech", "Sprache nicht verfügbar")
           }
       } else {
           Log.e("TextToSpeech", "Initialisierung fehlgeschlagen")
       }
    }

    override fun onDestroy() {
            textToSpeech.stop()
            textToSpeech.shutdown()
        super.onDestroy()

    }




//Speech to Text
    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {
                // changing the color of our mic icon to
                // gray to indicate it is not listening
                start_Button.setBackgroundColor(Color.parseColor("#FF007FAC")) // #FF0E87E7
                start_Button.text = "Start"
            }

            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    // attaching the output
                    // to our textview
                    show_text.text = result[0]
                    start_Button.setBackgroundColor(Color.parseColor("#FF007FAC")) // #FF0E87E7
                    start_Button.text = "Start"
                    play_text.visibility = View.VISIBLE
                    identifyLanguageWithStringInput(show_text.text.toString())

                }
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}

        })
        speechRecognizer.startListening(speechRecognizerIntent)

    }

    fun identifyLanguageWithStringInput(text: String) {
        // [START identify_languages]
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.i("ML TAG", "Can't identify language.")
                } else {
                    Log.i("ML TAG", "Language: $languageCode")
                    language = languageCode
                    Log.i("ML TAG", "Language lateinit: $language")

                }
            }
            .addOnFailureListener {
                // Model couldn’t be loaded or other internal error.
                // ...
            }
        // [END identify_languages]
    }



}





