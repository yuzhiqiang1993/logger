# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# 保留 kotlin 元信息
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# 保留 Logger 自身的包名，确保 stack trace 过滤逻辑正常工作
-keepnames class com.yzq.logger.** { *; }

# 保留源文件和行号信息，确保日志能打印出代码位置
-keepattributes SourceFile,LineNumberTable

# 保留泛型信息
-keepattributes Signature