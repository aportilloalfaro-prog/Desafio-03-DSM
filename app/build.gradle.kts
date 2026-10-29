import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Lee las credenciales desde local.properties
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun prop(name: String): String = localProps.getProperty(name, "")

android {
    namespace = "com.example.worldexplorer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.worldexplorer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Se generan como BuildConfig.XXX (no quedan en el código fuente)
        buildConfigField("String", "RESTCOUNTRIES_TOKEN", "\"${prop("RESTCOUNTRIES_TOKEN")}\"")
        buildConfigField("String", "GEONAMES_USERNAME", "\"${prop("GEONAMES_USERNAME")}\"")
        buildConfigField("String", "WEATHERAPI_KEY", "\"${prop("WEATHERAPI_KEY")}\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // Red
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Imágenes (banderas e íconos del clima)
    implementation("io.coil-kt:coil:2.6.0")

    // Mapa
    implementation("org.osmdroid:osmdroid-android:6.1.18")
}