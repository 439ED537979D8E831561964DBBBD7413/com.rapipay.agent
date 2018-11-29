package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pnsol.sdk.vo.AcquirerEmiDetailsVO;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

public class AcquirerBanksListAdapter extends BaseAdapter {
    Context context;
    ArrayList<AcquirerEmiDetailsVO> list;
    LayoutInflater inflter;

    public AcquirerBanksListAdapter(Context applicationContext, ArrayList<AcquirerEmiDetailsVO> list) {
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
    public View getView(int i, View view, ViewGroup viewGroup){
        view = inflter.inflate(R.layout.spinner_view,null);
        TextView names = (TextView)view.findViewById(R.id.spiner_text);
        names.setText(list.get(i).getAcquirerName());
        return view;
    }
}
