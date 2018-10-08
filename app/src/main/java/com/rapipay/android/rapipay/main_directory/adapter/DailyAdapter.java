package com.rapipay.android.rapipay.main_directory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.CommissionPozo;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private ArrayList<CommissionPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_name,p_transid,btn_p_amounts,status;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            status = (AutofitTextView)view.findViewById(R.id.status);
        }
    }

    public DailyAdapter(Context context, RecyclerView recyclerView, ArrayList<CommissionPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commission_adapter_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_p_amounts.setText(mValues.get(position).getTxnDate());
        holder.btn_name.setText(mValues.get(position).getServiceName());
        holder.p_transid.setText(mValues.get(position).getTxncrdrAmount());
        holder.status.setText(mValues.get(position).getTransactionStatus());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}






