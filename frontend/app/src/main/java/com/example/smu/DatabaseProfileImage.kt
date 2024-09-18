package com.example.smu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseProfileImage private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "user_profile.db"
        private const val TABLE_NAME = "mail_profile"
        private const val COLUMN_MAIL = "mail"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_TIME = "update_time"

        @Volatile
        private var instance: DatabaseProfileImage?= null

        fun getInstance(context: Context)=
            instance ?: synchronized(DatabaseProfileImage::class.java){
                instance ?: DatabaseProfileImage(context).also{
                    instance =it
                }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_MAIL TEXT PRIMARY KEY, $COLUMN_IMAGE TEXT, $COLUMN_TIME TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun insertImage(mail: String, image: String, time: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(COLUMN_MAIL, mail)
            put(COLUMN_IMAGE, image)
            put(COLUMN_TIME, time)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteImage(mail: String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_MAIL = '$mail'", null)
        db.close()
    }

    fun getImage(mail:String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IMAGE FROM $TABLE_NAME WHERE $COLUMN_MAIL = '$mail'", null)
        var imageUrl: String? = null

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_IMAGE)
            imageUrl = cursor.getString(columnIndex)
        }

        cursor.close()
        return imageUrl
    }

    fun getTime(mail:String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_TIME FROM $TABLE_NAME WHERE $COLUMN_MAIL = '$mail'", null)

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_TIME)
            return cursor.getString(columnIndex)
        }
        return null
    }

    fun checkExist(mail:String): Boolean{
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_MAIL = '$mail'", null)

        val recordExists = cursor.count > 0

        cursor.close()

        return recordExists
    }

    fun updateImage(mail: String, newImage: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, newImage)
        }
        db.update(TABLE_NAME, values, "$COLUMN_MAIL = '$mail'", null)
    }
}
