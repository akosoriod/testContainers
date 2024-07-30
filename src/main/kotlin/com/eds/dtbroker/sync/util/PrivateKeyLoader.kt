package com.eds.dtbroker.sync.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

object PrivateKeyLoader {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun loadPrivateKey(privateKeyPem: String, privateKeyPassword: String): java.security.PrivateKey {
        val privateKeyContent = privateKeyPem
            .replace("\\n".toRegex(), "")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .trim()

        val encoded = Base64.getDecoder().decode(privateKeyContent)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        val kf = KeyFactory.getInstance("RSA")

        return kf.generatePrivate(keySpec)
    }
}