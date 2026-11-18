plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.inversorfrequenciaapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.potenciometroapp"
        minSdk = 25
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
// Necessário para retornar String pura
    implementation("com.squareup.retrofit2:converter-scalars:3.0.0")
// Necessário para converter gson/string
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}