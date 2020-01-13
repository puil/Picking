package com.lagranjafoods.picking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.lagranjafoods.picking.helpers.StringHelper;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.models.User;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

public class MainActivity extends BaseActivity {
    TextView textView_userName;
    EditText editText_saleOrderNumber;
    Button button_find;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_saleOrderNumber = findViewById(R.id.editSaleOrderNumber);
        editText_saleOrderNumber.setOnEditorActionListener(editorActionListener);

        button_find = findViewById(R.id.find_button);

        button_find.setOnClickListener(this);

        //editText_saleOrderNumber.setText("30173");

        setupToolBar();
        getCurrentUser();

        // Definir que es mostri el teclat quan el camp rep el focus
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void setupToolBar(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.palet04);

        // Get access to the custom title view
        TextView mTitle = toolbar.findViewById(R.id.tvActivityTitle);
        mTitle.setText("Picking");

        textView_userName = toolbar.findViewById(R.id.tvUserName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuItem_seeLogs:
                seeLogs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void pressedOnClick(View view) {
        switch (view.getId()){
            case R.id.find_button:
                getPicking();
                break;
        }
    }

    EditText.OnEditorActionListener editorActionListener = new EditText.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getPicking();
                return true;
            }
            return false;
        }
    };

    private void getCurrentUser() {
        final int currentUserId = getSharedPreferences("com.lagranjafoods.picking", MODE_PRIVATE).getInt(getString(R.string.preference_userId), 0);
        String url = getUrl(R.string.usersEndpoint) + currentUserId;

        GsonRequest<User> gsonRequest = new GsonRequest<>(Request.Method.GET, url, User.class, null, null, new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                if (response != null) {
                    currentUser = response;
                    textView_userName.setText(currentUser.getFullName());
                } else {
                    showToast("No se ha podido obtener el usuario desde el servidor");
                }
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    public void getPicking() {
        if (!validate()) {
            return;
        }

        if (StringHelper.isNullOrEmpty(editText_saleOrderNumber.getText().toString())){
            showToast("Informa el número de pedido");
            return;
        }

        String url = getUrl(R.string.pickingEndpoint) + "getOrCreate/" + editText_saleOrderNumber.getText();

        showProgressDialog("Cargando...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {
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

    private boolean validate() {
        boolean valid = true;

        String saleOrderNumber = editText_saleOrderNumber.getText().toString();

        if (saleOrderNumber.isEmpty()) {
            editText_saleOrderNumber.setError("introduce el número de pedido");
            valid = false;
        } else {
            editText_saleOrderNumber.setError(null);
        }

        return valid;
    }

    private void startActivityDependingOnPalletsCountInPickingHeader(PickingResponse pickingResponse){
        hideKeyboard(this);

        PickingHeader pickingHeader = pickingResponse.getPickingHeader();
        PickingPallet firstActivePallet = getFirstActivePallet(pickingHeader);

        if (firstActivePallet == null){
            Intent intent = new Intent(this, PalletsActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, PalletContentActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, pickingHeader);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_PALLET, firstActivePallet);
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

        return firstAvailablePallet;
    }

    private void seeLogs(){
        Intent intent = new Intent(this, SeeLogsActivity.class);
        startActivity(intent);
    }
}
