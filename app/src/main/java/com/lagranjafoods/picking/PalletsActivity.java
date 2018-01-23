package com.lagranjafoods.picking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.adapters.PalletArrayAdapter;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PalletsActivity extends AppCompatActivity {

    TextView textView_saleOrderNumber;
    TextView textView_saleOrderDate;
    TextView textView_customerName;
    PickingHeader pickingHeader;
    ListView listView;
    PalletArrayAdapter palletArrayAdapter;
    int pickingPalletIdToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallets);

        Intent intent = getIntent();
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);

        setupToolBar();
        setupListView();
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
        showOrHideButtons();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

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
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyView);
        ListView list = findViewById(R.id.list);
        list.setEmptyView(empty);
    }

    private void refreshPallets(){
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/pallets/" + pickingHeader.getId() + "/";

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
                    palletArrayAdapter.refreshValues(response.getPickingPallets());
                    showOrHideButtons();
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

    private void showOrHideButtons() {
        Button addPallet = findViewById(R.id.btnAddPallet);
        Button confirmPallet = findViewById(R.id.btnConfirmPallet);

        if (palletArrayAdapter.isEmpty()){
            addPallet.setVisibility(View.GONE);
            confirmPallet.setVisibility(View.GONE);
        }
        else{
            addPallet.setVisibility(View.VISIBLE);
            confirmPallet.setVisibility(View.VISIBLE);
        }
    }

    public void addPallet(View view) {
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/addNewPallet/" + pickingHeader.getId() + "/";

        Map<String, String> headers = new HashMap<>();
        String token = AppController.getInstance(getApplicationContext()).getToken();
        headers.put("Token", token);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        GsonRequest<PickingResponse> jsObjRequest = new GsonRequest<>(Request.Method.POST,
                url, PickingResponse.class, headers, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    refreshPallets();
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

    public void deleteSelectedPallet(View view){
        PickingPallet pickingPallet = (PickingPallet) view.getTag();

        if (pickingPallet.getState().equals(PalletStateEnum.Picking)){
            AskDeletionQuestion(pickingPallet.getId());
        }
        else{
            Toast.makeText(getApplicationContext(), "No se puede eliminar porque no está en curso", Toast.LENGTH_LONG).show();
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    performDeletePickingPallet(pickingPalletIdToDelete);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //Do your No progress
                    break;
            }
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
        String url ="http://192.168.1.39/LaGranjaServices/api/picking/pallets/" + pickingPalletId + "/";

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
                    refreshPallets();
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
