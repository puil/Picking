package com.lagranjafoods.picking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
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
import java.util.List;

public class PalletContentActivity extends BaseActivity {
    public static int FINISH_ADDPRODUCTACTIVITY_REQUESTCODE = 100;

    TextView textView_saleOrderNumber;
    TextView textView_saleOrderDate;
    TextView textView_customerName;

    Button button_seePallets;
    Button button_confirmPallet;
    Button button_undoPalletConfirmation;
    android.support.design.widget.FloatingActionButton button_floatingAddProduct;

    ListView listView;
    PalletLineArrayAdapter palletLineArrayAdapter;

    PickingHeader pickingHeader;
    PickingPallet currentPickingPallet;
    List<PickingPalletLine> emptyPalletLinesList = new ArrayList<>();
    int pickingPalletLineIdToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_content);

        button_seePallets = findViewById(R.id.btnSeePallets);
        button_confirmPallet = findViewById(R.id.btnConfirmPallet);
        button_undoPalletConfirmation = findViewById(R.id.btnUndoPalletConfirmation);
        button_floatingAddProduct = findViewById(R.id.fbtnAddProduct);

        button_seePallets.setOnClickListener(this);
        button_confirmPallet.setOnClickListener(this);
        button_undoPalletConfirmation.setOnClickListener(this);
        button_floatingAddProduct.setOnClickListener(this);

        Intent intent = getIntent();
        currentPickingPallet = (PickingPallet)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_PALLET);
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);

        setupToolBar();
        setupListView();

        refreshButtonsVisibility();

        loadPalletLines();
    }

    private void refreshButtonsVisibility(){
        boolean isPickingInProgress = currentPickingPallet.getState().equals(PalletStateEnum.Picking);

        button_confirmPallet.setVisibility(isPickingInProgress ? View.VISIBLE : View.GONE);
        button_undoPalletConfirmation.setVisibility(isPickingInProgress ? View.GONE : View.VISIBLE);
    }

    private void setupToolBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Show back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view
        setToolbarTitle();

        textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(pickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(pickingHeader.getSaleOrderDate()));
        textView_customerName.setText(pickingHeader.getCustomerName());
    }

    private void setToolbarTitle(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.tvActivityTitle);
        mTitle.setText(getToolbarTitle());
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
    protected void pressedOnClick(View view){
        switch (view.getId()){
            case R.id.fbtnAddProduct:
                addProduct();
                break;

            case R.id.btnSeePallets:
                seePallets();
                break;

            case R.id.btnConfirmPallet:
                confirmPallet();
                break;

            case R.id.btnUndoPalletConfirmation:
                undoPalletConfirmation();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FINISH_ADDPRODUCTACTIVITY_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                // Si el resultat és OK, representa que s'ha afegit algun article correctament.
                // Per tant, refresco el contingut del palet
                loadPalletLines();
            }
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * Load pallet lines
     * ----------------------------------------------------------------------------
     */

    private void loadPalletLines(){
        String url = getString(R.string.baseUrl) + "palletlines/" + currentPickingPallet.getId();

        showProgressDialog("Cargando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

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

    /*
     * ----------------------------------------------------------------------------
     * Add pallet
     * ----------------------------------------------------------------------------
     */

    private void addProduct() {
        if (currentPickingPallet.getState().equals(PalletStateEnum.Picking)) {
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_PALLET, currentPickingPallet);
            startActivityForResult(intent, FINISH_ADDPRODUCTACTIVITY_REQUESTCODE);
        }
        else {
            showToast("No se puede añadir productos porque el palet no está en curso");
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * See pallets
     * ----------------------------------------------------------------------------
     */

    public void seePallets(){
        Intent intent = new Intent(this, PalletsActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
        startActivity(intent);
    }

    /*
     * ----------------------------------------------------------------------------
     * Confirm pallet
     * ----------------------------------------------------------------------------
     */

    private void confirmPallet() {
        String url = getString(R.string.baseUrl) + "confirmPallet/" + currentPickingPallet.getId();

        showProgressDialog("Confirmando palet...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.POST, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    showToast("Palet confirmado correctamente");
                    handlePalletConfirmationSuccessResponse();
                }
                else {
                    showToastWithErrorMessageFromResponse("No se ha podido confirmar el palet por el siguiente motivo:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void handlePalletConfirmationSuccessResponse(){
        currentPickingPallet.setState(PalletStateEnum.Confirmed);
        setToolbarTitle();
        refreshButtonsVisibility();
    }

    /*
     * ----------------------------------------------------------------------------
     * Undo pallet confirmation
     * ----------------------------------------------------------------------------
     */

    private void undoPalletConfirmation() {
        String url = getString(R.string.baseUrl) + "undoPalletConfirmation/" + currentPickingPallet.getId();

        showProgressDialog("Anulando confirmación palet...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.POST, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    showToast("Confirmación del palet anulada correctamente");
                    handleUndoPalletConfirmationSuccessResponse();
                }
                else {
                    showToastWithErrorMessageFromResponse("No se ha podido anular la confirmación del palet por el siguiente motivo:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void handleUndoPalletConfirmationSuccessResponse() {
        currentPickingPallet.setState(PalletStateEnum.Picking);
        setToolbarTitle();
        refreshButtonsVisibility();
    }

    /*
     * ----------------------------------------------------------------------------
     * Delete product
     * ----------------------------------------------------------------------------
     */

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
                url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

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
