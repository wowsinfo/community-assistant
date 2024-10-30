// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.1")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
