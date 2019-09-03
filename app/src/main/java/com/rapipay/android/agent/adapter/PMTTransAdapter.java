package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Model.PMTTransactionHistory;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PMTTransAdapter extends RecyclerView.Adapter<PMTTransAdapter.ViewHolder> {

    private ArrayList<PMTTransactionHistory> mValues;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView accountNo,userTxnId,requestAmt,serviceProviderTXNID,beneficiaryName,bankName,txnDateTime,txnStatus;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            accountNo = (TextView) view.findViewById(R.id.accountNo);
            userTxnId = (TextView) view.findViewById(R.id.userTxnId);
            requestAmt = (TextView) view.findViewById(R.id.requestAmt);
            serviceProviderTXNID = (TextView) view.findViewById(R.id.serviceProviderTXNID);
            beneficiaryName = (TextView) view.findViewById(R.id.beneficiaryName);
            bankName = (TextView) view.findViewById(R.id.bankName);
            txnDateTime = (TextView)view.findViewById(R.id.txnDateTime);
            txnStatus = (TextView)view.findViewById(R.id.txnStatus);
        }
    }

    public PMTTransAdapter(Context context, ArrayList<PMTTransactionHistory> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pmt_adapter_trans_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.requestAmt.setText(format(mValues.get(position).getRequestAmt()));
        holder.accountNo.setText(mValues.get(position).getAccountNo());
        holder.userTxnId.setText(mValues.get(position).getUserTxnId());
        holder.serviceProviderTXNID.setText(mValues.get(position).getServiceProviderTXNID());
        holder.beneficiaryName.setText(mValues.get(position).getBeneficiaryName());
        holder.bankName.setText(mValues.get(position).getBankName());
        holder.txnDateTime.setText(mValues.get(position).getTxnDateTime());
        holder.txnStatus.setText(mValues.get(position).getTxnStatus());
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



