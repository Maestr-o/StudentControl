plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.9.21"
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.maestrx.studentcontrol.teacherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.maestrx.studentcontrol.teacherapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val navigationVer = "2.7.7"
    val daggerVer = "2.50"
    val roomVer = "2.6.1"
    val apachePoiVer = "5.2.2"
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVer")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVer")
    implementation("com.google.dagger:hilt-android:$daggerVer")
    implementation("androidx.room:room-ktx:$roomVer")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("org.apache.poi:poi:$apachePoiVer")
    implementation("org.apache.poi:poi-ooxml:$apachePoiVer")
    ksp("androidx.room:room-compiler:$roomVer")
    ksp("com.google.dagger:dagger-compiler:$daggerVer")
    ksp("com.google.dagger:hilt-compiler:$daggerVer")
}