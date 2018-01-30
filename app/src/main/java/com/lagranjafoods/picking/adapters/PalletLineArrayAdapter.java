package com.lagranjafoods.picking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lagranjafoods.picking.R;
import com.lagranjafoods.picking.models.PickingPalletLine;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by joang on 20/01/2018.
 */

public class PalletLineArrayAdapter extends ArrayAdapter<PickingPalletLine> {
    private final Context context;
    private List<PickingPalletLine> values;

    public PalletLineArrayAdapter(Context context, List<PickingPalletLine> values) {
        super(context, R.layout.list_pallet_lines, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_pallet_lines, parent, false);
        PickingPalletLine palletLine = values.get(position);

        TextView textView_amount = rowView.findViewById(R.id.tvAmount);
        Integer intValueOfAmount = Double.valueOf(palletLine.getAmount()).intValue();
        textView_amount.setText(Integer.toString(intValueOfAmount));

        TextView textView_productDescription = rowView.findViewById(R.id.tvProductDescription);
        textView_productDescription.setText(String.format("%d - %s", palletLine.getProductId(), palletLine.getProductDescription()));

        TextView textView_lot = rowView.findViewById(R.id.tvLot);
        textView_lot.setText(palletLine.getLot());

        TextView textView_expirationDate = rowView.findViewById(R.id.tvExpirationDate);
        textView_expirationDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(palletLine.getExpirationDate()));

        ImageButton deleteButton = rowView.findViewById(R.id.btnDeletePalletLine);
        deleteButton.setTag(palletLine);

        return rowView;
    }

    public void refreshValues(List<PickingPalletLine> values){
        this.values.clear();
        this.values.addAll(values);
        notifyDataSetChanged();
    }
}
