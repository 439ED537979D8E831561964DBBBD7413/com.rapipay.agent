package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mValues;
    private ArrayList<String> arraylist = null;

    Context mContext;
    private  class ViewHolder {
        public AutofitTextView recycler_text;
    }

    public CustomSpinnerAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.receipt_list, data);
        this.mValues = data;
        this.mContext=context;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(mValues);

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.receipt_list, parent, false);
            viewHolder.recycler_text = (AutofitTextView) view.findViewById(R.id.recycler_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.recycler_text.setText(mValues.get(position));
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(arraylist);
        }
        else
        {
            for (String wp : arraylist)
            {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mValues.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

