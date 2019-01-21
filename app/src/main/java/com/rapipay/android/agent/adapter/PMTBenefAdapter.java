package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Model.PMTBenefPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

public class PMTBenefAdapter extends RecyclerView.Adapter<PMTBenefAdapter.ViewHolder> {

private ArrayList<PMTBenefPozo> mValues;
private Context context;

public static class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView btn_name,btn_account,btn_bank,btn_accountnum,btn_beneid,btn_bankdetails;

    public ViewHolder(View view) {
        super(view);
        mView = view;
        btn_name = (TextView) view.findViewById(R.id.btn_name);
        btn_account = (TextView) view.findViewById(R.id.btn_account);
        btn_bank = (TextView) view.findViewById(R.id.btn_bank);
        btn_accountnum = (TextView) view.findViewById(R.id.btn_accountnum);
        btn_beneid = (TextView) view.findViewById(R.id.btn_beneid);
        btn_bankdetails = (TextView) view.findViewById(R.id.btn_bankdetails);
    }
}

    public PMTBenefAdapter(Context context, ArrayList<PMTBenefPozo> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pmt_benef_adap_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_bank.setText(mValues.get(position).getRelation_With_Sender());
        holder.btn_name.setText(mValues.get(position).getReceiver_Mobile());
        holder.btn_account.setText(mValues.get(position).getReceiver_Details());
        holder.btn_accountnum.setText(mValues.get(position).getAccount_Number());
        holder.btn_beneid.setText(mValues.get(position).getPmt_Bene_Id());
        holder.btn_bankdetails.setText(mValues.get(position).getBank_Details());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}


