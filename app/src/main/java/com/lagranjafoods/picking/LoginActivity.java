package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.StringResponseWithHeader;
import com.lagranjafoods.picking.network.StringWithHeadersRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText _userText;
    EditText _passwordText;
    Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // link the XML layout to this JAVA class
        setContentView(R.layout.activity_login);

        //link graphical items to variables
        _userText = findViewById(R.id.input_user);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _userText.setText("10");
        _passwordText.setText("811");
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        // Hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        String url = "http://192.168.1.39/LaGranjaServices/login";

        StringWithHeadersRequest stringWithHeadersRequest = new StringWithHeadersRequest(
                Request.Method.POST,
                url,
                new Response.Listener<StringResponseWithHeader>() {
                    @Override
                    public void onResponse(StringResponseWithHeader response) {
                        if (response.equals(null)) {
                            Log.e("Login incorrecto", "Response is Null");
                            onLoginFailed();
                        } else {
                            // Si ha llegado aquí, significa que el login es correcto y que
                            // en los headers de la response está el TOKEN de conexión
                            String tokenValue = response.getHeader("Token");
                            AppController.getInstance(getApplicationContext()).setToken(tokenValue);
                            Log.d("Login correcto", "Token: " + tokenValue);
                            onLoginSuccess();
                        }

                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error on login", "Error detail: " + error);
                        onLoginFailed();
                        progressDialog.dismiss();
                    }
                }
        ) {
            //Pass Your Headers here
            @Override
            public Map<String, String> getHeaders() {
                String user = _userText.getText().toString();
                String encodedPassword = getEncodedPassword();
                String credentials = String.format("%s:%s:False", user, encodedPassword);

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + credentials);
                return headers;
            }
        };

        AppController.getInstance(this).addToRequestQueue(stringWithHeadersRequest);
    }

    private String getEncodedPassword(){
        Log.d(TAG, "getEncodedPassword");
        String password = _passwordText.getText().toString();

        return "081029054110069246053225200068109177085145066163";
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess");
        _loginButton.setEnabled(true);
        finish();
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    public void onLoginFailed() {
        Log.d(TAG, "onLoginFailed");
        Toast.makeText(getBaseContext(), "Autenticación errónea", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        Log.d(TAG, "validate");
        boolean valid = true;

        String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

        if (user.isEmpty()) {
            _userText.setError("introduce el usuario");
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("introduce la contraseña");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}