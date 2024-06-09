plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.bangkit.fraudguard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bangkit.fraudguard"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildConfigField("String", "BASE_URL", "\"http://34.143.137.97:3000/api/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://34.143.137.97:3000/api/\"")
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

    implementation(libs.androidx.core.ktx) // androidx.core:core-ktx:1.10.1
    implementation(libs.androidx.appcompat) // androidx.appcompat:appcompat:1.6.1
    implementation(libs.material) // com.google.android.material:material:1.9.0
    implementation(libs.androidx.constraintlayout) // androidx.constraintlayout:constraintlayout:2.1.4
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit) // junit:junit:4.13.2
    androidTestImplementation(libs.androidx.junit) // androidx.test.ext:junit:1.1.5
    androidTestImplementation(libs.androidx.espresso.core) // androidx.test.espresso:espresso-core:3.5.1
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1
    implementation(libs.androidx.lifecycle.livedata.ktx) // androidx.lifecycle:lifecycle-livedata-ktx:2.5.1
    implementation(libs.androidx.activity) // androidx.activity:activity-ktx:1.7.2
    implementation(libs.androidx.datastore.preferences) // androidx.datastore:datastore-preferences:1.0.0
    implementation(libs.androidx.activity.ktx)
    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp3.logging)
    implementation(libs.okhttp3)


}