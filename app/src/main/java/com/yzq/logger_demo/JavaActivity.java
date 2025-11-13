package com.yzq.logger_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yzq.logger.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JavaActivity extends AppCompatActivity {

    private static final String TAG = "JavaActivity";

    @Nullable
    public static Intent createIntent(@NotNull Activity activity) {
        Intent intent = new Intent(activity, JavaActivity.class);
        testLog();
        return intent;
    }

    private static void testLog() {
        Logger.i(TAG, "JavaActivity testLog");
        Logger.e(TAG, "JavaActivity testLog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        Logger.i(TAG, "JavaActivity onCreate", "1111");
        Logger.i(TAG, "JavaActivity onCreate", "2222");
    }
}