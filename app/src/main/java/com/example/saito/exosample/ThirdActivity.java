package com.example.saito.exosample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(getClass().getSimpleName());

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }
}
