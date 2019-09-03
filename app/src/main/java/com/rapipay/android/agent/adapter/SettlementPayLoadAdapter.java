package com.rapipay.android.agent.adapter;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rapipay.android.agent.Model.SettlementPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;

import java.util.ArrayList;

public class SettlementPayLoadAdapter extends RecyclerView.Adapter<SettlementPayLoadAdapter.ViewHolder> {

    private ArrayList<SettlementPozo> mValues;
    private RecyclerView mRecyclerView;
    private Activity context;
    CustomInterface anInterface;
    protected AlertDialog.Builder dialog;
    protected AlertDialog alertDialog;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Button account_button;
        private TextView account_name, bank_account_name, account_number, account_status;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            account_name = (TextView) view.findViewById(R.id.account_name);
            bank_account_name = (TextView) view.findViewById(R.id.bank_account_name);
            account_number = (TextView) view.findViewById(R.id.account_number);
            account_status = (TextView) view.findViewById(R.id.account_status);
            account_button = (Button) view.findViewById(R.id.account_button);
        }
    }

    public SettlementPayLoadAdapter(Activity context, RecyclerView recyclerView, ArrayList<SettlementPozo> items, CustomInterface anInterface) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
        this.anInterface = anInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settlement_payload_ladp_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.account_name.setText(mValues.get(position).getAgentName());
        holder.bank_account_name.setText(mValues.get(position).getAgentBankName());
        holder.account_number.setText(mValues.get(position).getAgentAccountNO());
        if (mValues.get(position).getAccountStatus().equalsIgnoreCase("0"))
            holder.account_status.setText("Pending");
        else if (mValues.get(position).getAccountStatus().equalsIgnoreCase("1"))
            holder.account_status.setText("Approved");
        else if (mValues.get(position).getAccountStatus().equalsIgnoreCase("2"))
            holder.account_status.setText("DENIED");
        else if (mValues.get(position).getAccountStatus().equalsIgnoreCase("3"))
            holder.account_status.setText("De-activate");
        holder.account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.account_button.setEnabled(false);
                customDeleteAccount("DELETEACCOUNT",mValues.get(position),"Are you sure?","Do You Want To De-Activate Account ?",holder.account_button);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    protected void customDeleteAccount(final String type, final Object ob, String msg, String output, final Button account_button) {
        dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("DELETEACCOUNT")) {
            btn_cancel.setText("Cancel");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Yes, De-Activate Account!");
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("DELETEACCOUNT")) {
                customView(alertLayout, output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("DELETEACCOUNT"))
                    anInterface.okClicked(type,ob);
                alertDialog.dismiss();
                account_button.setEnabled(true);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                account_button.setEnabled(true);
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                account_button.setEnabled(true);
            }
        });
        alertDialog = dialog.show();
    }

    protected void customView(View alertLayout, String output) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(output);
        otpView.setVisibility(View.VISIBLE);
        dialog.setView(alertLayout);
    }
}



