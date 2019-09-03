package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.CreditHistoryPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class CreditHistoryAdapter extends RecyclerView.Adapter<CreditHistoryAdapter.ViewHolder> {

    private ArrayList<CreditHistoryPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_name, p_transid, btn_p_amounts, status;
        private TextView btn_p_bank, btn_p_amount, txt_r_id, txt_amount, txt_req_type, txt_remarks, txt_status,txt_bank_details,btn_p_bank_details;
        private LinearLayout ln_history;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (TextView) view.findViewById(R.id.btn_p_bank);
            status = (AutofitTextView) view.findViewById(R.id.status);
            btn_p_amount = (TextView) view.findViewById(R.id.btn_p_amount);
            txt_r_id = (TextView) view.findViewById(R.id.txt_r_id);
            txt_amount = (TextView) view.findViewById(R.id.txt_amount);
            txt_req_type = (TextView) view.findViewById(R.id.txt_req_type);
            txt_remarks = (TextView) view.findViewById(R.id.txt_remarks);
            txt_status = (TextView) view.findViewById(R.id.txt_status);
            txt_bank_details = (TextView) view.findViewById(R.id.txt_bank_details);
            btn_p_bank_details = (TextView) view.findViewById(R.id.btn_p_bank_details);
            ln_history = (LinearLayout) view.findViewById(R.id.ln_history);
        }
    }

    public CreditHistoryAdapter(Context context, RecyclerView recyclerView, ArrayList<CreditHistoryPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_history_adapter, parent, false);
        return new ViewHolder(view);
    }

    String rid, amounts, rtype, remarks, status,bankTxnId;

    // CreditHistoryPozo(String requestId,  String amount, String requesttype, String agentid, String remark, String status)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mValues.get(position).getHeader() == 0) {
            rid = mValues.get(position).getRequestId();
            amounts = mValues.get(position).getAmount();
            rtype = mValues.get(position).getrequesttype();
            remarks = mValues.get(position).getRemark();
            status = mValues.get(position).getStatus();
            bankTxnId = mValues.get(position).getBankTxnId();
        }
        if (mValues.get(position).getHeader() == 0) {
            holder.ln_history.setVisibility(View.GONE);
        } else {
            holder.ln_history.setVisibility(View.VISIBLE);
        }
        holder.txt_r_id.setText(rid);
        holder.txt_amount.setText(amounts);
        holder.txt_req_type.setText(rtype);
        holder.txt_remarks.setText(remarks);
        holder.txt_status.setText(status);
        holder.txt_bank_details.setText(bankTxnId);
        holder.btn_p_amounts.setText(mValues.get(position).getrequesttype());
        holder.btn_name.setText(mValues.get(position).getRequestId());
        holder.p_transid.setText(format(mValues.get(position).getAgentid()));
        holder.btn_p_bank.setText(mValues.get(position).getRemark());
        holder.status.setText(mValues.get(position).getStatus());
        holder.btn_p_amount.setText(mValues.get(position).getAmount());
        holder.btn_p_bank_details.setText(mValues.get(position).getBankTxnId());
        //   }
       /* holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mValues.get(position).getStatus().equalsIgnoreCase("PENDING")){
                  //  Toast.makeText(context,"Are you sure to deactivate",Toast.LENGTH_LONG).show();
                }
            }
        });*/

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





