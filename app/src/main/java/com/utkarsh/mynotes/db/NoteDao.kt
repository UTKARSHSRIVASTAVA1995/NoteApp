package com.utkarsh.mynotes.db

import androidx.room.*

@Dao
interface NoteDao {


    // Here we are using Suspend Functions because so that we can call this functions in Coroutines Scope
    @Insert
    suspend fun addNote(note: Note)

    @Query("SELECT*FROM note ORDER BY id DESC")
    suspend fun getAllNotes(): List<Note>

    @Insert
    suspend fun addMultipleNote(vararg note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}