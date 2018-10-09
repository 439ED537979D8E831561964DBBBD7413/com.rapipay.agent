package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.R;

public class ReceiptAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mValues;
    Context mContext;
    private  class ViewHolder {
        public AutofitTextView recycler_text;
    }

    public ReceiptAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.receipt_list, data);
        this.mValues = data;
        this.mContext=context;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.receipt_list, parent, false);
            viewHolder.recycler_text = (AutofitTextView) view.findViewById(R.id.recycler_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(position==1)
            viewHolder.recycler_text.setTypeface(viewHolder.recycler_text.getTypeface(), Typeface.BOLD);
        viewHolder.recycler_text.setText(mValues.get(position));
        // Return the completed view to render on screen
        return view;
    }
}
