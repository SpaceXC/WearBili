package cn.spacexc.wearbili.utils

import android.util.Base64
import java.math.BigInteger
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

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

    @Throws(Exception::class)
    fun rsaEncrypt(content: String, key: String): String? {
        var key = key
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
        key = key.replace("-----END PUBLIC KEY-----", "")
        val decoded = Base64.decode(key, Base64.DEFAULT)
        val pubKey = KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(decoded)) as RSAPublicKey
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        return Base64.encodeToString(
            cipher.doFinal(content.toByteArray(charset("UTF-8"))),
            Base64.NO_PADDING
        )
    }

    fun getAppSign(type: AppSignType, params: String): String {
        val content = "${params}59b43e04ad6965f34319062b478f83dd"
        return when (type) {
            AppSignType.TYPE_TV -> md5(content)
            else -> ""
        }
    }

    private fun md5(plainText: String): String {
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