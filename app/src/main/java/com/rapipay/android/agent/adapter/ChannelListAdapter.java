package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.ChannelHistoryPozo;
import com.rapipay.android.agent.R;

public class ChannelListAdapter extends ArrayAdapter<ChannelHistoryPozo> {

    private ArrayList<ChannelHistoryPozo> mValues;
    Context mContext;

    // View lookup cache
    private  class ViewHolder {
        public View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,btn_status;
    }

    public ChannelListAdapter(ArrayList<ChannelHistoryPozo> data, Context context) {
        super(context, R.layout.channel_history_adapter, data);
        this.mValues = data;
        this.mContext=context;

    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        ChannelHistoryPozo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.channel_history_adapter, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            viewHolder.btn_status = (AutofitTextView)view.findViewById(R.id.btn_status);
            result=view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        viewHolder.btn_p_amounts.setText("Bene Account ID : "+mValues.get(position).getAccount());
        viewHolder.btn_name.setText("Sender Name : " + mValues.get(position).getName());
        viewHolder.p_transid.setText("Transaction Amt : " + mValues.get(position).getAmount());
        viewHolder.btn_p_bank.setText("RRN : "+mValues.get(position).getServiceProviderTXNID());
        viewHolder.btn_status.setText("Status : "+mValues.get(position).getTxnId());
        // Return the completed view to render on screen
        return view;
    }
}