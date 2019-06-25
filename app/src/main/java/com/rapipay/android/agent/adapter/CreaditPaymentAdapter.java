package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rapipay.android.agent.Model.CreaditPaymentModePozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

public class CreaditPaymentAdapter extends BaseAdapter {
    Context context;
    String id;
    ArrayList<CreaditPaymentModePozo> list;
    LayoutInflater inflter;

    public CreaditPaymentAdapter(Context applicationContext, ArrayList<CreaditPaymentModePozo> list) {
        this.context = applicationContext;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public ArrayList<CreaditPaymentModePozo> getList() {
        return list;
    }

    public void setList(ArrayList<CreaditPaymentModePozo> list1) {
        this.list = list1;
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
        names.setText(list.get(i).getPaymentMode());

        return view;
    }
}
