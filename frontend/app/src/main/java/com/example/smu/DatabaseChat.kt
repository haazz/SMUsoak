package com.example.smu

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseChat private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "chat_database.db"
        private const val TABLE_NAME = "chat_messages"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ROOM_ID = "room_id"
        private const val COLUMN_SENDER = "sender"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_FLAG = "flag"

        @Volatile
        private var instance: DatabaseChat?= null

        fun getInstance(context: Context)=
            instance ?: synchronized(DatabaseChat::class.java){
                instance ?: DatabaseChat(context).also{
                    instance =it
                }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_ROOM_ID TEXT, $COLUMN_SENDER TEXT, $COLUMN_MESSAGE TEXT, $COLUMN_TIME TEXT, $COLUMN_FLAG INTEGER)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun insertMessage(roomId: String, sender: String, message: String, timestamp: String, flag: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(COLUMN_ROOM_ID, roomId)
            put(COLUMN_SENDER, sender)
            put(COLUMN_MESSAGE, message)
            put(COLUMN_TIME, timestamp)
            put(COLUMN_FLAG, flag)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteChatroom(roomId: String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ROOM_ID = '$roomId'", null)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllMessages(roomId:String): MutableList<ChatMessage> {
        val messages: MutableList<ChatMessage> = mutableListOf()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ROOM_ID = '$roomId'", null)
        if (cursor.count==0)
            return messages

        cursor.use {
            while (it.moveToNext()) {
                val sender = it.getString(2)
                val message = it.getString(3)
                val timestamp = it.getString(4)
                val flag = it.getInt(5)
                val chatMessage = ChatMessage(sender, message, timestamp, flag)
                messages.add(chatMessage)
            }
        }
        db.close()
        return messages
    }
}
