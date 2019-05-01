package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;
import java.util.Locale;

public class BCBeneficiaryAdapter extends RecyclerView.Adapter<BCBeneficiaryAdapter.ViewHolder> {

    private ArrayList<BeneficiaryDetailsPozo> mValues;
    private Context context;
    private ArrayList<BeneficiaryDetailsPozo> arraylist = null;
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

    public BCBeneficiaryAdapter(Context context, ArrayList<BeneficiaryDetailsPozo> items) {
        mValues = items;
        this.context = context;
        this.arraylist = new ArrayList<BeneficiaryDetailsPozo>();
        this.arraylist.addAll(mValues);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bene_layout_new, parent, false);
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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(arraylist);
        } else {
            for (BeneficiaryDetailsPozo wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)||wp.getBank().toLowerCase(Locale.getDefault()).contains(charText)||wp.getAccountno().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mValues.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}


