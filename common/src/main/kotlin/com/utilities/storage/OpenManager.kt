package com.utilities.storage

import android.os.Environment
import com.google.gson.Gson
import java.io.File
import java.io.FileReader

class OpenManager<T>(private val type: Class<T>) {
    /**
     * opens and reads a JSON object out from the file
     *
     * @param file
     * @return
     */
    fun openObject(file: File?): T? {
        var obj: T? = null
        try {
            val fr = FileReader(file)
            obj = Gson().fromJson(fr, type)
            fr.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }

    fun openObject(fileName: String): T? {
        val f = File(fileName)
        val obj = openObject(f)
        return obj
    }

    /**
     * @param dir
     * @param fileName
     * @return
     */
    fun openObject(dir: File?, fileName: String): T? {
        var obj: T? = null
        val f = File(dir, fileName)
        obj = openObject(f)
        return obj
    }

    /**
     * opens a object from external environment area
     *
     * @param tempDirectoryName
     * @param fileName
     * @return
     */
    fun openObject(tempDirectoryName: String?, fileName: String): T? {
        var obj: T? = null
        val dir = File(Environment.getExternalStorageDirectory(), tempDirectoryName)
        obj = openObject(dir, fileName)
        return obj
    }
}
