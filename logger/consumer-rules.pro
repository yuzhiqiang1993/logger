# 移除日志打印
-assumenosideeffects class android.util.Log {
        public static *** d(...);
        public static *** e(...);
        public static *** i(...);
        public static *** v(...);
        public static *** println(...);
        public static *** w(...);
        public static *** wtf(...);
}