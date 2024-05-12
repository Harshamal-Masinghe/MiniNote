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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mininote.AppDatabase
import com.mininote.Note
import com.mininote.R
import com.mininote.UpdateNoteActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotesAdapter(private var notes: LiveData<List<Note>>, private val lifecycleOwner: LifecycleOwner, context: Context) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "notesapp.db"
    ).build()

    init {
        notes.observe(lifecycleOwner, Observer { newNotes ->
            refreshData(newNotes)
        })
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

    override fun getItemCount(): Int = notes.value?.size ?: 0

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes.value?.get(position)
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
        notifyDataSetChanged()
    }
}