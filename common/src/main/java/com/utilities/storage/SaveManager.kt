package com.utilities.storage

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.utilities.interfaces.ISave
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter

class SaveManager<T> {
    /**
     * Saves an object to a file
     *
     *
     * if file doesn't exist it will create it
     *
     *
     * if obj instanceof ISave
     * this will call readyForSave()
     *
     * @param obj
     * @param fileToSaveTo
     * @return
     */
    fun saveObject(obj: T, fileToSaveTo: File): Boolean {
        var saved = false
        try {
            if (!fileToSaveTo.exists()) {
                fileToSaveTo.createNewFile()
            }
            val fos = FileOutputStream(fileToSaveTo)
            val pw = PrintWriter(fos)
            if (obj is ISave) (obj as ISave).readyForSave()

            pw.print(Gson().toJson(obj))
            pw.flush()
            fos.close()
            saved = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saved
    }

    /**
     * Saves an object to a file in given directory
     *
     *
     * if directory(s) don't exist it will create it or them
     *
     *
     * if file doesn't exist it will create it
     *
     * @param obj      - T object to save
     * @param dir      - directory of file
     * @param fileName - filename to save to
     * @return
     */
    fun saveObject(obj: T, dir: File, fileName: String?): Boolean {
        var saved = false
        try {
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            saved = saveObject(obj, file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saved
    }

    /**
     * Saves an Object to a file with the given name in the given directory.
     *
     *
     * if file doesn't exist it will create it
     *
     * @param obj
     * @param directoryName
     * @param fileName
     * @return
     */
    fun saveObject(obj: T, directoryName: String?, fileName: String?): Boolean {
        var saved = false
        try {
            val dir = File(directoryName)
            if (!dir.exists()) {
                dir.mkdir()
            } else if (dir.isDirectory) {
                saved = saveObject(obj, dir, fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saved
    }

    /**
     * Saves the obj to a file with the name given in to the directory that Context.getFileStreamPath(filename) gives you.
     *
     *
     * if file doesn't exist it will create it
     *
     * @param obj
     * @param tempFileName
     * @return
     */
    fun saveObject(obj: T, ctx: Context, tempFileName: String?): Boolean {
        var saved = false
        val fileToSaveTo = ctx.getFileStreamPath(tempFileName)
        saved = saveObject(obj, fileToSaveTo)
        return saved
    }

    /**
     * Saves in External Storage to a Directory
     *
     *
     * if file doesn't exist it will create it
     *
     * @param obj
     * @param nameOfFile
     * @return
     */
    fun saveTempFile(obj: T, tempDirectoryName: String?, nameOfFile: String?): File? {
        var temp: File? = null
        try {
            val dir = File(Environment.getExternalStorageDirectory(), tempDirectoryName)
            dir.mkdirs()
            temp = File(dir, nameOfFile)
            temp.createNewFile()
            saveObject(obj, temp)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return temp
    }
}
