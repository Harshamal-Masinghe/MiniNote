package com.mininote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.mininote.databinding.ActivityAddNoteBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "notesapp.db"
        ).build()

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val note = Note(0, title, content)
            lifecycleScope.launch {
                db.noteDao().insert(note)
            }
            finish()
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        }
    }
}