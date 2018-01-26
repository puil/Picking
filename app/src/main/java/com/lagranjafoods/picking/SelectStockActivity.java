package com.lagranjafoods.picking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lagranjafoods.picking.adapters.ProductStockArrayAdapter;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.ProductStock;
import com.lagranjafoods.picking.models.Warehouses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SelectStockActivity extends AppCompatActivity {

    ListView manufacturingAreaListView;
    ListView warehouseListView;
    PickingHeader pickingHeader;
    List<ProductStock> productStocks;
    List<ProductStock> manufacturingAreaProductStocks;
    List<ProductStock> warehouseProductStocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stock);

        Intent intent = getIntent();
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);
        productStocks = (ArrayList<ProductStock>)intent.getSerializableExtra(ExtraConstants.EXTRA_PRODUCTS_STOCKS);

        manufacturingAreaProductStocks = new ArrayList<>();
        warehouseProductStocks = new ArrayList<>();

        setupToolBar();
        setupListViews();
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
    public void onContentChanged() {
        super.onContentChanged();

        View emptyStockInManufacturingArea = findViewById(R.id.emptyStockInManufacturingArea);
        ListView manufacturingAreaList = findViewById(R.id.manufacturingAreaList);
        manufacturingAreaList.setEmptyView(emptyStockInManufacturingArea);

        View emptyStockInWarehouse = findViewById(R.id.emptyStockInWarehouse);
        ListView warehouseList = findViewById(R.id.warehouseList);
        warehouseList.setEmptyView(emptyStockInWarehouse);
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
        mTitle.setText("Elige stock");

        TextView textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        TextView textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        TextView textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(pickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(pickingHeader.getSaleOrderDate()));
        textView_customerName.setText(pickingHeader.getCustomerName());
    }

    private void setupListViews() {
        fillProductStockArrayLists();
        setupManufacturingAreaListView();
        setupWarehouseListView();
    }

    private void fillProductStockArrayLists() {
        for (ProductStock productStock : productStocks) {
            if (productStock.getWarehouseId() == Warehouses.ManufacturingArea)
                manufacturingAreaProductStocks.add(productStock);
            else if (productStock.getWarehouseId() == Warehouses.Warehouse)
                warehouseProductStocks.add(productStock);
        }
    }

    private void setupManufacturingAreaListView(){
        manufacturingAreaListView = findViewById(R.id.manufacturingAreaList);
        manufacturingAreaListView.setAdapter(new ProductStockArrayAdapter(this, manufacturingAreaProductStocks));

        manufacturingAreaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                ProductStock selectedProductStock = (ProductStock) manufacturingAreaListView.getItemAtPosition(position);

                Intent intent = new Intent();
                intent.putExtra(ExtraConstants.EXTRA_SELECTED_PRODUCT_STOCK, selectedProductStock);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setupWarehouseListView() {
        warehouseListView = findViewById(R.id.warehouseList);
        warehouseListView.setAdapter(new ProductStockArrayAdapter(this, warehouseProductStocks));

        warehouseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                ProductStock selectedProductStock = (ProductStock) warehouseListView.getItemAtPosition(position);

                Intent intent = new Intent();
                intent.putExtra(ExtraConstants.EXTRA_SELECTED_PRODUCT_STOCK, selectedProductStock);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
