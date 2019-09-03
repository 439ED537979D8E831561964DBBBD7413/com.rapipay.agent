package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Model.AEPSPendingPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MATMAEPSAdapter extends RecyclerView.Adapter<MATMAEPSAdapter.ViewHolder> {

    private ArrayList<AEPSPendingPozo> mValues;
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

    public MATMAEPSAdapter(Context context, ArrayList<AEPSPendingPozo> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aeps_matm_adp_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_bank.setText(formatss(mValues.get(position).getAmount()));
        holder.btn_name.setText(mValues.get(position).getCustomerName());
        holder.btn_account.setText(mValues.get(position).getCustomerMobile());
        holder.isverified.setText(mValues.get(position).getTxnDateTime());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private String formatss(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


