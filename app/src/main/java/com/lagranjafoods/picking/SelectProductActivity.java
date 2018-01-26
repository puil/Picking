package com.lagranjafoods.picking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lagranjafoods.picking.adapters.PickingProductArrayAdapter;
import com.lagranjafoods.picking.models.PickingHeader;
import com.lagranjafoods.picking.models.PickingProduct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SelectProductActivity extends AppCompatActivity {

    ListView listView;
    PickingHeader pickingHeader;
    List<PickingProduct> pickingProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        Intent intent = getIntent();
        pickingHeader = (PickingHeader)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_HEADER);
        pickingProducts = (ArrayList<PickingProduct>)intent.getSerializableExtra(ExtraConstants.EXTRA_PICKING_PRODUCTS);

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
        mTitle.setText("Elige artículo");

        TextView textView_saleOrderNumber = findViewById(R.id.tvSaleOrderNumber);
        TextView textView_saleOrderDate = findViewById(R.id.tvSaleOrderDate);
        TextView textView_customerName = findViewById(R.id.tvCustomerName);

        textView_saleOrderNumber.setText(Integer.toString(pickingHeader.getSaleOrderNumber()));
        textView_saleOrderDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(pickingHeader.getSaleOrderDate()));
        textView_customerName.setText(pickingHeader.getCustomerName());
    }

    private void setupListView() {
        listView = findViewById(R.id.list);
        listView.setAdapter(new PickingProductArrayAdapter(this, pickingProducts));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                PickingProduct selectedPickingProduct = (PickingProduct) listView.getItemAtPosition(position);

                Intent intent = new Intent();
                intent.putExtra(ExtraConstants.EXTRA_SELECTED_PICKING_PRODUCT, selectedPickingProduct);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
