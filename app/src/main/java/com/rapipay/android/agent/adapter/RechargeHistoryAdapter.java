package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rapipay.android.agent.Model.RechargePozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class RechargeHistoryAdapter extends RecyclerView.Adapter<RechargeHistoryAdapter.ViewHolder> {

    private ArrayList<RechargePozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,txnStatus,txnid;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            txnStatus = (AutofitTextView) view.findViewById(R.id.txnStatus);
            txnid = (AutofitTextView)view.findViewById(R.id.txnid);
        }
    }

    public RechargeHistoryAdapter(Context context, RecyclerView recyclerView, ArrayList<RechargePozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_historu_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_p_amounts.setText(mValues.get(position).getRechargeType());
        holder.btn_name.setText(mValues.get(position).getOperatorName());
        holder.p_transid.setText(mValues.get(position).getMobileNo());
        holder.btn_p_bank.setText(format(mValues.get(position).getTxnAmount()))
        ;
        holder.txnid.setText(mValues.get(position).getTransactionID());
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





