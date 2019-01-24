package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.R;

public class BeneficiaryAdapter extends RecyclerView.Adapter<BeneficiaryAdapter.ViewHolder> {

    private ArrayList<BeneficiaryDetailsPozo> mValues;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView btn_name, btn_account, btn_bank, isverified;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (TextView) view.findViewById(R.id.btn_name);
            btn_account = (TextView) view.findViewById(R.id.btn_account);
            btn_bank = (TextView) view.findViewById(R.id.btn_bank);
            isverified = (TextView) view.findViewById(R.id.isverified);
        }
    }

    public BeneficiaryAdapter(Context context, ArrayList<BeneficiaryDetailsPozo> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beneficiary_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_bank.setText(mValues.get(position).getBank());
        holder.btn_name.setText(mValues.get(position).getName());
        holder.btn_account.setText(mValues.get(position).getAccountno());
        if (!mValues.get(position).getIfsc().equalsIgnoreCase("NOT-VEREFIED"))
            holder.isverified.setText("VEREFIED");
        else
            holder.isverified.setText(mValues.get(position).getIfsc());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

