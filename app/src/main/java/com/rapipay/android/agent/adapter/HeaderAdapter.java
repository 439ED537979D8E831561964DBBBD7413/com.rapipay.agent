package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.R;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.ViewHolder> {

    private ArrayList<HeaderePozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AutofitTextView input_name, btn_name;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            input_name = (AutofitTextView) view.findViewById(R.id.input_name);
            btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
        }
    }

    public HeaderAdapter(Context context, RecyclerView recyclerView, ArrayList<HeaderePozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_top_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.input_name.setText(mValues.get(position).getHeaderValue());
        if (mValues.get(position).getHeaderData().matches(".*\\d+.*") && !mValues.get(position).getHeaderValue().equalsIgnoreCase("Parent Mobile") && !mValues.get(position).getHeaderValue().equalsIgnoreCase("TOP_PARENT")  && !mValues.get(position).getHeaderValue().equalsIgnoreCase("Agent Name"))
            holder.btn_name.setText(format(mValues.get(position).getHeaderData()));
        else
            holder.btn_name.setText(mValues.get(position).getHeaderData());
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




