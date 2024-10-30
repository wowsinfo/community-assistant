plugins {
    id("com.android.library")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

android {
    namespace = "com.half.common"

    defaultConfig {
        minSdk = 26
        targetSdkVersion(27)
        compileSdk = 34
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
}

dependencies {
    // NOTE: api is needed here, because common is not in the same module as wows
    api(files("libs/gson-2.2.4.jar"))
    api("com.squareup.picasso:picasso:2.71828")
    api("org.greenrobot:eventbus:3.1.1")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("androidx.core:core:1.13.1")
    api("androidx.appcompat:appcompat:1.7.0")
    api("org.jsoup:jsoup:1.13.1")
    api("com.jakewharton.picasso:picasso2-okhttp3-downloader:1.1.0")
}
