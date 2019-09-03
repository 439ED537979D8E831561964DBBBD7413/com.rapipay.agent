package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class BottomAdapter extends ArrayAdapter<HeaderePozo> {

    private ArrayList<HeaderePozo> mValues;
    Context mContext;

    private class ViewHolder {
        public View mView;
        public TextView btn_p_bank;
        public AutofitTextView btn_name;
        public LinearLayout top;
    }

    public BottomAdapter(ArrayList<HeaderePozo> data, Context context) {
        super(context, R.layout.bottom_layout, data);
        this.mValues = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
            if (view == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.bottom_layout, parent, false);
                viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
                viewHolder.btn_p_bank = (TextView) view.findViewById(R.id.btn_p_bank);
                viewHolder.top = (LinearLayout) view.findViewById(R.id.top);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (position % 2 == 0)
                viewHolder.top.setBackgroundColor(mContext.getResources().getColor(R.color.colorbackground));
            else
                viewHolder.top.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            if (mValues.get(position).getHeaderValue().equalsIgnoreCase("Txn. ID/RRN/STATUS")) {
                viewHolder.top.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                viewHolder.btn_name.setTextColor(mContext.getResources().getColor(R.color.white));
                viewHolder.btn_p_bank.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                viewHolder.btn_name.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                viewHolder.btn_p_bank.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }

            viewHolder.btn_name.setText(mValues.get(position).getHeaderData());
            viewHolder.btn_p_bank.setText(mValues.get(position).getHeaderValue());
        return view;
    }
}