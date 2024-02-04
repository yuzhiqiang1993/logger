plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
}

android {
    namespace = "com.yzq.logger"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.appcompat)
    api(libs.xeonyu.application)
}