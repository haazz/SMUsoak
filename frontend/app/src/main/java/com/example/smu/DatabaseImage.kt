package com.example.smu

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseImage private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "user_profile.db"
        private const val TABLE_NAME = "mail-profile"
        private const val COLUMN_MAIL = "mail"
        private const val COLUMN_IMAGE = "image"

        @Volatile
        private var instance: DatabaseImage?= null

        fun getInstance(context: Context)=
            instance ?: synchronized(DatabaseImage::class.java){
                instance ?: DatabaseImage(context).also{
                    instance =it
                }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_MAIL TEXT PRIMARY KEY, $COLUMN_IMAGE BLOB)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun insertImage(mail: String, image: ByteArray) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(COLUMN_MAIL, mail)
            put(COLUMN_IMAGE, image)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteImage(mail: String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_MAIL = '$mail'", null)
        db.close()
    }

    fun getImage(mail:String): ByteArray? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IMAGE FROM $TABLE_NAME WHERE $COLUMN_MAIL = '$mail'", null)
        var imageByteArray: ByteArray? = null

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_IMAGE)
            imageByteArray = cursor.getBlob(columnIndex)
        }

        cursor.close()
        return imageByteArray
    }

    fun checkExist(mail:String): Boolean{
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_MAIL = '$mail'", null)

        val recordExists = cursor.count > 0

        cursor.close()

        return recordExists
    }

    fun updateImage(mail: String, newImage: ByteArray) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, newImage)
        }
        db.update(TABLE_NAME, values, "$COLUMN_MAIL = '$mail'", null)
    }
}
