package com.utkarsh.mynotes.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.Navigation
import com.utkarsh.mynotes.R
import com.utkarsh.mynotes.db.Note
import com.utkarsh.mynotes.db.NoteDataBase
import com.utkarsh.mynotes.util.toast
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment : BaseFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var note: Note? = null
    private var mUserReminder: Date? = null

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

        // Below is the Time click where we are setting the Time in Edit Text
        et_date.setOnClickListener {
            hideKeyBoard(et_date)
            getDate()
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

    //Below Method We are getting the Date and Time
    private fun getDate() {
        val calendar = Calendar.getInstance()
        var date = Date()
        calendar.time = date
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog.newInstance(
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(context)
        )
        activity?.let { timePickerDialog.show(it.supportFragmentManager, "TimeFragment") }
    }

    //Below Two method onDateSet and onTimeSet is from wdullaer Dependencyto get Date and Time from Calendar.
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {}

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        setTime(hourOfDay, minute)
    }

    private fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        if (mUserReminder != null) {
            calendar.time = mUserReminder
        }
        var year: Int = calendar.get(Calendar.YEAR)
        var month: Int = calendar.get(Calendar.MONTH)
        var day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        mUserReminder = calendar.time
        setTimeEditText()
    }

    private fun setTimeEditText() {
        var dateFormat: String
        if (DateFormat.is24HourFormat(context)) {
            dateFormat = "k:mm"
        } else {
            dateFormat = "h:mm a"
        }
        et_date.setText(mUserReminder?.let { formatDate(dateFormat, it) })
    }

    private fun formatDate(formatString: String, dateFormat: Date): String? {
        var simpledateFormat = SimpleDateFormat(formatString)
        return simpledateFormat.format(dateFormat)
    }

    private fun hideKeyBoard(et: EditText) {
        val im: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(et.windowToken, 0)
    }

}
