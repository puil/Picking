package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.adapters.PalletLineArrayAdapter;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingPalletLine;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalletContentActivity extends AppCompatActivity {

    TextView textView_saleOrderNumber;
    TextView textView_saleOrderDate;
    TextView textView_customerName;
    ListView listView;
    PickingHeader pickingHeader;
    PickingPallet currentPickingPallet;
    PalletLineArrayAdapter palletLineArrayAdapter;
    List<PickingPalletLine> palletLines = new ArrayList<>();
    int pickingPalletLineIdToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_content);

        Intent intent = getIntent();
        currentPickingPallet = (PickingPallet)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_PALLET);
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);

        setupToolBar();
        setupListView();

        loadPalletLines();
    }

    private void setupToolBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Show back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view
        TextView mTitle = toolbar.findViewById(R.id.tvActivityTitle);
        mTitle.setText("Palet " + currentPickingPallet.getPalletNumber());

        textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(pickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(pickingHeader.getSaleOrderDate()));
        textView_customerName.setText(pickingHeader.getCustomerName());
    }

    private void setupListView(){
        listView = findViewById(R.id.list);
        palletLineArrayAdapter = new PalletLineArrayAdapter(this, palletLines);
        listView.setAdapter(palletLineArrayAdapter);
    }

    private void loadPalletLines(){
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/palletlines/" + currentPickingPallet.getId() + "/";

        Map<String, String> headers = new HashMap<>();
        String token = AppController.getInstance(getApplicationContext()).getToken();
        headers.put("Token", token);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        GsonRequest<PickingResponse> jsObjRequest = new GsonRequest<>(Request.Method.GET,
                url, PickingResponse.class, headers, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    palletLineArrayAdapter.refreshValues(response.getPickingPalletLines());
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.getErrorMessage(), Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String message = String.format("That didn't work! Error:\n%s", error.getMessage());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                //showMessage(String.format("That didn't work! Error:\n%s", error.getMessage()));
                progressDialog.dismiss();
            }
        });

        AppController.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = findViewById(R.id.list);
        list.setEmptyView(empty);
    }

    public void seePallets(View view){
        Intent intent = new Intent(this, PalletsActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
        startActivity(intent);
    }

    public void deleteSelectedPalletLine(View view){
        PickingPalletLine pickingPalletLine = (PickingPalletLine) view.getTag();

        if (currentPickingPallet.getState().equals(PalletStateEnum.Picking)){
            AskDeletionQuestion(pickingPalletLine.getId());
        }
        else{
            Toast.makeText(getApplicationContext(), "No se puede eliminar ningún producto porque el palet no está en curso", Toast.LENGTH_LONG).show();
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    performDeletePickingPalletLine(pickingPalletLineIdToDelete);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //Do your No progress
                    break;
            }
        }
    };

    private void AskDeletionQuestion(final int pickingPalletLineId) {
        pickingPalletLineIdToDelete = pickingPalletLineId;
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("¿Eliminar producto?")
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void performDeletePickingPalletLine(int pickingPalletLineId) {
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/palletLines/" + pickingPalletLineId + "/";

        Map<String, String> headers = new HashMap<>();
        String token = AppController.getInstance(getApplicationContext()).getToken();
        headers.put("Token", token);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Eliminando...");
        progressDialog.show();

        GsonRequest<PickingResponse> jsObjRequest = new GsonRequest<>(Request.Method.DELETE,
                url, PickingResponse.class, headers, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    loadPalletLines();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No se ha podido eliminar por el siguiente motivo:\n\n" + response.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String message = String.format("That didn't work! Error:\n%s", error.getMessage());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                //showMessage(String.format("That didn't work! Error:\n%s", error.getMessage()));
                progressDialog.dismiss();
            }
        });

        AppController.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
