package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rapipay.android.agent.Model.SettlementPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

public class SettlementPayloadDeleteAdapter extends RecyclerView.Adapter<SettlementPayloadDeleteAdapter.ViewHolder> {

    private ArrayList<SettlementPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Button account_button;
        private TextView account_name, bank_account_name, account_number, account_status;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            account_name = (TextView) view.findViewById(R.id.account_name);
            bank_account_name = (TextView) view.findViewById(R.id.bank_account_name);
            account_number = (TextView) view.findViewById(R.id.account_number);
            account_status = (TextView) view.findViewById(R.id.account_status);
            account_button = (Button) view.findViewById(R.id.account_button);
        }
    }

    public SettlementPayloadDeleteAdapter(Context context, RecyclerView recyclerView, ArrayList<SettlementPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settlement_payload_ladp_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.account_name.setText(mValues.get(position).getAgentName());
        holder.bank_account_name.setText(mValues.get(position).getAgentBankName());
        holder.account_number.setText(mValues.get(position).getAgentAccountNO());
        if (mValues.get(position).getAccountStatus().equalsIgnoreCase("0"))
            holder.account_status.setText("Pending");
        else if (mValues.get(position).getAccountStatus().equalsIgnoreCase("1"))
            holder.account_status.setText("Approved");
        else if (mValues.get(position).getAccountStatus().equalsIgnoreCase("2"))
            holder.account_status.setText("DENIED");
        else if (mValues.get(position).getAccountStatus().equalsIgnoreCase("3"))
            holder.account_status.setText("De-activate");
        holder.account_button.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


}




