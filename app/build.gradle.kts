import org.jetbrains.kotlin.ir.backend.js.compile
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt") version "2.0.0"
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.litenote"
    compileSdk = 35

    defaultConfig {
        applicationId = "cn.tw.sar.note"
        minSdk= 29
        maxSdk =35
        targetSdk =35
        versionCode = 3000050
        versionName = "2.5.0.BETA"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        create("google") {
            initWith(getByName("release"))
            applicationIdSuffix = ".google"
        }

    }
    signingConfigs {
        create("dp") {
            storeFile = file("../keystore/keystore.jks")
            storePassword = "wxdqwe123"
            keyAlias = "key0"
            keyPassword = "wxdqwe123"
        }
        create("dp2") {
            storeFile = file("../keystore/keystore.jks")
            storePassword = "wxdqwe123"
            keyAlias = "key0"
            keyPassword = "wxdqwe123"
        }

    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("dp2")
        }
        debug{
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("dp2")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("dp2")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        pickFirst("META-INF/LICENSE.md")

        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")



        exclude("META-INF/*.kotlin_module")
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(fileTree(
        mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))

    ))
    //implementation(libs.ezxhelper)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.constraintlayout.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.accompanist:accompanist-drawablepainter:0.36.0")

    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-material:1.1.0")
    implementation("androidx.glance:glance-material3:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.4.1")

    val room_version = "2.6.1"

    implementation(libs.com.github.promeg.tinypinyin19) // TinyPinyin核心包，



    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    // To use Kotlin Symbol Processing (KSP)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("com.belerweb:pinyin4j:2.5.0")
    //ksp("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")
    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")
    // optional - Guavasupport for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")
    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")

    implementation("com.google.code.gson:gson:2.10")

    implementation("io.github.bytebeats:compose-charts:0.2.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    // Coroutines

    implementation("com.google.accompanist:accompanist-insets:0.16.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.16.0")


    implementation("com.github.jeziellago:compose-markdown:0.3.6")

    implementation("de.psdev.licensesdialog:licensesdialog:2.1.0")



    implementation(libs.compose.qr.code)

    implementation("com.umeng.umsdk:common:9.4.7")// 必选
    implementation("com.umeng.umsdk:asms:1.4.0")// 必选

}