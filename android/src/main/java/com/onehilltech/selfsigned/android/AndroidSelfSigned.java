package com.onehilltech.selfsigned.android;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class AndroidSelfSigned
{
  public static final String DEFAULT_CA_TYPE = "X.509";
  public static final String DEFAULT_SSL_CONTEXT_TYPE = "TLS";

  public static SSLContext newSSLContext (Context context, String assetFile)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
  {
    return newSSLContext (context, DEFAULT_CA_TYPE, assetFile);
  }

  public static SSLContext newSSLContext (Context context, String type, String assetFile)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
  {
    InputStream caInput = new BufferedInputStream (context.getAssets ().open (assetFile));
    return newSSLContext (type, caInput);
  }

  public static SSLContext newSSLContext (InputStream caInput)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
  {
    return newSSLContext (DEFAULT_CA_TYPE, caInput);
  }

  public static SSLContext newSSLContext (String type, InputStream caInput)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
  {
    CertificateFactory cf = CertificateFactory.getInstance (type);
    try
    {
      Certificate ca = cf.generateCertificate (caInput);

      // Create a KeyStore containing our trusted CAs
      String keyStoreType = KeyStore.getDefaultType ();
      KeyStore keyStore = KeyStore.getInstance (keyStoreType);
      keyStore.load (null, null);
      keyStore.setCertificateEntry ("ca", ca);

      // Create a TrustManager that trusts the CAs in our KeyStore
      String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm ();
      TrustManagerFactory tmf = TrustManagerFactory.getInstance (tmfAlgorithm);
      tmf.init (keyStore);

      // Create an SSLContext that uses our TrustManager
      SSLContext sslContext = SSLContext.getInstance (DEFAULT_SSL_CONTEXT_TYPE);
      sslContext.init (null, tmf.getTrustManagers (), null);

      return sslContext;
    }
    finally
    {
      caInput.close ();
    }
  }
}
