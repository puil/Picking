package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.CryptoHandler;
import com.lagranjafoods.picking.network.StringResponseWithHeader;
import com.lagranjafoods.picking.network.StringWithHeadersRequest;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    CryptoHandler _crypto;
    EditText _userText;
    EditText _passwordText;
    Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        InitComponents();

        setupToolBar();

        _userText.setText("10");
        _passwordText.setText("811");
    }

    /**
     * Initialize class members intances
     */
    private void InitComponents()
    {
        _userText = findViewById(R.id.input_user);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);

        _passwordText.setOnEditorActionListener(editorActionListener);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _crypto = new CryptoHandler(CryptoHandler.PASSWORD);
    }

    private void setupToolBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.palet0_48x48);

        // Get access to the custom title view
        TextView mTitle = toolbar.findViewById(R.id.tvActivityTitle);
        mTitle.setText("Picking");
    }

    EditText.OnEditorActionListener editorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        }
    };

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

        String url = getString(R.string.loginEndpoint);

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

                            int userId = Integer.parseInt(response.getResponseData());
                            saveUserIdIntoSharedPreferences(userId);

                            Log.d("Login correcto", "Token: " + tokenValue);
                            onLoginSuccess();
                        }

                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error on login", "Error detail: " + error);

                        if (error.getCause() instanceof SocketException)
                        {
                            onServerUnreachable();
                        }
                        else
                        {
                            onLoginFailed();
                        }

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

        stringWithHeadersRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance(this).addToRequestQueue(stringWithHeadersRequest);
    }

    private String getEncodedPassword(){
        Log.d(TAG, "getEncodedPassword");
        String password = _passwordText.getText().toString();
        byte[] encryptedData = _crypto.Encrypt(password.getBytes());
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
    }

    private void saveUserIdIntoSharedPreferences(int userId){
        SharedPreferences sharedPref = getSharedPreferences("com.lagranjafoods.picking", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.preference_userId), userId);
        editor.commit();
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

    private void onServerUnreachable() {
        Log.d(TAG, "OnServerUnreachable");
        Toast.makeText(getBaseContext(), "No hay conexión con el servidor", Toast.LENGTH_LONG).show();
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