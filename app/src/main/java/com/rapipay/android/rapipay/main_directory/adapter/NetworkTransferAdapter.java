package com.rapipay.android.rapipay.main_directory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.NetworkTransferPozo;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class NetworkTransferAdapter extends RecyclerView.Adapter<NetworkTransferAdapter.ViewHolder> {

    private ArrayList<NetworkTransferPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;
    private String type;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,agent_category;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            agent_category = (AutofitTextView)view.findViewById(R.id.agent_category);
        }
    }

    public NetworkTransferAdapter(Context context, RecyclerView recyclerView, ArrayList<NetworkTransferPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.network_list_adapter, parent, false);
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
        holder.btn_p_amounts.setText(mValues.get(position).getMobileNo());
        holder.btn_name.setText(mValues.get(position).getCompanyName());
        holder.p_transid.setText(mValues.get(position).getAgentName());
        holder.btn_p_bank.setText(mValues.get(position).getAgentBalance());
        holder.agent_category.setText(mValues.get(position).getAgentCategory());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}



