plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsKotlin)
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
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        compose = true
    }

    signingConfigs {
        create("release")
    }

    defaultConfig {
        applicationId = "com.half.wowsca"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(":common"))
    implementation(libs.google.material)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.mpandroidchart)
    implementation("com.mikepenz:materialdrawer:5.2.6@aar") {
        isTransitive = true
    }
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)

    // Uncomment the line below if you want to include Google Play Services Ads, make sure to use a different version
    // implementation("com.google.android.gms:play-services-ads:17.1.1")
}
