plugins {
    id("com.android.application")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

android {
    namespace = "com.half.wowsca"

    signingConfigs {
        create("release")
    }

    defaultConfig {
        minSdk = 26
        targetSdkVersion(28)
        compileSdk = 34
        versionCode = 57
        versionName = "1.3.1.3"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
        }
    }

    lint {
        checkReleaseBuilds = false
        // Or, if you prefer, you can continue to check for errors in release builds,
        // but continue the build even when errors are found:
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":common"))
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.PhilJay:MPAndroidChart:v2.2.4")
    implementation("com.mikepenz:materialdrawer:5.2.6@aar") {
        isTransitive = true
    }
    // Uncomment the line below if you want to include Google Play Services Ads, make sure to use a different version
    // implementation("com.google.android.gms:play-services-ads:17.1.1")
}
