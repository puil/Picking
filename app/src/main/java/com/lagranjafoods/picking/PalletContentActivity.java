package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class PalletContentActivity extends AppCompatActivity implements View.OnClickListener{

    TextView textView_saleOrderNumber;
    TextView textView_saleOrderDate;
    TextView textView_customerName;
    Button button_addProduct;
    Button button_seePallets;
    Button button_confirmPallet;
    ListView listView;
    PalletLineArrayAdapter palletLineArrayAdapter;
    ProgressDialog progressDialog;
    PickingHeader pickingHeader;
    PickingPallet currentPickingPallet;
    List<PickingPalletLine> emptyPalletLinesList = new ArrayList<>();
    int pickingPalletLineIdToDelete;

    // variable to track event time (to avoid double click on buttons)
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_content);

        button_addProduct = findViewById(R.id.btnAddProduct);
        button_seePallets = findViewById(R.id.btnSeePallets);
        button_confirmPallet = findViewById(R.id.btnConfirmPallet);

        button_addProduct.setOnClickListener(this);
        button_seePallets.setOnClickListener(this);
        button_confirmPallet.setOnClickListener(this);

        Intent intent = getIntent();
        currentPickingPallet = (PickingPallet)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_PALLET);
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);

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
        mTitle.setText(getToolbarTitle());

        textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(pickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(pickingHeader.getSaleOrderDate()));
        textView_customerName.setText(pickingHeader.getCustomerName());
    }

    private String getToolbarTitle(){
        return "Palet " + currentPickingPallet.getPalletNumber() + "   (" + currentPickingPallet.getState() + ")";
    }

    private void setupListView(){
        listView = findViewById(R.id.list);
        palletLineArrayAdapter = new PalletLineArrayAdapter(this, emptyPalletLinesList);
        listView.setAdapter(palletLineArrayAdapter);
    }

    @Override
    public void onClick(View view) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        pressedOnClick(view);
    }

    public void pressedOnClick(View view){
        switch (view.getId()){
            case R.id.btnAddProduct:
                addProduct();
                break;

            case R.id.btnSeePallets:
                seePallets();
                break;

            case R.id.btnConfirmPallet:
                confirmPallet();
                break;
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = findViewById(R.id.list);
        list.setEmptyView(empty);
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
            Log.e("PalletContentActivity", message);
            showMessage(message);
            hideProgressDialog();
        }
    };

    private void showMessage(String message){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void loadPalletLines(){
        String url = getString(R.string.baseUrl) + "palletlines/" + currentPickingPallet.getId();

        showProgressDialog("Cargando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, url, PickingResponse.class, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    palletLineArrayAdapter.refreshValues(response.getPickingPalletLines());
                }
                else {
                    showToastWithErrorMessageFromResponse(response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void addProduct() {
        showToast("Pendent de fer");
    }

    public void seePallets(){
        Intent intent = new Intent(this, PalletsActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
        startActivity(intent);
    }

    private void confirmPallet() {
        String url = getString(R.string.baseUrl) + "confirmPallet/" + currentPickingPallet.getId();

        showProgressDialog("Confirmando palet...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.POST, url, PickingResponse.class, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    showToast("Palet confirmado correctamente");
                    finish();
                }
                else {
                    showToastWithErrorMessageFromResponse("No se ha podido confirmar el palet por el siguiente motivo:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    public void deleteSelectedPalletLine(View view){
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        PickingPalletLine pickingPalletLine = (PickingPalletLine) view.getTag();

        if (currentPickingPallet.getState().equals(PalletStateEnum.Picking)){
            AskDeletionQuestion(pickingPalletLine.getId());
        }
        else{
            showToast("No se puede eliminar ningún producto porque el palet no está en curso");
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE)
                performDeletePickingPalletLine(pickingPalletLineIdToDelete);
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
        String url = getString(R.string.baseUrl) + "palletLines/" + pickingPalletLineId;

        showProgressDialog("Eliminando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.DELETE,
                url, PickingResponse.class, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    showToast("Producto eliminado correctamente");
                    loadPalletLines();
                }
                else {
                    showToastWithErrorMessageFromResponse("No se ha podido eliminar por el siguiente motivo:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }
}
