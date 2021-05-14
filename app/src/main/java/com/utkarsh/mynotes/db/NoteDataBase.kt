package com.utkarsh.mynotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Note::class], version = 1)
abstract class NoteDataBase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object {

        @Volatile
        private var instance: NoteDataBase? = null
        private val Lock = Any()

        // If instance is not null it will return Note Database.
        operator fun invoke(context: Context) = instance ?: synchronized(Lock) {
            instance ?: buildDatabase(context).also {
                instance = it
            }//above line if instance is null it will create the database means build the Database.
        }

        //Build the Database if not null.
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDataBase::class.java,
            "noteDatabase"
        ).build()
    }
}