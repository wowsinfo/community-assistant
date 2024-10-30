plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlin)
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}
android {
    namespace = "com.half.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    lint {
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
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
    // NOTE: api is needed here, because common is not in the same module as wows
    api(files("libs/gson-2.2.4.jar"))
    api(libs.picasso)
    api(libs.eventbus)
    api(libs.okhttp)
    api(libs.androidx.core)
    api(libs.appcompat)
    api(libs.jsoup)
    api(libs.picasso.downloader)
    implementation(libs.androidx.core.ktx)
}
