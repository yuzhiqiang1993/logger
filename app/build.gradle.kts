plugins {
    alias(libs.plugins.xeonyu.application)
//    alias(libs.plugins.xeonyu.dependencyManager)
}

//dependencyManager {
//    //依赖分析
//    analysis {
//        enable = true
//    }
//}


android {
    namespace = "com.yzq.logger_demo"

    defaultConfig {
        applicationId = "com.yzq.logger_demo"
        versionCode = 1
        versionName = "1.0"

    }

    signingConfigs {

        getByName("debug") {
            storeFile = file("ketstore")
            storePassword = "123456"
            keyAlias = "logger"
            keyPassword = "123456"
        }
    }


    buildTypes {

        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)
    implementation(libs.moshiKotlin)
    implementation(libs.xeonyu.application)
    implementation(libs.xeonyu.coroutine)
//    implementation(libs.xeonyu.logger)
    implementation(project(":logger"))

}