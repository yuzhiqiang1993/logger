plugins {
    alias(libs.plugins.xeonyu.application)
}

android {
    namespace = "com.yzq.logger.demo"

    defaultConfig {
        applicationId = "com.yzq.logger.demo"
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)
    implementation(libs.moshiKotlin)
//    implementation(libs.xeonyu.logger)
    implementation(project(":logger"))

}