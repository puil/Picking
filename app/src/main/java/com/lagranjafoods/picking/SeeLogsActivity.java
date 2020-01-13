package com.lagranjafoods.picking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SeeLogsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_logs);
        try {
            //Process process = Runtime.getRuntime().exec("logcat -e Services -d");
            Process process = Runtime.getRuntime().exec("logcat -d AppController AddProductActivity *:s");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n\n");
            }
            TextView tv = findViewById(R.id.textViewLogs);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setText(log.toString());
        } catch (java.io.IOException e) {
        }
    }
}
