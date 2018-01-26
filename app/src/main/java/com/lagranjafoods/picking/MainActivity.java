package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

public class MainActivity extends AppCompatActivity {
    EditText editText_saleOrderNumber;
    Button button;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_saleOrderNumber = findViewById(R.id.editSaleOrderNumber);
        button = findViewById(R.id.find_button);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);

        editText_saleOrderNumber.setText("21366");

        setupToolBar();
    }

    private void setupToolBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view
        TextView mTitle = toolbar.findViewById(R.id.tvActivityTitle);
        mTitle.setText("Picking");

        TextView textView_userName = toolbar.findViewById(R.id.tvUserName);
        textView_userName.setText("");
    }

    private void showProgressDialog(String message){
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.dismiss();
    }

    private void showToastWithErrorMessageFromResponse(PickingResponse response){
        showToastWithErrorMessageFromResponse("Error: \n\n", response);
    }

    private void showToastWithErrorMessageFromResponse(String message, PickingResponse response){
        showToast(message + response.getErrorMessage());
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    Response.ErrorListener volleyErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            String message = String.format("Error crítico:\n%s", error.getMessage());
            Log.e("PalletsActivity", message);
            showMessage(message);
            hideProgressDialog();
        }
    };

    private void showMessage(String message){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void getPicking(View view) {
        String url = getString(R.string.baseUrl) + "getOrCreate/" + editText_saleOrderNumber.getText();

        showProgressDialog("Cargando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET,
                url, PickingResponse.class, null, new Response.Listener<PickingResponse>() {
            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()) {
                    startActivityDependingOnPalletsCountInPickingHeader(response);
                } else {
                    showToastWithErrorMessageFromResponse("Error al cargar el picking:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void startActivityDependingOnPalletsCountInPickingHeader(PickingResponse pickingResponse){
        PickingHeader pickingHeader = pickingResponse.getPickingHeader();

        if (pickingHeader.getPallets().isEmpty()){
            Intent intent = new Intent(this, PalletsActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, PalletContentActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_PALLET, getFirstActivePallet(pickingHeader));
            startActivity(intent);
        }
    }

    private PickingPallet getFirstActivePallet(PickingHeader pickingHeader){
        PickingPallet firstAvailablePallet = null;

        for (PickingPallet pickingPallet : pickingHeader.getPallets()) {
            if (pickingPallet.getState().equals(PalletStateEnum.Picking)){
                firstAvailablePallet = pickingPallet;
                break;
            }
        }

        if (firstAvailablePallet == null)
            firstAvailablePallet = pickingHeader.getPallets().get(0);

        return firstAvailablePallet;
    }
}
