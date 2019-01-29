package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileAdapter extends ArrayAdapter<HeaderePozo> {

    private ArrayList<HeaderePozo> mValues;
    HashMap<String,String> headerePozoArrayList;
    Context mContext;

    private class ViewHolder {
        public TextView headervalue, headerdata;
        public ImageView headeredit;
        public EditText headerdataedit;
    }

    public ProfileAdapter(ArrayList<HeaderePozo> data, Context context) {
        super(context, R.layout.profile_layout, data);
        this.mValues = data;
        this.mContext = context;
        headerePozoArrayList = new HashMap<String,String>();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.profile_layout, parent, false);
            viewHolder.headervalue = (TextView) view.findViewById(R.id.headervalue);
            viewHolder.headerdata = (TextView) view.findViewById(R.id.headerdata);
            viewHolder.headeredit = (ImageView) view.findViewById(R.id.headeredit);
            viewHolder.headerdataedit = (EditText) view.findViewById(R.id.headerdataedit);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.headervalue.setText(mValues.get(position).getHeaderData());
        viewHolder.headerdata.setText(mValues.get(position).getHeaderValue());
        viewHolder.headerdataedit.setText(mValues.get(position).getHeaderValue());
        if (mValues.get(position).getHeaderID().equalsIgnoreCase("Y"))
            viewHolder.headeredit.setVisibility(View.VISIBLE);
        else
            viewHolder.headeredit.setVisibility(View.GONE);
        viewHolder.headeredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.headerdataedit.setVisibility(View.VISIBLE);
                viewHolder.headerdata.setVisibility(View.GONE);
            }
        });
        viewHolder.headerdataedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    public HashMap<String,String> getList(){
        return headerePozoArrayList;
    }
}

