package com.lagranjafoods.picking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lagranjafoods.picking.network.AppController;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.lagranjafoods.picking.MESSAGE";

    EditText editText_saleOrderNumber;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_saleOrderNumber = findViewById(R.id.editSaleOrderNumber);
        button = findViewById(R.id.find_button);
    }

    public void getPicking(View view) {
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/getOrCreate/" + editText_saleOrderNumber.getText() + "/";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        showMessage("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showMessage(String.format("That didn't work! Error:\n%s", error.getMessage()));
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                String token = AppController.getInstance(getApplicationContext()).getToken();
                params.put("Token", token);

                return params;
            }
        };

        AppController.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void showMessage(String message){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
