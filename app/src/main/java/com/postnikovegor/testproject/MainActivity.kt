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
            searchIdButton.requestFocus()
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
        if (user == null) {
            Toast.makeText(context, "Something went wrong, user not loaded", Toast.LENGTH_SHORT)
                .show()
        }
        user!!
        binding.apply {
            if (user.avatar != "") {
                avatarView.visibility = View.VISIBLE
                Picasso.get().load(user.avatar).into(avatarView)
            }

            username.apply {
                visibility = View.VISIBLE
                text = user.fullname
            }

            bioInclude.apply {
                var atLeastOneItemShown = false
                livingPlace.apply living@{
                    if (user.country == null && user.languages.isEmpty()) {
                        visibility = View.GONE
                        return@living
                    }
                    atLeastOneItemShown = true
                    visibility = View.VISIBLE
                    val lives = "Lives in ${user.country}"
                    val speaks = "Speaks ${user.languages.joinToString()}"
                    val textToShow = if (user.country != null && user.languages.isNotEmpty()) {
                        lives + "\n" + speaks
                    } else if (user.country == null) {
                        speaks
                    } else {
                        lives
                    }
                    text = textToShow
                }

                if (user.bio == "") {
                    bioTitle.visibility = View.GONE
                    bioText.visibility = View.GONE
                } else {
                    atLeastOneItemShown = true
                    bioTitle.visibility = View.VISIBLE
                    bioText.visibility = View.VISIBLE
                    bioText.text = user.bio
                }

                if (user.experience == "") {
                    experienceText.visibility = View.GONE
                    experienceTitle.visibility = View.GONE
                } else {
                    atLeastOneItemShown = true
                    experienceText.visibility = View.VISIBLE
                    experienceTitle.visibility = View.VISIBLE
                    experienceText.text = user.experience
                }

                bioCardView.visibility = if (atLeastOneItemShown) View.VISIBLE else View.GONE
            }

            gamificationInclude.apply {
                gamificationCardView.visibility = View.VISIBLE
                user.gamification.apply {
                    timeUserLearning.text = resources.getQuantityString(
                        R.plurals.learning_time_information_days,
                        activeDays,
                        activeDays
                    )
                    topicsCompleted.text =
                        resources.getQuantityString(
                            R.plurals.completed_topics,
                            passedTopics,
                            passedTopics
                        )
                    projectsCompleted.text =
                        resources.getQuantityString(
                            R.plurals.completed_projects,
                            passedProjects,
                            passedProjects
                        )
                    problemsSolved.text =
                        resources.getQuantityString(
                            R.plurals.solved_any_problems,
                            passedProblems,
                            passedProblems
                        )
                    problemsOfTheDaySolved.text = resources.getQuantityString(
                        R.plurals.solved_problems_of_the_day,
                        dailyStepCompletedCount,
                        dailyStepCompletedCount
                    )
                }
                user.postedData.apply {
                    commentsPosted.text =
                        resources.getQuantityString(R.plurals.posted_comments, comment, comment)
                    hintPosted.text =
                        resources.getQuantityString(R.plurals.posted_hints, hint, hint)
                    usefulLinksPosted.text =
                        resources.getQuantityString(
                            R.plurals.posted_useful_links,
                            usefulLink,
                            usefulLink
                        )
                    solutionsPosted.text =
                        resources.getQuantityString(
                            R.plurals.posted_solutions,
                            solutions,
                            solutions
                        )
                }
            }
        }
        Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show()
    }

}