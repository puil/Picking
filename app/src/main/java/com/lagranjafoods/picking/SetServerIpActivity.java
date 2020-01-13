package com.lagranjafoods.picking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetServerIpActivity extends AppCompatActivity {

    EditText editText_serverIp;
    Button button_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_server_ip);

        editText_serverIp = findViewById(R.id.editText_serverIp);
        button_save = findViewById(R.id.saveServerIp_button);

        button_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveServerIpIntoSharedPreferences();
            }
        });

        setupToolBar();

        String serverIp = getSharedPreferences("com.lagranjafoods.picking", MODE_PRIVATE).getString(getString(R.string.preference_serverIp), null);
        editText_serverIp.setText(serverIp);
    }

    private void setupToolBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Show back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.palet0_48x48);

        // Get access to the custom title view
        TextView mTitle = toolbar.findViewById(R.id.tvActivityTitle);
        mTitle.setText("Picking");
    }

    private void saveServerIpIntoSharedPreferences(){
        String serverIp = editText_serverIp.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences("com.lagranjafoods.picking", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_serverIp), serverIp);
        editor.commit();

        Toast.makeText(getApplicationContext(), "Ip del servidor cambiada correctamente", Toast.LENGTH_LONG).show();
        finish();
    }
}
