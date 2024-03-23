plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hayk.healthmanagerregistration"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hayk.healthmanagerregistration"
        minSdk = 29
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore:24.10.2")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.activity:activity:1.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")
}