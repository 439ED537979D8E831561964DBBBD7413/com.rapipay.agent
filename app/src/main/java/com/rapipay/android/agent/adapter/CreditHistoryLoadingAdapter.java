package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Model.CreditHistoryPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class CreditHistoryLoadingAdapter extends RecyclerView.Adapter<CreditHistoryLoadingAdapter.ViewHolder> {

    private ArrayList<CreditHistoryPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_name,p_transid,btn_p_amounts,status;
        private TextView btn_p_bank,btn_p_amount,btn_p_bank_details;
        private LinearLayout ln_req_type;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (TextView) view.findViewById(R.id.btn_p_bank);
            btn_p_amount = (TextView) view.findViewById(R.id.btn_p_amount);
            btn_p_bank_details = (TextView) view.findViewById(R.id.btn_p_bank_details);
            status = (AutofitTextView)view.findViewById(R.id.status);
            ln_req_type = (LinearLayout) view.findViewById(R.id.ln_req_type);
        }
    }

    public CreditHistoryLoadingAdapter(Context context, RecyclerView recyclerView, ArrayList<CreditHistoryPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public CreditHistoryLoadingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_history_adapter, parent, false);
        return new CreditHistoryLoadingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CreditHistoryLoadingAdapter.ViewHolder holder, final int position) {
        holder.ln_req_type.setVisibility(View.GONE);
        holder.btn_p_amounts.setText(mValues.get(position).getrequesttype());
        holder.btn_p_amount.setText(mValues.get(position).getAmount());
        holder.btn_name.setText(mValues.get(position).getRequestId());
        holder.p_transid.setText(format(mValues.get(position).getAmount()));
        holder.btn_p_bank.setText(mValues.get(position).getRemark());
        holder.status.setText(mValues.get(position).getStatus());
        holder.btn_p_bank_details.setText(mValues.get(position).getrequesttype());
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

