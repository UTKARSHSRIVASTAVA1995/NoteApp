package com.utkarsh.mynotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
// Here Class Note is the Table
// So Class Name is the table name
data class Note(
//Below Attributes are the column of the table
// So there are three Columns id, title, note

    val title: String,
    val note: String

) : Serializable {

    @PrimaryKey(autoGenerate = true) // This Primary key means ID will be generated automatically
    //Here our id is the Primary Key
    var id: Int = 0

}



