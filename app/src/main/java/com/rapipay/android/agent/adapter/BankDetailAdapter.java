package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Model.BankDetailPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

public class BankDetailAdapter extends RecyclerView.Adapter<BankDetailAdapter.ViewHolder> {

    private ArrayList<BankDetailPozo> mValues;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView btn_name, btn_account, btn_bank, isverified,ifsc;
        public LinearLayout ifsc_lay;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (TextView) view.findViewById(R.id.btn_name);
            btn_account = (TextView) view.findViewById(R.id.btn_account);
            btn_bank = (TextView) view.findViewById(R.id.btn_bank);
            isverified = (TextView) view.findViewById(R.id.isverified);
            ifsc = (TextView) view.findViewById(R.id.ifsc);
            ifsc_lay = (LinearLayout)view.findViewById(R.id.ifsc_lay);
        }
    }

    public BankDetailAdapter(Context context, ArrayList<BankDetailPozo> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_detail_adp_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_bank.setText(mValues.get(position).getACNO());
        holder.btn_name.setText(mValues.get(position).getBANK());
        holder.btn_account.setText(mValues.get(position).getNAME());
        holder.isverified.setText(mValues.get(position).getDEPOSIT());
        holder.ifsc.setText(mValues.get(position).getIFSC());
        if(mValues.get(position).getIFSC().isEmpty())
            holder.ifsc_lay.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

