package com.example.mytv2
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.example.mytv2.Movie
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

data class MovieResponse(
    @SerializedName("docs") val docs: List<Movie>
)

data class Movie(
    @SerializedName("alternativeName") val alternativeName: String,
    @SerializedName("year") val year: Int,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("poster") val poster: Poster
)

data class Genre(
    @SerializedName("name") val name: String
)

data class Poster(
    @SerializedName("url") val url: String
)

class QuestsActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val gson = Gson()
    private lateinit var gridLayout: GridLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quests)

        gridLayout = findViewById(R.id.gridLayout)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        val genreSpinner: Spinner = findViewById(R.id.genreSpinner)


        val genres = listOf("All", "Драма", "Комедия", "Боевик", "Криминал")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genreSpinner.adapter = adapter

        // Set up Spinner listener
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGenre = genres[position]
                fetchMovies(selectedGenre)
                Log.d("QuestsActivity", "Response: $selectedGenre")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Default to "All" if nothing is selected
                fetchMovies("All")
            }
        }

        searchButton.setOnClickListener {
            fetchMovies()
        }
    }
    private fun fetchMovies() {
        gridLayout.removeAllViews() // Очистка GridLayout перед добавлением новых элементов

        val itemWidth = 600// Ширина для 2 колонок
        val itemHeight = 600 // Высота для 2 строк

        val searchQuery = searchEditText.text.toString().trim() // Получение текста из EditText
        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            return // Если строка пустая, выходим из функции
        }

        Log.e("QuestsActivity", searchQuery)

        val request = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie/search?page=1&limit=10&query=${searchQuery}")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "R4H3X4H-A7S4TTE-HQVBE2P-CZGF2S6")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("QuestsActivity", "Failed to fetch data", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("QuestsActivity", "Response: $responseData") // Логирование ответа

                    // Десериализация JSON в объект MovieResponse
                    val movieResponse = gson.fromJson(responseData, MovieResponse::class.java)

                    runOnUiThread {
                        // Проверка на наличие фильмов и фильтрация по жанрам
                        var rows = 0
                        var columns = 0

                        movieResponse.docs.forEach { movie ->
                            Log.d("QuestsActivity", "Response: ${movie.genres}")

                                if (rows == 0 && columns == 0){
                                    val linearLayout = LinearLayout(this@QuestsActivity).apply {
                                        orientation = LinearLayout.VERTICAL
                                        layoutParams = GridLayout.LayoutParams().apply {
                                            rowSpec = GridLayout.spec(rows)
                                            columnSpec = GridLayout.spec(columns)
                                        }
                                        setPadding(16, 16, 16, 16)
                                        setBackgroundResource(R.drawable.ramka)
                                        val layoutParams = layoutParams as GridLayout.LayoutParams
                                        layoutParams.setMargins(8, 8, 8, 8)
                                    }

                                    val imageView = ImageView(this@QuestsActivity).apply {
                                        layoutParams =
                                            LinearLayout.LayoutParams(itemWidth, itemHeight)
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        movie.poster?.let { // Используйте movie.poster напрямую
                                            Picasso.get().load(it.url)
                                                .into(this) // Загрузка изображения с помощью Picasso
                                        }
                                    }

                                    linearLayout.addView(imageView)

                                    val titleTextView = TextView(this@QuestsActivity).apply {
                                        text = movie.alternativeName // Используйте alternativeName
                                        setTextColor(Color.parseColor("#FFFFFF"))
                                        layoutParams =
                                            LinearLayout.LayoutParams(itemWidth, itemHeight / 10)
                                        gravity = Gravity.CENTER
                                    }

                                    val yearTextView = TextView(this@QuestsActivity).apply {
                                        text = movie.year.toString()
                                        setTextColor(Color.parseColor("#FFFFFF"))
                                        layoutParams =
                                            LinearLayout.LayoutParams(itemWidth, itemHeight / 10)
                                        gravity = Gravity.CENTER
                                    }

                                    linearLayout.addView(titleTextView)
                                    linearLayout.addView(yearTextView)

                                    gridLayout.addView(linearLayout)
                                }
                            else {
                                    val linearLayout = LinearLayout(this@QuestsActivity).apply {
                                        orientation = LinearLayout.VERTICAL
                                        layoutParams = GridLayout.LayoutParams().apply {
                                            rowSpec = GridLayout.spec(rows)
                                            columnSpec = GridLayout.spec(columns)
                                        }
                                        setPadding(16, 16, 16, 16)
                                        val layoutParams = layoutParams as GridLayout.LayoutParams
                                        layoutParams.setMargins(8, 8, 8, 8)
                                    }

                                    val imageView = ImageView(this@QuestsActivity).apply {
                                        layoutParams =
                                            LinearLayout.LayoutParams(itemWidth, itemHeight)
                                    }

                                    linearLayout.addView(imageView)

                                    val titleTextView = TextView(this@QuestsActivity).apply {
                                        layoutParams =
                                            LinearLayout.LayoutParams(itemWidth, itemHeight / 10)
                                        gravity = Gravity.CENTER
                                    }

                                    val yearTextView = TextView(this@QuestsActivity).apply {
                                        layoutParams =
                                            LinearLayout.LayoutParams(itemWidth, itemHeight / 10)
                                        gravity = Gravity.CENTER
                                    }

                                    linearLayout.addView(titleTextView)
                                    linearLayout.addView(yearTextView)

                                    gridLayout.addView(linearLayout)
                                }

                                columns += 1
                                if (columns == 3) {
                                    rows += 1
                                    columns = 0
                                }
                            }
                        }

                } else {
                    Log.e("QuestsActivity", "Response not successful: ${response.code}")
                }
            }
        })
    }

    private fun fetchMovies(selectedGenre: String) {
        gridLayout.removeAllViews() // Очистка GridLayout перед добавлением новых элементов

        val itemWidth = 1190 / 2 // Ширина для 3 колонок
        val itemHeight = 600// Высота для 2 строк

        val request = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie/search?page=1&limit=10&genres.name=${selectedGenre}") // Добавление жанра в запрос
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "R4H3X4H-A7S4TTE-HQVBE2P-CZGF2S6")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("QuestsActivity", "Failed to fetch data", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("QuestsActivity", "Response: $responseData") // Логирование ответа

                    // Десериализация JSON в объект MovieResponse
                    val movieResponse = gson.fromJson(responseData, MovieResponse::class.java)

                    runOnUiThread {
                        // Проверка на наличие фильмов и фильтрация по жанрам
                        var rows = 0
                        var columns = 0

                        movieResponse.docs.forEach { movie ->
                            Log.d("QuestsActivity", "Response: ${movie.genres}")

                            if (selectedGenre == "All" || movie.genres.any {
                                    it.name.equals(
                                        selectedGenre,
                                        ignoreCase = true
                                    )
                                }) {
                                val linearLayout = LinearLayout(this@QuestsActivity).apply {
                                    orientation = LinearLayout.VERTICAL
                                    layoutParams = GridLayout.LayoutParams().apply {
                                        rowSpec = GridLayout.spec(rows)
                                        columnSpec = GridLayout.spec(columns)
                                    }
                                    setPadding(16, 16, 16, 16)
                                    setBackgroundResource(R.drawable.ramka)
                                    val layoutParams = layoutParams as GridLayout.LayoutParams
                                    layoutParams.setMargins(8, 8, 8, 8)
                                }

                                val imageView = ImageView(this@QuestsActivity).apply {
                                    layoutParams =
                                        LinearLayout.LayoutParams(itemWidth, itemHeight)
                                    scaleType = ImageView.ScaleType.FIT_CENTER
                                    movie.poster?.let { // Используйте movie.poster напрямую
                                        Picasso.get().load(it.url)
                                            .into(this) // Загрузка изображения с помощью Picasso
                                    }
                                }

                                linearLayout.addView(imageView)

                                val titleTextView = TextView(this@QuestsActivity).apply {
                                    text = movie.alternativeName // Используйте alternativeName
                                    setTextColor(Color.parseColor("#FFFFFF"))
                                    layoutParams =
                                        LinearLayout.LayoutParams(itemWidth, itemHeight / 10)
                                    gravity = Gravity.CENTER
                                }

                                val yearTextView = TextView(this@QuestsActivity).apply {
                                    text = movie.year.toString()
                                    setTextColor(Color.parseColor("#FFFFFF"))
                                    layoutParams =
                                        LinearLayout.LayoutParams(itemWidth, itemHeight / 10)
                                    gravity = Gravity.CENTER
                                }

                                linearLayout.addView(titleTextView)
                                linearLayout.addView(yearTextView)

                                gridLayout.addView(linearLayout)

                                columns += 1
                                if (columns == 3) {
                                    rows += 1
                                    columns = 0
                                }
                            }
                        }
                    }
                } else {
                    Log.e("QuestsActivity", "Response error: ${response.code}")
                }
            }
        })
    }
}

//    fun parseMovieJson(responseData: String?): Movie? {
//        if (responseData == null) return null
//
//        return try {
//            val jsonObject = JSONObject(responseData)
//            val docsArray = jsonObject.getJSONArray("docs") // Access the "docs" array
//            if (docsArray.length() > 0) {
//                val movieObject = docsArray.getJSONObject(0) // Get the first movie object
//                val title = movieObject.getString("alternativeName")
//                val year = movieObject.getInt("year")
//                val poster = movieObject.getString("poster")
//
//                val genres = movieObject.getJSONArray("genres").let { array ->
//                    List(array.length()) { index ->
//                        val genreObject = array.getJSONObject(index)
//                        Genre(genreObject.getString("name"))
//                    }
//                }
//
//                Movie(title, year, poster, genres)
//            } else {
//                null // Handle case where there are no movies
//            }
//        } catch (e: JSONException) {
//            Log.e("QuestsActivity", "JSON parsing error", e)
//            null
//        }
//    }


