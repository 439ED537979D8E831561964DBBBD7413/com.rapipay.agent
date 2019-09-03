package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

public class WalletBeneficiaryAdapter extends RecyclerView.Adapter<WalletBeneficiaryAdapter.ViewHolder> {

    private ArrayList<BeneficiaryDetailsPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView btn_name, btn_account, btn_bank, btn_accountname;
        public final LinearLayout accname;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (TextView) view.findViewById(R.id.btn_name);
            btn_account = (TextView) view.findViewById(R.id.btn_account);
            btn_bank = (TextView) view.findViewById(R.id.btn_bank);
            btn_accountname = (TextView) view.findViewById(R.id.btn_accountname);
            accname = (LinearLayout) view.findViewById(R.id.accname);
        }
    }

    public WalletBeneficiaryAdapter(Context context, RecyclerView recyclerView, ArrayList<BeneficiaryDetailsPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_beneficiary_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_bank.setText(mValues.get(position).getAccountno());
        holder.btn_name.setText(mValues.get(position).getBeneficiaryId());
        holder.btn_account.setText(mValues.get(position).getName());
        if (mValues.get(position).getBank().equalsIgnoreCase(""))
            holder.accname.setVisibility(View.GONE);
        holder.btn_accountname.setText(mValues.get(position).getBank());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}


