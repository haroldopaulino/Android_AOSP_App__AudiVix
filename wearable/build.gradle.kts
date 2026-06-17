plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.harold.audivix.wear"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.harold.audivix.wear"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.31"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation("androidx.media3:media3-ui:1.9.4")
    implementation("androidx.media3:media3-session:1.9.4")
    implementation("androidx.media3:media3-common:1.9.4")
    implementation("androidx.media3:media3-exoplayer:1.9.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    testImplementation("junit:junit:4.13.2")
    implementation("io.insert-koin:koin-androidx-compose:4.1.1")
    implementation("io.insert-koin:koin-android:4.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.11.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.squareup.retrofit2:adapter-rxjava3:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.12.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.compose.ui:ui:1.10.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.10.0")
    implementation("androidx.compose.foundation:foundation:1.10.0")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation("androidx.wear.compose:compose-material3:1.6.0")
    implementation("androidx.wear.compose:compose-foundation:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.10.0")
}
