plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.kotlin.android)
}

// Maven Central发布配置 - 具体参数从gradle.properties读取
mavenPublishing {
    // 发布到 Maven Central（自动检测 SNAPSHOT 和正式版本）
    publishToMavenCentral()

    // 显式启用签名
    signAllPublications()
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

