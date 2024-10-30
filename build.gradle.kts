// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.jetbrainsKotlin) apply false
}

buildscript {
    val kotlin_version = libs.versions.kotlin
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.gradle)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
