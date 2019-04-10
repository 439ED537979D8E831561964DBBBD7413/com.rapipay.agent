package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Model.NetworkTransHistPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class NetworkTranHistAdapter extends RecyclerView.Adapter<NetworkTranHistAdapter.ViewHolder> {

    private ArrayList<NetworkTransHistPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_name,p_transid,btn_p_amounts,status,btn_number;
        private TextView btn_p_bank;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (TextView) view.findViewById(R.id.btn_p_bank);
            status = (AutofitTextView)view.findViewById(R.id.status);
            btn_number= (AutofitTextView)view.findViewById(R.id.btn_number);
        }
    }

    public NetworkTranHistAdapter(Context context, RecyclerView recyclerView, ArrayList<NetworkTransHistPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_network_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_p_amounts.setText(mValues.get(position).getTransactionDate());
        holder.btn_name.setText(mValues.get(position).getPayeeName());
        holder.p_transid.setText(format(mValues.get(position).getLastTxnAmount()));
        holder.btn_p_bank.setText(format(mValues.get(position).getTotalAmount()));
        holder.status.setText(mValues.get(position).getCompanyName());
        holder.btn_number.setText(mValues.get(position).getPayeeMobileNo());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    private String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(String.valueOf(Float.parseFloat(amount))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}






