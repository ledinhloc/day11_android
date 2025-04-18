plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.viduvideowithfirebase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.viduvideowithfirebase"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

     // BoM để đồng bộ phiên bản Firebase

    implementation(libs.auth)
    implementation(libs.firebaseAuth)
    implementation(libs.firebaseDatabase)
    implementation(libs.firebaseDatabaseKtx)
    implementation(libs.firebaseFirestore)
    implementation(libs.firebaseStorage)
    implementation(libs.firebaseUiDatabase)

    implementation(platform(libs.firebaseBom))
    implementation(libs.cloudinary.android)

}