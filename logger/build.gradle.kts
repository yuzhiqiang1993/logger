plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
}

android {
    namespace = "com.yzq.logger"
}

dependencies {
    implementation(platform(libs.kotlin.bom.stable))
    implementation(libs.androidx.appcompat.stable)
    implementation(libs.androidx.recyclerview.stable)
    api(libs.xeonyu.application)
    implementation(libs.xeonyu.coroutine)
}