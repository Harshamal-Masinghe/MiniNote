package com.mininote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.mininote.databinding.ActivityUpdateNoteBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: AppDatabase
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "notesapp.db"
        ).build()

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish()
            return
        }

        db.noteDao().getNoteById(noteId).observe(this) { note ->
            binding.updateTitleEditText.setText(note.title)
            binding.updateContentEditText.setText(note.content)
        }

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val updatedNote = Note(noteId, newTitle, newContent)
            lifecycleScope.launch {
                db.noteDao().update(updatedNote)
            }
            finish()
            Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
}