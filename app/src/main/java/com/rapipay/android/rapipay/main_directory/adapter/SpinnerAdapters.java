package com.rapipay.android.rapipay.main_directory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.BankDetailsPozo;

import java.util.ArrayList;

public class SpinnerAdapters extends BaseAdapter {
    Context context;
    ArrayList<BankDetailsPozo> list;
    LayoutInflater inflter;

    public SpinnerAdapters(Context applicationContext, ArrayList<BankDetailsPozo> list) {
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
        names.setText(list.get(i).getBankName());
        return view;
    }
}
