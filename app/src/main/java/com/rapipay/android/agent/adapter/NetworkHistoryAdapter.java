package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.rapipay.android.agent.Model.NetworkHistoryPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class NetworkHistoryAdapter extends ArrayAdapter<NetworkHistoryPozo> {

    private ArrayList<NetworkHistoryPozo> mValues;
    private Context context;

    public static class ViewHolder {
        public AutofitTextView btn_p_bank, btn_name, p_transid, btn_p_amounts,sysRemarks;
        LinearLayout mainlay;
    }

    public NetworkHistoryAdapter(ArrayList<NetworkHistoryPozo> items, Context context) {
        super(context, R.layout.network_history_lits_layout, items);
        mValues = items;
        this.context = context;
    }
    @Override
    public int getCount() {
        return mValues.size();
    }
    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.network_history_lits_layout, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView) view.findViewById(R.id.btn_p_bank);
            viewHolder.sysRemarks = (AutofitTextView)view.findViewById(R.id.sysRemarks);
            viewHolder.mainlay = (LinearLayout)view.findViewById(R.id.mainlay);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (mValues.get(position).getRequestType().contains("Recieved From"))
            viewHolder.mainlay.setBackgroundColor(context.getResources().getColor(R.color.receivedfrom));
        else if (mValues.get(position).getRequestType().contains("Reversed BY"))
            viewHolder.mainlay.setBackgroundColor(context.getResources().getColor(R.color.reverseby));
        else if (mValues.get(position).getRequestType().contains("Given To"))
            viewHolder.mainlay.setBackgroundColor(context.getResources().getColor(R.color.givento));
        else if (mValues.get(position).getRequestType().contains("Reversed From"))
            viewHolder.mainlay.setBackgroundColor(context.getResources().getColor(R.color.reversefrom));
        viewHolder.btn_p_amounts.setText(mValues.get(position).getCreatedOn());
        viewHolder.btn_name.setText(mValues.get(position).getAgentID());
        viewHolder.p_transid.setText(mValues.get(position).getRequestAmount());
        viewHolder.btn_p_bank.setText(mValues.get(position).getCreditID());
        viewHolder.sysRemarks.setText(mValues.get(position).getSysRemarks());
        return view;
    }

    protected String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}





