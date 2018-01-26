package com.lagranjafoods.picking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lagranjafoods.picking.R;
import com.lagranjafoods.picking.models.ProductStock;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by joang on 25/01/2018.
 */

public class ProductStockArrayAdapter extends ArrayAdapter<ProductStock> {
    private final Context context;
    private List<ProductStock> values;

    public ProductStockArrayAdapter(Context context, List<ProductStock> values) {
        super(context, R.layout.list_product_stocks, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_product_stocks, parent, false);
        ProductStock productStock = values.get(position);

        TextView textView_lot = rowView.findViewById(R.id.tvLot);
        textView_lot.setText(productStock.getLot());

        TextView textView_expirationDate = rowView.findViewById(R.id.tvExpirationDate);
        textView_expirationDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(productStock.getExpirationDate()));

        TextView textView_amount = rowView.findViewById(R.id.tvAmount);
        Integer intValueOfAmount = Double.valueOf(productStock.getAmount()).intValue();
        textView_amount.setText(Integer.toString(intValueOfAmount));

        return rowView;
    }
}