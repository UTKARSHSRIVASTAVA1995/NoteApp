package com.utkarsh.mynotes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.utkarsh.mynotes.R
import com.utkarsh.mynotes.db.NoteDataBase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflating Layout fragment_home.
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Initializing RecyclerView in layout with Staggered grid view.
        recycler_view_notes.setHasFixedSize(true)
        recycler_view_notes.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)


        //Displaying All notes in the RecyclerView Staggered grid view using Coroutines.
        launch {
            context?.let {

                val notes = NoteDataBase(it).getNoteDao().getAllNotes()
                recycler_view_notes.adapter = NotesAdapter(notes)
            }
        }

        //Moving Home Fragment from Add Note Fragment.
        button_add.setOnClickListener {
            val action = HomeFragmentDirections.actionAddNote()
            Navigation.findNavController(it).navigate(action)

        }
    }
}