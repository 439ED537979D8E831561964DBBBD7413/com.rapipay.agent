package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pnsol.sdk.vo.AcquirerEmiDetailsVO;
import com.rapipay.android.agent.R;

import java.util.ArrayList;


public class EMITenureAdapter extends ArrayAdapter<AcquirerEmiDetailsVO> {
    private ArrayList<AcquirerEmiDetailsVO> mValues;
    Context mContext;

    public EMITenureAdapter(ArrayList<AcquirerEmiDetailsVO> mValues, Context context) {
        super(context, R.layout.tenure_layout);
        this.mValues = mValues;
        this.mContext = context;
    }

    private class ViewHolder {
        public View mView;
        public TextView amount, percentage, tenure;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.tenure_layout, parent, false);
            viewHolder.amount = (TextView) view.findViewById(R.id.amount);
            viewHolder.percentage = (TextView) view.findViewById(R.id.percentage);
            viewHolder.tenure = (TextView) view.findViewById(R.id.tenure);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.amount.setText(String.valueOf(mValues.get(position).getEmiAmount()));
        viewHolder.percentage.setText(String.valueOf(mValues.get(position).getEmiPercentage()));
        viewHolder.tenure.setText(String.valueOf(mValues.get(position).getEmiTenure()));
        return view;
    }
}