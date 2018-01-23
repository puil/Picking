package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText editText_saleOrderNumber;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_saleOrderNumber = findViewById(R.id.editSaleOrderNumber);
        button = findViewById(R.id.find_button);

        editText_saleOrderNumber.setText("28034");
    }

    public void getPicking(View view) {
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/getOrCreate/" + editText_saleOrderNumber.getText() + "/";

        Map<String, String> headers = new HashMap<>();
        String token = AppController.getInstance(getApplicationContext()).getToken();
        headers.put("Token", token);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("cargando...");
        progressDialog.show();

        GsonRequest<PickingResponse> jsObjRequest = new GsonRequest<>(Request.Method.GET,
                url, PickingResponse.class, headers, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                checkResponseAndStartPalletContentActivity(response);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(String.format("That didn't work! Error:\n%s", error.getMessage()));
                progressDialog.dismiss();
            }
        });

        AppController.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void showMessage(String message){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void checkResponseAndStartPalletContentActivity(PickingResponse pickingResponse){
        if (pickingResponse.isSuccess()){
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
        else {
            showMessage(pickingResponse.getActionResultMessage());
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
