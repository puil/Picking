package com.lagranjafoods.picking;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.lagranjafoods.picking.helpers.StringHelper;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingPallet;
import com.lagranjafoods.picking.models.PickingProduct;
import com.lagranjafoods.picking.models.PickingResponse;
import com.lagranjafoods.picking.models.ProductStock;
import com.lagranjafoods.picking.models.Warehouses;
import com.lagranjafoods.picking.network.AppController;
import com.lagranjafoods.picking.network.GsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends BaseActivity {
    private static final String TAG = "AddProductActivity";

    public static int SELECT_PICKING_PRODUCT_REQUESTCODE = 101;
    public static int SELECT_PRODUCT_STOCK_REQUESTCODE = 102;

    TextView textView_saleOrderNumber;
    TextView textView_saleOrderDate;
    TextView textView_customerName;
    TextView textView_productDescription;
    TextView textView_saleOrderLineNumber;
    TextView textView_amountInSaleOrderLine;
    TextView textView_sourceWarehouse;
    TextView textView_lot;
    TextView textView_expirationDate;
    TextView textView_amountInSourceWarehouse;
    TextView textView_stockMessage;

    TableLayout stockTableLayout;

    EditText editText_productCode;
    EditText editText_productBarcode;
    EditText editText_amount;
    Button button_addProductByCode;
    Button button_confirm;
    ImageButton button_clearSelectedProduct;
    ImageButton button_selectSourceStock;
    View readBarcodeView;
    View allDataView;

    PickingHeader currentPickingHeader;
    PickingPallet currentPickingPallet;
    PickingProduct currentPickingProduct;
    ProductStock currentProductStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        textView_productDescription = findViewById(R.id.tvProductDescription);
        textView_saleOrderLineNumber = findViewById(R.id.tvSaleOrderLineNumber);
        textView_amountInSaleOrderLine = findViewById(R.id.tvAmountInSaleOrderLine);
        textView_sourceWarehouse = findViewById(R.id.tvSourceWarehouse);
        textView_lot = findViewById(R.id.tvLot);
        textView_expirationDate = findViewById(R.id.tvExpirationDate);
        textView_amountInSourceWarehouse = findViewById(R.id.tvAmountInSource);
        textView_stockMessage = findViewById(R.id.lblStockMessage);

        stockTableLayout = findViewById(R.id.stockTableLayout);

        editText_productCode = findViewById(R.id.editProductCode);
        editText_amount = findViewById(R.id.editTextAmountToAdd);
        editText_productBarcode = findViewById(R.id.editBarcodeNumber);

        editText_productCode.setOnEditorActionListener(editorActionListener);
        editText_amount.setOnEditorActionListener(editorActionListener);
        editText_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkAndEnableConfirmButton();
            }
        });
        editText_productBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringHelper.isNullOrEmpty(editText_productBarcode.getText().toString())){
                    return;
                }

                searchProductByBarcodeNumber(editText_productBarcode.getText().toString());
                editText_productBarcode.setText("");
            }
        });

        editText_productBarcode.requestFocus();

        button_addProductByCode = findViewById(R.id.btnAddProductByCode);
        button_clearSelectedProduct = findViewById(R.id.btnClearSelectedProduct);
        button_selectSourceStock = findViewById(R.id.btnSelectSourceStock);
        button_confirm = findViewById(R.id.btnConfirm);

        readBarcodeView = findViewById(R.id.readBarcodeView);
        allDataView = findViewById(R.id.allDataScrollView);

        readBarcodeView.setVisibility(View.VISIBLE);
        allDataView.setVisibility(View.GONE);

        hideKeyboard();

        button_addProductByCode.setOnClickListener(this);
        button_clearSelectedProduct.setOnClickListener(this);
        button_selectSourceStock.setOnClickListener(this);
        button_confirm.setOnClickListener(this);

        button_confirm.setEnabled(false);

        Intent intent = getIntent();
        currentPickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);
        currentPickingPallet = (PickingPallet)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_PALLET);

        setupToolBar();
    }

    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICKING_PRODUCT_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                PickingProduct selectedPickingProduct = (PickingProduct)data.getSerializableExtra(ExtraConstants.EXTRA_SELECTED_PICKING_PRODUCT);
                setCurrentProduct(selectedPickingProduct);
            }
        }
        else if (requestCode == SELECT_PRODUCT_STOCK_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                ProductStock selectedProductStock = (ProductStock)data.getSerializableExtra(ExtraConstants.EXTRA_SELECTED_PRODUCT_STOCK);
                setCurrentProductStock(selectedProductStock);
            }
            else{
                clearSelectedProductStock();
            }
        }
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
        mTitle.setText("Añadir producto");

        textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(currentPickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(currentPickingHeader.getSaleOrderDate()));
        textView_customerName.setText(currentPickingHeader.getCustomerName());
    }

    public void pressedOnClick(View view){
        switch (view.getId()){
            case R.id.btnAddProductByCode:
                searchProductByCode();
                break;

            case R.id.btnClearSelectedProduct:
                clearSelectedProduct();
                break;

            case R.id.btnSelectSourceStock:
                selectSourceStock();
                break;

            case R.id.btnConfirm:
                confirm();
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
                switch (textView.getId()){
                    case R.id.editProductCode:
                        searchProductByCode();
                        return true;

                    case R.id.editTextAmountToAdd:
                        confirm();
                        return true;
                }
            }
            return false;
        }
    };

    /*
     * ----------------------------------------------------------------------------
     * Product
     * ----------------------------------------------------------------------------
     */
    private void searchProductByBarcodeNumber(String barcodeNumber) {
        if (StringHelper.isNullOrEmpty(barcodeNumber)){
            showToast("No hay código de barras leído...");
            return;
        }

        String url = getUrl(R.string.pickingEndpoint)
                + "products/withBarcode?pickingHeaderId=" + currentPickingHeader.getId()
                + "&barcodeNumber=" + barcodeNumber;

        showProgressDialog("Buscando artículo...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    hideKeyboard();
                    editText_productCode.setText("");
                    handleReceivedPickingProducts(response.getPickingProducts());
                }
                else {
                    showToastWithErrorMessageFromResponse(response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void searchProductByCode() {
        if (StringHelper.isNullOrEmpty(editText_productCode.getText().toString())){
            showToast("Informa el código de artículo");
            return;
        }

        String url = getUrl(R.string.pickingEndpoint)
                + "products/withCode?pickingHeaderId=" + currentPickingHeader.getId()
                + "&productId=" + editText_productCode.getText();

        showProgressDialog("Buscando artículo...");

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.GET, url, PickingResponse.class, null, null, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    hideKeyboard();
                    editText_productCode.setText("");
                    handleReceivedPickingProducts(response.getPickingProducts());
                }
                else {
                    showToastWithErrorMessageFromResponse("", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void setCurrentProduct(PickingProduct pickingProduct){
        currentPickingProduct = pickingProduct;

        textView_productDescription.setText(String.format("%d - %s", currentPickingProduct.getProductId(), currentPickingProduct.getProductDescription()));
        textView_saleOrderLineNumber.setText(Integer.toString(currentPickingProduct.getSaleOrderLineNumber()));
        textView_amountInSaleOrderLine.setText(String.format("%d de %d cajas", currentPickingProduct.getSaleOrderLinePickedUnits(), currentPickingProduct.getSaleOrderLineUnits()));

        crossfadeViews(readBarcodeView, allDataView);

        if (currentPickingProduct.isProductIsCommercial()){
            // No need to look for stock
            textView_stockMessage.setText("El artículo es de comercial");
            textView_stockMessage.setVisibility(View.VISIBLE);
            stockTableLayout.setVisibility(View.INVISIBLE);
            checkAndEnableConfirmButton();
        } else {
            getAvailableStockSources(false);
        }
    }

    private void handleReceivedPickingProducts(List<PickingProduct> pickingProducts){
        if (pickingProducts.isEmpty()){
            showToast("No se ha recibido ningún producto del servidor");
        }
        else if (pickingProducts.size() == 1){
            setCurrentProduct(pickingProducts.get(0));
        }
        else {
            Intent intent = new Intent(this, SelectProductActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, currentPickingHeader);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_PRODUCTS, (ArrayList<PickingProduct>) pickingProducts);
            startActivityForResult(intent, SELECT_PICKING_PRODUCT_REQUESTCODE);
        }
    }

    private void clearSelectedProduct() {
        clearSelectedProductStock();

        currentPickingProduct = null;

        textView_productDescription.setText("");
        textView_saleOrderLineNumber.setText("");
        textView_amountInSaleOrderLine.setText("");

        crossfadeViews(allDataView, readBarcodeView);
    }

    /*
     * ----------------------------------------------------------------------------
     * Stock
     * ----------------------------------------------------------------------------
     */

    public void getAvailableStockSources(final boolean forceOpeningSelectStockActivity) {
        String url = getUrl(R.string.stockEndpoint) + "?warehouseIds=1&warehouseIds=2&product=" + currentPickingProduct.getProductId();

        showProgressDialog("Buscando stock...");

        Type productStockListType = new TypeToken<List<ProductStock>>(){}.getType();

        GsonRequest<List<ProductStock>> gsonRequest = new GsonRequest<>(Request.Method.GET, url, productStockListType, null, null, new Response.Listener<List<ProductStock>>() {

            @Override
            public void onResponse(List<ProductStock> response) {
                handleReceivedProductStocks(response, forceOpeningSelectStockActivity);
                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }

    private void handleReceivedProductStocks(List<ProductStock> productStockList, boolean forceOpeningSelectStockActivity) {
        boolean openSelectStockActivity = forceOpeningSelectStockActivity;

        if (productStockList.isEmpty()){
            textView_stockMessage.setText("No hay stock");
            textView_stockMessage.setVisibility(View.VISIBLE);
            stockTableLayout.setVisibility(View.INVISIBLE);
        } else if (!openSelectStockActivity && productStockList.size() == 1){
            setCurrentProductStock(productStockList.get(0));
            showToast("Se ha asignado el stock automáticamente porque sólo hay un lote/ubicación");
        }
        else if (productStockList.size() > 1) {
            textView_stockMessage.setText("Stock no seleccionado");
            textView_stockMessage.setVisibility(View.VISIBLE);
            stockTableLayout.setVisibility(View.INVISIBLE);

            openSelectStockActivity = true;
        }

        if (openSelectStockActivity){
            Intent intent = new Intent(this, SelectStockActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_PICKING_HEADER, currentPickingHeader);
            intent.putExtra(ExtraConstants.EXTRA_PRODUCTS_STOCKS, (ArrayList<ProductStock>) productStockList);
            startActivityForResult(intent, SELECT_PRODUCT_STOCK_REQUESTCODE);
        }
    }

    private void selectSourceStock() {
        if (currentPickingProduct.isProductIsCommercial())
            showToast("El artículo es de comercial. No hay gestión de stock para productos de comercial");
        else
            getAvailableStockSources(true);
    }

    private void setCurrentProductStock(ProductStock productStock){
        currentProductStock = productStock;

        stockTableLayout.setVisibility(View.VISIBLE);
        textView_stockMessage.setVisibility(View.GONE);

        textView_sourceWarehouse.setText(Warehouses.getDescription(currentProductStock.getWarehouseId()));
        Integer intValueOfAmount = Double.valueOf(currentProductStock.getAmount()).intValue();
        textView_amountInSourceWarehouse.setText(String.format("%d cajas", intValueOfAmount));

        if (currentProductStock.getExpirationDate() != null) {
            textView_lot.setText(currentProductStock.getLot());
            textView_expirationDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(currentProductStock.getExpirationDate()));
        }
        else {
            textView_lot.setText(currentProductStock.getLot());
            textView_expirationDate.setText("-");
        }

        checkAndEnableConfirmButton();
    }

    private void clearSelectedProductStock() {
        currentProductStock = null;

        stockTableLayout.setVisibility(View.INVISIBLE);
        textView_stockMessage.setVisibility(View.VISIBLE);
        textView_stockMessage.setText("Stock no seleccionado");

        textView_sourceWarehouse.setText("");
        textView_lot.setText("");
        textView_expirationDate.setText("");
        textView_amountInSourceWarehouse.setText("");
    }

    /*
     * ----------------------------------------------------------------------------
     * Confirm
     * ----------------------------------------------------------------------------
     */

    private void checkAndEnableConfirmButton() {
        if (currentPickingProduct != null
                && ((!currentPickingProduct.isProductIsCommercial() && currentProductStock != null)
                    || currentPickingProduct.isProductIsCommercial() && currentProductStock == null)
                && !StringHelper.isNullOrEmpty(editText_amount.getText().toString()))
            button_confirm.setEnabled(true);
        else
            button_confirm.setEnabled(false);
    }

    private void confirm() {
        String url = getUrl(R.string.pickingEndpoint) + "palletLines";

        showProgressDialog("Añadiendo artículo...");

        JSONObject jsonBody = new JSONObject();
        String mRequestBody = null;

        try {
            jsonBody.put("PickingPalletId", currentPickingPallet.getId());
            jsonBody.put("SaleOrderLineId", currentPickingProduct.getSaleOrderLineId());
            jsonBody.put("ProductId", currentPickingProduct.getProductId());
            jsonBody.put("Amount", Integer.parseInt(editText_amount.getText().toString()));
            jsonBody.put("UserId", 1);

            if (currentProductStock != null){
                if (currentProductStock.getExpirationDate() != null) {
                    jsonBody.put("ExpirationDate", getDateParsedForJsonBody(currentProductStock.getExpirationDate()));
                }

                jsonBody.put("Lot", currentProductStock.getLot());
                jsonBody.put("SourceWarehouseId", currentProductStock.getWarehouseId());
            }
            else{
                jsonBody.put("SourceWarehouseId", Warehouses.WAREHOUSE);
            }

            mRequestBody = jsonBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Vaig a fer la crida a la url " + url + "\nEl jsonBody es:\n" + jsonBody.toString());

        GsonRequest<PickingResponse> gsonRequest = new GsonRequest<>(Request.Method.POST, url, PickingResponse.class, null, mRequestBody, new Response.Listener<PickingResponse>() {

            @Override
            public void onResponse(PickingResponse response) {
                if (response.isSuccess()){
                    hideKeyboard();
                    showToast("Artículo añadido correctamente");
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    showToastWithErrorMessageFromResponse("No se ha podido añadir el artículo en el palet por el siguiente motivo:\n\n", response);
                }

                hideProgressDialog();
            }
        }, volleyErrorListener);

        AppController.getInstance(this).addToRequestQueue(gsonRequest);
    }
}
