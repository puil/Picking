package com.lagranjafoods.picking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.lagranjafoods.picking.adapters.PalletArrayAdapter;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

import java.text.SimpleDateFormat;

public class PalletsActivity extends BaseActivity {

    TextView textView_saleOrderNumber;
    TextView textView_saleOrderDate;
    TextView textView_customerName;
    Button button_addFirstPallet;
    Button button_addPallet;
    Button button_confirmPicking;
    android.support.design.widget.FloatingActionButton button_floatingAddPallet;
    ListView listView;
    PalletArrayAdapter palletArrayAdapter;
    PickingHeader pickingHeader;
    int pickingPalletIdToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallets);

        button_addFirstPallet = findViewById(R.id.btnAddFirstPallet);
        button_addPallet = findViewById(R.id.btnAddPallet);
        button_confirmPicking = findViewById(R.id.btnConfirmPicking);
        button_floatingAddPallet = findViewById(R.id.fbtnAddPallet);

        button_addFirstPallet.setOnClickListener(this);
        button_addPallet.setOnClickListener(this);
        button_confirmPicking.setOnClickListener(this);
        button_floatingAddPallet.setOnClickListener(this);

        Intent intent = getIntent();
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);

        setupToolBar();
        setupListView();

        loadPallets();
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
        mTitle.setText("Palets");

        textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(pickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(pickingHeader.getSaleOrderDate()));
        textView_customerName.setText(pickingHeader.getCustomerName());
    }

    private void setupListView() {
        listView = findViewById(R.id.list);
        palletArrayAdapter = new PalletArrayAdapter(this, pickingHeader.getPallets());
        listView.setAdapter(palletArrayAdapter);
        showOrHideBottomButtons();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                PickingPallet selectedPickingPallet = (PickingPallet) listView.getItemAtPosition(position);

                // Open pallet content activity
                Intent intent = new Intent(PalletsActivity.this, PalletContentActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
                intent.putExtra(ExtraConstants.EXTRA_PICKING_PALLET, selectedPickingPallet);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void pressedOnClick(View view){
        switch (view.getId()){
            case R.id.btnAddPallet:
            case R.id.btnAddFirstPallet:
            case R.id.fbtnAddPallet:
                addPallet();
                break;

            case R.id.btnConfirmPicking:
                confirmPicking();
                break;
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyView);
        ListView list = findViewById(R.id.list);
        list.setEmptyView(empty);
    }

    private void showOrHideBottomButtons() {
        LinearLayout bottomLinearLayout = findViewById(R.id.bottomLinearLayout);
        bottomLinearLayout.setVisibility(palletArrayAdapter.isEmpty() ? View.GONE : View.VISIBLE);

        button_floatingAddPallet.setVisibility(palletArrayAdapter.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /*
     * ----------------------------------------------------------------------------
     * Load pallets
     * ----------------------------------------------------------------------------
     */

    private void loadPallets(){
        String url = getString(R.string.baseUrl) + "pallets/" + pickingHeader.getId();

        showProgressDialog("Cargando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    palletArrayAdapter.refreshValues(response.getPickingPallets());
                    showOrHideBottomButtons();
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

    private void addPallet() {
        String url = getString(R.string.baseUrl) + "addNewPallet/" + pickingHeader.getId();

        showProgressDialog("Añadiendo palet...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.POST, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    loadPallets();
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
     * Confirm picking
     * ----------------------------------------------------------------------------
     */

    private void confirmPicking() {
        String url = getString(R.string.baseUrl) + "confirmPicking/" + pickingHeader.getId();

        showProgressDialog("Confirmando picking...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.POST, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    showToast("Picking confirmado");
                    finish();
                }
                else {
                    showToastWithErrorMessageFromResponse("No se ha podido confirmar el picking por el siguiente motivo:\n\n", response);
                }
                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    /*
     * ----------------------------------------------------------------------------
     * Delete pallet
     * ----------------------------------------------------------------------------
     */

    public void deleteSelectedPallet(View view){
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        PickingPallet pickingPallet = (PickingPallet) view.getTag();

        if (pickingPallet.getState().equals(PalletStateEnum.Picking)){
            AskDeletionQuestion(pickingPallet.getId());
        }
        else{
            showToast("No se puede eliminar porque no está en curso");
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE)
                performDeletePickingPallet(pickingPalletIdToDelete);
        }
    };

    private void AskDeletionQuestion(final int pickingPalletId) {
        pickingPalletIdToDelete = pickingPalletId;
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("¿Eliminar palet?")
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void performDeletePickingPallet(int pickingPalletId) {
        String url = getString(R.string.baseUrl) + "pallets/" + pickingPalletId;

        showProgressDialog("Eliminando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.DELETE,
                url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {
            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()) {
                    loadPallets();
                    showToast("Palet eliminado correctamente");
                } else {
                    showToastWithErrorMessageFromResponse("No se ha podido eliminar por el siguiente motivo:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }
}
