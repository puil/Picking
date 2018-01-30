package com.lagranjafoods.picking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lagranjafoods.picking.R;
import com.lagranjafoods.picking.models.PickingProduct;

import java.util.List;

/**
 * Created by joang on 25/01/2018.
 */

public class PickingProductArrayAdapter extends ArrayAdapter<PickingProduct> {
    private final Context context;
    private List<PickingProduct> values;

    public PickingProductArrayAdapter(Context context, List<PickingProduct> values) {
        super(context, R.layout.list_picking_products, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_picking_products, parent, false);
        PickingProduct pickingProduct = values.get(position);

        TextView textView_productDescription = rowView.findViewById(R.id.tvProductDescription);
        textView_productDescription.setText(String.format("%d - %s", pickingProduct.getProductId(), pickingProduct.getProductDescription()));

        TextView textView_saleOrderLineNumber = rowView.findViewById(R.id.tvSaleOrderLineNumber);
        textView_saleOrderLineNumber.setText(String.format("%d", pickingProduct.getSaleOrderLineNumber()));

        TextView textView_amountInSaleOrderLine = rowView.findViewById(R.id.tvAmountInSaleOrderLine);
        textView_amountInSaleOrderLine.setText(String.format("%d de %d cajas", pickingProduct.getSaleOrderLinePickedUnits(), pickingProduct.getSaleOrderLineUnits()));

        return rowView;
    }
}
