package com.mininote

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotesAdapter(notes: LiveData<List<Note>>, lifecycleOwner: LifecycleOwner, context: Context) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "notesapp.db"
    ).build()

    private var _notes = MutableLiveData<List<Note>>()

    init {
        notes.observe(lifecycleOwner) { newNotes ->
            refreshData(newNotes)
        }
    }

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NotesViewHolder(view)
    }

    override fun getItemCount(): Int = _notes.value?.size ?: 0

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = _notes.value?.get(position)
        if (note != null) {
            holder.titleTextView.text = note.title
            holder.contentTextView.text = note.content

            holder.updateButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, UpdateNoteActivity::class.java).apply {
                    putExtra("note_id", note.id)
                }
                holder.itemView.context.startActivity(intent)
            }
            holder.deleteButton.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    db.noteDao().delete(note)
                }
                Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun refreshData(newNotes: List<Note>) {
        _notes.value = newNotes
        notifyDataSetChanged()
    }
}