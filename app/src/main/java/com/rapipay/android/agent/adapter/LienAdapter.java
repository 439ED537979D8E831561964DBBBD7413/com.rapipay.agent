package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rapipay.android.agent.Model.LienHistoryPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class LienAdapter extends ArrayAdapter<LienHistoryPozo> {

    private ArrayList<LienHistoryPozo> mValues;
    Context mContext;

    private  class ViewHolder {
        public View mView;
        public AutofitTextView lien_reason,agent_mobile,lien_amount,lien_requestid,lien_created,lien_remove_status;
    }

    public LienAdapter(ArrayList<LienHistoryPozo> data, Context context) {
        super(context, R.layout.channel_history_adapter, data);
        this.mValues = data;
        this.mContext=context;

    }

    @Override
    public int getCount() {
        return mValues.size();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.lien_history_adapter, parent, false);
            viewHolder.agent_mobile = (AutofitTextView) view.findViewById(R.id.agent_mobile);
            viewHolder.lien_requestid = (AutofitTextView) view.findViewById(R.id.lien_requestid);
            viewHolder.lien_amount = (AutofitTextView) view.findViewById(R.id.lien_amount);
            viewHolder.lien_reason = (AutofitTextView)view.findViewById(R.id.lien_reason);
            viewHolder.lien_created = (AutofitTextView)view.findViewById(R.id.lien_created);
            viewHolder.lien_remove_status = (AutofitTextView)view.findViewById(R.id.lien_remove_status);
            result=view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        viewHolder.lien_requestid.setText(mValues.get(position).getRequestID());
        viewHolder.agent_mobile.setText(mValues.get(position).getAgentMobile());
        viewHolder.lien_amount.setText(format(mValues.get(position).getLienAmt()));
        viewHolder.lien_reason.setText(mValues.get(position).getLienReason());
        viewHolder.lien_created.setText(mValues.get(position).getCreatedOn());
        viewHolder.lien_remove_status.setText(mValues.get(position).getLienRemovalStatus());
        return view;
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
