plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.kotlin.android)
}

mavenPublishing {
    // 发布到 Maven Central
    publishToMavenCentral()

    // 只有非 SNAPSHOT 版本才需要签名
    // SNAPSHOT 版本发布到 snapshots 仓库，不需要 GPG 签名
    val versionName = project.findProperty("VERSION_NAME")?.toString() ?: ""
    val isSnapshot = versionName.endsWith("SNAPSHOT", ignoreCase = true)

    if (!isSnapshot) {
        signAllPublications()
    }
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
