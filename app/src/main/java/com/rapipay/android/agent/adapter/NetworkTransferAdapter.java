package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.R;

public class NetworkTransferAdapter extends ArrayAdapter<NetworkTransferPozo>{

    private ArrayList<NetworkTransferPozo> mValues;
    private Context context;

    public class ViewHolder {
        public AutofitTextView btn_p_bank, btn_name, p_transid, btn_p_amounts, agent_category;

//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
//            btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
//            p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
//            btn_p_bank = (AutofitTextView) view.findViewById(R.id.btn_p_bank);
//            agent_category = (AutofitTextView) view.findViewById(R.id.agent_category);
//        }
    }

    public NetworkTransferAdapter(Context context, ArrayList<NetworkTransferPozo> items) {
        super(context, R.layout.network_list_adapter, items);
        this.mValues = items;
        this.context = context;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        NetworkTransferPozo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.network_list_adapter, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView) view.findViewById(R.id.btn_p_bank);
            viewHolder.agent_category = (AutofitTextView) view.findViewById(R.id.agent_category);
            result=view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        viewHolder.btn_p_amounts.setText(mValues.get(position).getMobileNo());
        viewHolder.btn_name.setText(mValues.get(position).getCompanyName());
        viewHolder.p_transid.setText(mValues.get(position).getAgentName());
        if (mValues.get(position).getAgentBalance().matches(".*\\d+.*")) {
            viewHolder.btn_p_bank.setText(format(mValues.get(position).getAgentBalance()));
        } else
            viewHolder.btn_p_bank.setText(mValues.get(position).getAgentBalance());
        viewHolder.agent_category.setText(mValues.get(position).getAgentCategory());
        return view;
    }


    @Override
    public int getCount() {
        return mValues.size();
    }
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.network_list_adapter, parent, false);
//        return new ViewHolder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, final int position) {
//           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
//            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
//                layoutParams.height = 200;
//            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
//                layoutParams.height = 600;
//            } else {
//                layoutParams.height = 800;
//            }*/
//        holder.btn_p_amounts.setText(mValues.get(position).getMobileNo());
//        holder.btn_name.setText(mValues.get(position).getCompanyName());
//        holder.p_transid.setText(mValues.get(position).getAgentName());
//        if (mValues.get(position).getAgentBalance().matches(".*\\d+.*")) {
//            holder.btn_p_bank.setText(format(mValues.get(position).getAgentBalance()));
//        } else
//            holder.btn_p_bank.setText(mValues.get(position).getAgentBalance());
//        holder.agent_category.setText(mValues.get(position).getAgentCategory());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mValues.size();
//    }

    private String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}



