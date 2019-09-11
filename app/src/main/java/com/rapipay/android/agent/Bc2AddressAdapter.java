package com.rapipay.android.agent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.bc2addresspojo.PostOffice;

import java.util.ArrayList;

public class Bc2AddressAdapter extends BaseAdapter {
    Context context;
    ArrayList<PostOffice> list;
    LayoutInflater inflter;

    public Bc2AddressAdapter(Context applicationContext, ArrayList<PostOffice> list) {
        this.context = applicationContext;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_view, null);
        TextView names = (TextView) view.findViewById(R.id.spiner_text);
        names.setText(list.get(i).getPincode());
        return view;
    }
}
