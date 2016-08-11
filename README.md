android-selfsigned
==================

[![Download](https://jitpack.io/v/onehilltech/android-selfsigned.svg)](https://jitpack.io/#onehilltech/android-selfsigned)
[![Build Status](https://travis-ci.org/onehilltech/android-selfsigned.svg)](https://travis-ci.org/onehilltech/android-selfsigned)
[![codecov.io](http://codecov.io/github/onehilltech/android-selfsigned/coverage.svg?branch=master)](http://codecov.io/github/onehilltech/android-selfsigned?branch=master)

A simple library for supporting self-signed certificates in Android

* Integrate with services that use **self-signed certificates**.
* **Preserve** existing security measures on the mobile device.
* Ideal for **prototyping and testing** using secure protocols.

**NOTE.** We strongly recommend that you purchase a certificate from a trusted authority 
when you move to production.

## Installation

#### Gradle

```
buildscript {
  repositories {
    maven { url "https://jitpack.io" }
  }
}

dependencies {
  # Only include if using HttpsURLConnection
  compile com.github.onehilltech.android-selfsigned:android:x.y.z
  
  # Otherwise, use appropriate module for framework in use
  compile com.github.onehilltech.android-selfsigned:android-volley:x.y.
}
```

## Getting Started

Manually define the list of hostnames/IP addresses that are using self-signed 
certificates. It is best to define the list as a resource so you can have
different list for different Gradle configurations:

```xml
<resources>
    <string-array name="hostnames">
        <!-- localhost on the Android emulator -->
        <item>10.0.2.2</item>
    </string-array>
</resources>
```

Define an `Application` class to initialize the `DefaultHostnameVerifier`, 
which is used by `HttpsURLConnection`.

```java
public class TheApplication extends Application 
{
  @Override
  public void onCreate ()
  {
    super.onCreate ();

    String [] hostnames = this.getResources ().getStringArray (R.array.hostnames);
    SelfSigned.getDefaultHostnameVerifier ().addAll (Arrays.asList (hostnames));
  }
}
```

Make sure you add the `TheApplication` class to `AndroidManifest.xml`.

```xml
<application
    android:name="[package].TheApplication"
    
    >
    
</application>
```

Add the public certificate to the application's assets. For example, if
the certificate is in a file named `server.crt`, then it must be added
to `main/assets/server.crt` (or the assets folder for the target configuration).

### HttpsURLConnection

First, create a `SSLContext` that uses the public certificate bundled as an
asset:

```java
SSLContext sslContext = AndroidSelfSigned.newSSLContext (context, "server.crt");
```

Attach the `SSLContext` to a `HttpsURLConnection`:

```java
URL url = new URL ("https://www.google.com");
HttpsURLConnection conn = (HttpsURLConnection)url.openConnection ();
conn.setSSLSocketFactory (sslContext.getSocketFactory ());
```

You can even set the `SSLContext` as the default so you do not have to initialize
the `SSLSocketFactory` for each `HttpsURLConnection`:

```java
HttpsURLConnection.setDefaultSSLSocketFactory (sslContext.getSocketFactory ());
```

If you use this approach, it is best to do so in the `Application` class for
your application.

### android-volley

Volley uses `HttpsURLConnection` under the hood. If you **do not** set the 
default `SSLSocketFactory`, as explained above, then you can use the helper 
class to create a `RequestQueue` that supports self-signed certificates:

```java
VolleySelfSigned.newRequestQueue (context, "server.crt")
```

Now, requests executed on the returned `RequestQueue` that interact with an 
hostname/IP address defined in the resources above will not throw the usual 
security exceptions.
