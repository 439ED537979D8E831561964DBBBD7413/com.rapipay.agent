package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rapipay.android.agent.Model.CommissionPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class DailyAdapter extends ArrayAdapter<CommissionPozo> {

    private ArrayList<CommissionPozo> mValues;
    private Context context;

    public class ViewHolder  {
        public AutofitTextView btn_name,p_transid,btn_p_amounts,status;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }
    public DailyAdapter(Context context, ArrayList<CommissionPozo> items) {
        super(context, R.layout.commission_adapter_layout, items);
        this.mValues = items;
        this.context = context;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        CommissionPozo dataModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.commission_adapter_layout, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.status = (AutofitTextView) view.findViewById(R.id.status);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }
        viewHolder.btn_p_amounts.setText(mValues.get(position).getTxnDate());
        viewHolder.btn_name.setText(mValues.get(position).getServiceName());
        viewHolder.p_transid.setText(mValues.get(position).getTxncrdrAmount());
        viewHolder.status.setText(mValues.get(position).getTransactionStatus());
        return view;

    }
}






