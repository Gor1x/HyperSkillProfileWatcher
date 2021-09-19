package com.postnikovegor.testproject

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.postnikovegor.testproject.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

const val API_CONNECTION_TIMEOUT_TIME_MS = 4000

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            searchIdButton.setOnClickListener {
                tryToShowUser(inputIdView.text.toString().toInt())
                Toast.makeText(this@MainActivity, "Loading", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tryToShowUser(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = getUser(id)
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                loadUser(this@MainActivity, user)
            }
        }
    }

    private fun getUser(id: Int): User? {
        val hyperSkillApi = URL("https://hyperskill.org/api/users/${id}?format=json")
        val apiConnection: HttpsURLConnection = hyperSkillApi.openConnection() as HttpsURLConnection
        apiConnection.connectTimeout = API_CONNECTION_TIMEOUT_TIME_MS
        apiConnection.readTimeout = API_CONNECTION_TIMEOUT_TIME_MS
        if (apiConnection.responseCode == 200) {
            val responseBodyReader =
                InputStreamReader(apiConnection.inputStream, StandardCharsets.UTF_8)
            val currentUser = JsonParser.parseReader(responseBodyReader)
                .asJsonObject.getAsJsonArray("users").get(0).asJsonObject
            return Gson().fromJson(currentUser, User::class.java)
        }
        return null
    }

    private fun loadUser(context: Context, user: User?) {
        if (user != null) {
            binding.apply {
                avatarView.visibility = View.VISIBLE
                Picasso.get().load(user.avatar).into(avatarView)

                username.apply {
                    visibility = View.VISIBLE
                    text = user.fullname

                }
            }
            Toast.makeText(context, "Done!", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(context, "Something went wrong, user not loaded", Toast.LENGTH_SHORT)
                .show()
        }
    }

}