package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Model.WalletTransPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder> {

    private ArrayList<WalletTransPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView btn_name, btn_account, btn_bank, btn_accountname, btn_amount;
        public final LinearLayout accname;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (TextView) view.findViewById(R.id.btn_name);
            btn_account = (TextView) view.findViewById(R.id.btn_account);
            btn_bank = (TextView) view.findViewById(R.id.btn_bank);
            btn_accountname = (TextView) view.findViewById(R.id.btn_accountname);
            accname = (LinearLayout) view.findViewById(R.id.accname);
            btn_amount = (TextView) view.findViewById(R.id.btn_amount);
        }
    }

    public WalletTransactionAdapter(Context context, RecyclerView recyclerView, ArrayList<WalletTransPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_trans_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_bank.setText(mValues.get(position).getAccountNumber());
        holder.btn_name.setText(mValues.get(position).getBcBeneId());
        holder.btn_account.setText(mValues.get(position).getBankName());
        holder.btn_amount.setText(formatss(mValues.get(position).getTxnAmount()));
        holder.btn_accountname.setText(mValues.get(position).getBankAccountName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    protected String formatss(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}



