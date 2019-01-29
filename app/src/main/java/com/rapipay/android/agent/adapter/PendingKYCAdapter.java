package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rapipay.android.agent.Model.PendingKYCPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;
public class PendingKYCAdapter extends ArrayAdapter<PendingKYCPozo> {

    private ArrayList<PendingKYCPozo> mValues;
    private Context context;

    public class ViewHolder {
        public TextView btn_p_bank,btn_name,p_transid,btn_p_amounts,remark,status,createdon;
    }

    public PendingKYCAdapter(ArrayList<PendingKYCPozo> items,Context context) {
        super(context, R.layout.pending_layut_adapter, items);
        mValues = items;
        this.context = context;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.pending_layut_adapter, parent, false);
            viewHolder.btn_name = (TextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (TextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (TextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (TextView)view.findViewById(R.id.btn_p_bank);
            viewHolder.createdon = (TextView) view.findViewById(R.id.createdon);
            viewHolder.status = (TextView) view.findViewById(R.id.status);
            viewHolder.remark = (TextView)view.findViewById(R.id.remark);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.btn_p_amounts.setText(mValues.get(position).getCompanyName());
        viewHolder.btn_name.setText(mValues.get(position).getFullName()+" ("+mValues.get(position).getMobileNo()+") ");
        viewHolder.p_transid.setText(mValues.get(position).getEmailId());
        viewHolder.btn_p_bank.setText(mValues.get(position).getFullAddress()+" ("+mValues.get(position).getStateName()+") ");
        viewHolder.createdon.setText("Created On - "+mValues.get(position).getCreationDate());
        viewHolder.remark.setText("Remarks - "+mValues.get(position).getRemarks());
        viewHolder.status.setText("Status - "+mValues.get(position).getStatusAction());
        return view;
    }
}





