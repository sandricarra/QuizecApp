plugins {
    // alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.android.application")
    id("com.google.gms.google-services") // Plugin de Google Services para Firebase

}

android {
    namespace = "pt.isec.ams.quizec"
    compileSdk = 34

    defaultConfig {
        applicationId = "pt.isec.ams.quizec"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }



}


dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    implementation("io.coil-kt.coil3:coil-compose:3.0.3")
    // Agrega la dependencia de Navegación de Compose
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // MPAndroidChart (Gráficos)
    //implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // O la versión v3.1.1 si está disponible

    // Dependencias necesarias Compose/Material3/ViewModel, etc
    implementation("androidx.compose.material3:material3:1.0.0")
    implementation("androidx.compose.foundation:foundation-layout:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    // Dependencia para restringir la Localización
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.location)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.firebase.perf.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}