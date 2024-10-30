package com.utilities.encryption

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Obsidian47 on 2/26/14.
 */
object Encrypt {
    fun MD5(str: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(str.toByteArray())
            val sb = StringBuffer()
            for (i in array.indices) {
                sb.append(Integer.toHexString((array[i].toInt() and 0xFF) or 0x100).substring(1, 3))
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
        }
        return null
    }
}
