package com.eds.dtbroker.sync.util

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import java.io.FileReader
import java.nio.file.Paths
import java.security.PrivateKey
import java.security.Security

object PrivateKeyLoader {

    fun loadPrivateKey(privateKeyPath: String, password: String): PrivateKey {
        var privateKeyInfo: PrivateKeyInfo? = null
        Security.addProvider(BouncyCastleProvider())
        // Read an object from the private key file.
        val pemParser = PEMParser(FileReader(Paths.get(privateKeyPath).toFile()))
        val pemObject = pemParser.readObject()
        if (pemObject is PKCS8EncryptedPrivateKeyInfo) {
            // Handle the case where the private key is encrypted.
            val passphrase = password
            val pkcs8Prov = JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase.toCharArray())
            privateKeyInfo = pemObject.decryptPrivateKeyInfo(pkcs8Prov)
        } else if (pemObject is PrivateKeyInfo) {
            // Handle the case where the private key is unencrypted.
            privateKeyInfo = pemObject as PrivateKeyInfo
        }
        pemParser.close()
        val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
        return converter.getPrivateKey(privateKeyInfo)
    }
}