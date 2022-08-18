package cn.spacexc.wearbili.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by XC-Qan on 2022/7/28.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */


object EncryptUtils {
    enum class AppSignType {
        TYPE_COMMON,
        TYPE_TV
    }

    fun getAppSign(type: AppSignType, params: String): String {
        val content = "${params}59b43e04ad6965f34319062b478f83dd"
        return when (type) {
            AppSignType.TYPE_TV -> md5(content)
            else -> ""
        }
    }

    fun md5(plainText: String): String {
        val secretBytes: ByteArray? = try {
            val md = MessageDigest.getInstance("MD5")
            md.update(plainText.toByteArray())
            md.digest()
        } catch (e: NoSuchAlgorithmException) {
            null
        }
        val md5code = StringBuilder(BigInteger(1, secretBytes).toString(16))
        for (i in 0 until 32 - md5code.length) {
            md5code.insert(0, "0")
        }
        return md5code.toString()
    }
}