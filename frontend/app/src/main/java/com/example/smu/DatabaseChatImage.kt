package com.example.smu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseChatImage private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "chat_image.db"
        private const val TABLE_NAME = "image_url"
        private const val COLUMN_URL = "roomId"
        private const val COLUMN_ROOM = "roomId"
        private const val COLUMN_IMAGE = "image"

        @Volatile
        private var instance: DatabaseChatImage?= null

        fun getInstance(context: Context)=
            instance ?: synchronized(DatabaseChatImage::class.java){
                instance ?: DatabaseChatImage(context).also{
                    instance =it
                }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_URL TEXT PRIMARY KEY, $COLUMN_ROOM TEXT, $COLUMN_IMAGE BLOB)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun saveImage(roomId: String, url: String, image: ByteArray) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(COLUMN_ROOM, roomId)
            put(COLUMN_URL, url)
            put(COLUMN_IMAGE, image)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteImage(roomId: String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ROOM = '$roomId'", null)
        db.close()
    }

    fun getImage(url:String): ByteArray? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IMAGE FROM $TABLE_NAME WHERE $COLUMN_URL = '$url'", null)
        var imageByteArray: ByteArray? = null

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_IMAGE)
            imageByteArray = cursor.getBlob(columnIndex)
        }

        cursor.close()
        return imageByteArray
    }
}