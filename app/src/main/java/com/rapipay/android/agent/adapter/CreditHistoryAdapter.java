package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.CreditHistoryPozo;
import com.rapipay.android.agent.R;

public class CreditHistoryAdapter extends RecyclerView.Adapter<CreditHistoryAdapter.ViewHolder> {

    private ArrayList<CreditHistoryPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,status;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            status = (AutofitTextView)view.findViewById(R.id.status);
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


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/
        holder.btn_p_amounts.setText(mValues.get(position).getBankName());
        holder.btn_name.setText(mValues.get(position).getRequestId());
        holder.p_transid.setText(mValues.get(position).getAmount());
        holder.btn_p_bank.setText(mValues.get(position).getRemark());
        holder.status.setText(mValues.get(position).getStatus());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}





