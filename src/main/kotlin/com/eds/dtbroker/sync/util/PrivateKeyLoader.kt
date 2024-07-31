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

    fun loadPrivateKey(privateKeyPem: String, privateKeyPassword: String?): PrivateKey {
        val pemParser = PEMParser(StringReader(privateKeyPem))
        val converter = JcaPEMKeyConverter().setProvider("BC")
        val obj = pemParser.readObject()

        return when (obj) {
            is PEMEncryptedKeyPair -> {
                val decryptorProvider = JcePEMDecryptorProviderBuilder().build(privateKeyPassword?.toCharArray())
                val keyPair = obj.decryptKeyPair(decryptorProvider)
                converter.getPrivateKey(keyPair.privateKeyInfo)
            }
            is PEMKeyPair -> converter.getPrivateKey(obj.privateKeyInfo)
            else -> throw IllegalArgumentException("Unsupported key format")
        }
    }
}