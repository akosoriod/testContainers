package com.eds.dtbroker.sync.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import java.io.StringReader
import java.security.PrivateKey

object PrivateKeyLoader {


    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun loadPrivateKey(privateKeyPem: String, privateKeyPassword: String): PrivateKey {
       var privateKeyInfo: PrivateKeyInfo? = null

       val pemParser = PEMParser(StringReader(privateKeyPem))
       val pemObject = pemParser.readObject()

       if (pemObject is PKCS8EncryptedPrivateKeyInfo) {
           val pkcs8Prov = JceOpenSSLPKCS8DecryptorProviderBuilder().build(privateKeyPassword.toCharArray())
           privateKeyInfo = pemObject.decryptPrivateKeyInfo(pkcs8Prov)
       } else if (pemObject is PrivateKeyInfo) {
           privateKeyInfo = pemObject as PrivateKeyInfo
       }
       pemParser.close()
       val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
       return converter.getPrivateKey(privateKeyInfo)
    }
}