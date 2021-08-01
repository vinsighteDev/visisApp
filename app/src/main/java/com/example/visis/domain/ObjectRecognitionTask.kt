package com.example.visis.domain

import android.app.ProgressDialog
import android.os.AsyncTask
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.example.visis.BuildConfig
import com.example.visis.presentation.view.MainActivity
import com.example.visis.utils.Recognizer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class ObjectRecognitionTask<X, Y, Z>(mainActivity: MainActivity) :
    AsyncTask<ByteArray?, String?, String?>() {
    private val API_KEY: String = BuildConfig.ApiKey
    var resultString: String? = null
    var textView: TextView? = null

    private val mainActivity: MainActivity

    private val reconizer: Recognizer? = null

    private val progressDialog: ProgressDialog

    override fun onPreExecute() {
        progressDialog.show()
    }

    override fun doInBackground(vararg inputBytes: ByteArray?): String? {
        try {
            publishProgress("Recognizing")

            val url =
                URL("https://eastus.api.cognitive.microsoft.com/vision/v2.0/analyze?visualFeatures=Objects")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", API_KEY)

            val outputStream = connection.outputStream
            outputStream.write(inputBytes[0])
            outputStream.flush()
            outputStream.close()

            connection.connect()

            val response = StringBuffer()
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?

                while (bufferedReader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                bufferedReader.close()
            }

            return response.toString()
        } catch (e: IOException)
        {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(s: String?) {
        val recognitionText: String

        if (TextUtils.isEmpty(s)) {
            Toast.makeText(mainActivity, "API returned empty result", Toast.LENGTH_SHORT)
        } else {
            progressDialog.dismiss()

            val jsonObject = JsonParser().parse(s).asJsonObject

            val stringResult = StringBuilder()
            val arr = jsonObject.getAsJsonArray("objects")

            recognitionText = if (arr.size() > 0) {
                for (i in 0 until arr.size()) {
                    if (arr[i].asJsonObject["confidence"].asDouble > 0.5) {
                        stringResult.append(arr[i].asJsonObject["object"].asString + " ")
                    }
                }

                if (stringResult.length == 0) {
                    stringResult.append("Sorry, I'm not sure what the object is")
                }

                stringResult.toString()
            } else {
                "No Objects Recognized"
                // textView.setText("No Objects Recognized");
            }
            resultString = recognitionText
            mainActivity.speak(recognitionText)
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        progressDialog.setMessage(values[0])
    }

    init {
        this.mainActivity = mainActivity
        progressDialog = ProgressDialog(mainActivity)
        //textView = mainActivity.findViewById(R.id.txt_result);
    }
}
