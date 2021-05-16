package com.utkarsh.mynotes.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.navigation.Navigation
import com.utkarsh.mynotes.R
import com.utkarsh.mynotes.db.Note
import com.utkarsh.mynotes.db.NoteDataBase
import com.utkarsh.mynotes.util.toast
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch

class AddNoteFragment : BaseFragment() {

    private var note: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true) // This will display options menu for deleting the Notes
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Below we are setting the note to edit it, After clicking on note from Home Fragment it is going to edit text of Add Fragment
        arguments?.let {
            note = AddNoteFragmentArgs.fromBundle(it).note
            et_text_title.setText(note?.title)
            et_text_note.setText(note?.note)
        }

        button_save.setOnClickListener { view ->

            // Below we are getting Title and Note from our edit text (UI).
            val noteTitle = et_text_title.text.toString().trim()
            val noteBody = et_text_note.text.toString().trim()

            // Validation if Note Title is empty
            if (noteTitle.isEmpty()) {
                et_text_title.error = "Title Required"
                et_text_title.requestFocus()
                return@setOnClickListener
            }
            // Validation if Note Body is empty.
            if (noteBody.isEmpty()) {
                et_text_title.error = "Title Required"
                et_text_title.requestFocus()
                return@setOnClickListener
            }

            // If validation succeed we will return Note Class where we have to pass title and note.
            // Means note Tile and note Body to get the text from the Edit Text.
            // Below we are Calling the NoteDataBase in Coroutines.
            launch {

                context?.let {
                    // Initialized mnote variable to edit it.
                    val mnote = Note(noteTitle, noteBody)

                    if (note == null) {
                        NoteDataBase(it).getNoteDao().addNote(mnote)
                        it.toast("Note Saved")
                    } else {
                        //Below we are Updating the Note
                        mnote.id = note!!.id
                        NoteDataBase(it).getNoteDao().updateNote(mnote)
                        it.toast("Note Updated")
                    }

                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
    }

    // Below we are Deleting the Note.
    // Displaying alert dialog to delete it.
    private fun deleteNote() {
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure")
            setMessage("You cannot undo this operation")
            setPositiveButton("Yes") { _, _ ->
                launch {
                    NoteDataBase(context).getNoteDao().deleteNote(note!!)

                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
            setNegativeButton("No") { _, _ ->
            }
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete -> if (note != null) deleteNote()
            else context?.toast("Cannot Delete")
        }
        return super.onOptionsItemSelected(item)
    }

    // It will create options bar in appbar with delete option.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }
}
