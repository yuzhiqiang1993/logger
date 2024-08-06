plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.yzq.logger"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom.stable))
    implementation(libs.androidx.appcompat.stable)
    implementation(libs.androidx.activity.stable)
    implementation(libs.androidx.activity.ktx.stable)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout.stable)
    implementation(libs.androidx.cardview)

    api(libs.xeonyu.application)
    implementation(libs.xeonyu.coroutine)
    implementation(libs.xeonyu.binding)

}