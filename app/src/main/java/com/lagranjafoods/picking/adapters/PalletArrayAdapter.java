package com.lagranjafoods.picking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lagranjafoods.picking.R;
import com.lagranjafoods.picking.models.PalletStateEnum;
import com.lagranjafoods.picking.models.PickingPallet;

import java.util.List;

/**
 * Created by joang on 20/01/2018.
 */

public class PalletArrayAdapter extends ArrayAdapter<PickingPallet> {
    private final Context context;
    private List<PickingPallet> values;

    public PalletArrayAdapter(Context context, List<PickingPallet> values) {
        super(context, R.layout.list_pallets, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_pallets, parent, false);
        PickingPallet pickingPallet = values.get(position);

        TextView textView_palletNumber = rowView.findViewById(R.id.tvPalletNumber);
        textView_palletNumber.setText(Integer.toString(pickingPallet.getPalletNumber()));

        TextView textView_state = rowView.findViewById(R.id.tvPalletState);
        textView_state.setText("(" + getStateText(pickingPallet.getState()) + ")");

        ImageButton deleteButton = rowView.findViewById(R.id.deleteButton);
        deleteButton.setTag(pickingPallet);

        return rowView;
    }

    public void refreshValues(List<PickingPallet> values){
        this.values.clear();
        this.values.addAll(values);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return this.values.isEmpty();
    }

    private String getStateText(PalletStateEnum palletStateEnum){
        switch (palletStateEnum){
            case Confirmed:
                return "confirmado";
            case Palletized:
                return "paletizado";
            case Shipped:
                return "expedido";
            default:
                return "en curso";
        }
    }
}
