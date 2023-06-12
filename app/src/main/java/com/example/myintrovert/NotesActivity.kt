package com.example.myintrovert

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

import com.example.myintrovert.service.ApiService
import com.example.myintrovert.service.NotesPayload
import com.example.myintrovert.service.NotesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotesActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var linearLayoutResult: LinearLayout


    // Retrofit instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.43.110/") // Replace with your API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // ApiService instance
    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)
        linearLayoutResult = findViewById(R.id.linearLayoutResult)
        buttonSave.setBackgroundResource(android.R.color.white)

        fetchNotes() // Fetch and display existing notes on activity creation

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val summary = editTextContent.text.toString()

            val call = apiService.saveNote(title, summary)
            call.enqueue(object : Callback<NotesResponse> {
                override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                    if (response.isSuccessful) {
                        val notesResponse = response.body()
                        if (notesResponse != null && notesResponse.success) {
                            // Note saved successfully, perform UI updates if needed
                            val cardView = createNoteCard(title, summary)
                            linearLayoutResult.addView(cardView, 0) // Add card view at the top of the layout

                            // Reset input fields
                            editTextTitle.text.clear()
                            editTextContent.text.clear()
                        } else {
                            // Note saving failed, handle the error
                            Toast.makeText(this@NotesActivity, "Failed to save note", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle API error
                        Toast.makeText(this@NotesActivity, "API error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                    // Handle network or unexpected errors
                    Toast.makeText(this@NotesActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchNotes() {
        val call = apiService.getNotes() // Update the API endpoint for fetching notes
        call.enqueue(object : Callback<List<NotesPayload>> {
            override fun onResponse(call: Call<List<NotesPayload>>, response: Response<List<NotesPayload>>) {
                if (response.isSuccessful) {
                    val notesList = response.body()
                    notesList?.let {
                        for (note in it) {
                            val cardView = createNoteCard(note.title, note.summary)
                            linearLayoutResult.addView(cardView)
                        }
                    }
                } else {
                    // Handle API error
                    Toast.makeText(this@NotesActivity, "API error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NotesPayload>>, t: Throwable) {
                // Handle network or unexpected errors
                Toast.makeText(this@NotesActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createNoteCard(title: String, summary: String): CardView {
        val cardView = CardView(this@NotesActivity)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.card_margin_bottom))
        cardView.layoutParams = layoutParams
        cardView.cardElevation = resources.getDimension(R.dimen.card_elevation)
        cardView.setContentPadding(
            resources.getDimensionPixelSize(R.dimen.card_content_padding),
            resources.getDimensionPixelSize(R.dimen.card_content_padding),
            resources.getDimensionPixelSize(R.dimen.card_content_padding),
            resources.getDimensionPixelSize(R.dimen.card_content_padding)
        )

        val linearLayout = LinearLayout(this@NotesActivity)
        linearLayout.orientation = LinearLayout.VERTICAL
        cardView.addView(linearLayout)

        val titleTextView = TextView(this@NotesActivity)
        titleTextView.text = title
        titleTextView.textSize = 22f
        titleTextView.setTypeface(null, Typeface.BOLD)
        linearLayout.addView(titleTextView)

        val summaryTextView = TextView(this@NotesActivity)
        summaryTextView.text = summary
        summaryTextView.textSize = 16f
        linearLayout.addView(summaryTextView)

        return cardView
    }



}
