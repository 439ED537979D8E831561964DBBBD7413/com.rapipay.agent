package com.rapipay.android.agent.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.HeaderAdapter;
import com.rapipay.android.agent.adapter.SimpleStringRecyclerViewAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.main_directory.AEPS_BBPS_RegistrationActivity;
import com.rapipay.android.agent.main_directory.CashOutClass;
import com.rapipay.android.agent.main_directory.ChannelHistoryActivity;
import com.rapipay.android.agent.main_directory.CreditTabPage;
import com.rapipay.android.agent.main_directory.CreditTransHistActivity;
import com.rapipay.android.agent.main_directory.Fino_AEPS_BBPS_Activity;
import com.rapipay.android.agent.main_directory.FundTransferActivity;
import com.rapipay.android.agent.main_directory.MICRO_AEPS_Activity;
import com.rapipay.android.agent.main_directory.MPOSRegistration;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.main_directory.MyCommission;
import com.rapipay.android.agent.main_directory.NetworkTab;
import com.rapipay.android.agent.main_directory.NetworkTransHistory;
import com.rapipay.android.agent.main_directory.NetworkTransferActivity;
import com.rapipay.android.agent.main_directory.NewChannelHistoryActivity;
import com.rapipay.android.agent.main_directory.PMTRemittanceActivity;
import com.rapipay.android.agent.main_directory.PassbookActivity;
import com.rapipay.android.agent.main_directory.PendingRefundActivity;
import com.rapipay.android.agent.main_directory.ReChargeActivity;
import com.rapipay.android.agent.main_directory.RechargeHistory;
import com.rapipay.android.agent.main_directory.RegisterKYCTab;
import com.rapipay.android.agent.main_directory.WalletDetailsActivity;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.finopaytech.finosdk.helpers.Utils.hideKeyboard;

public class DashBoardFragments extends Fragment {
    RecyclerView recycler_view, recycler_view2, recycler_view3, recycler_view4, recycler_view5, recycler_view6, recycler_view7, recycler_view8;
    View rv;
    TextView bankdetails;
    protected static final int CONTACT_PICKER_RESULT = 1;
    final protected static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.dashboard_layout, container, false);
        return rv;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setupRecyclerView(rv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(View view) {
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        recycler_view2 = (RecyclerView) view.findViewById(R.id.recycler_view2);
//        recycler_view3 = (RecyclerView) view.findViewById(R.id.recycler_view3);
        recycler_view4 = (RecyclerView) view.findViewById(R.id.recycler_view4);
        recycler_view5 = (RecyclerView) view.findViewById(R.id.recycler_view5);
        recycler_view6 = (RecyclerView) view.findViewById(R.id.recycler_view6);
        recycler_view7 = (RecyclerView) view.findViewById(R.id.recycler_view7);
        recycler_view8 = (RecyclerView) view.findViewById(R.id.recycler_view8);
        bankdetails = (TextView) view.findViewById(R.id.bankdetails);
        bankdetails.setVisibility(View.VISIBLE);
        bankdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankdetails.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.matm).setVisibility(View.VISIBLE);
        view.findViewById(R.id.aeps).setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(layoutManager);
//        recycler_view.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view, ImageUtils.getFirstImageUrl(), "first"));
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view2.setLayoutManager(layoutManager2);
        recycler_view2.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view2, ImageUtils.getSecondImageUrl(), "second"));
//        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        recycler_view3.setLayoutManager(layoutManager3);


        if (MainActivity.regBankDetails != null) {
            String splitReg[] = MainActivity.regBankDetails.split("\\|");
            ArrayList<ImagePozo> imagePozoArrayList = ImageUtils.getFirstImageUrl();
            for (int j = 0; j < imagePozoArrayList.size(); j++) {
                for (int i = 0; i < splitReg.length; i++) {
                    if (imagePozoArrayList.get(j).getImageTagName().equalsIgnoreCase(splitReg[i])) {
                        imagePozoArrayList.remove(j);
                    }
                }
            }
            if (imagePozoArrayList.size() != 0)
                recycler_view.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view, imagePozoArrayList, "first"));
            else {
                view.findViewById(R.id.regss).setVisibility(View.GONE);
                recycler_view.setVisibility(View.GONE);
            }
        } else {
            recycler_view.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view, ImageUtils.getFirstImageUrl(), "first"));
        }

        LinearLayoutManager layoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view4.setLayoutManager(layoutManager4);
        recycler_view4.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view4, ImageUtils.getFourthImageUrl(), "fourth"));
        LinearLayoutManager layoutManager6 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view6.setLayoutManager(layoutManager6);
        recycler_view6.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view6, ImageUtils.getSixthImageUrl(), "fourth"));
        LinearLayoutManager layoutManager7 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view7.setLayoutManager(layoutManager7);
        recycler_view7.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view7, ImageUtils.getSeventhImageUrl(), "seventh"));
        LinearLayoutManager layoutManager8 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view8.setLayoutManager(layoutManager8);
        recycler_view8.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view8, ImageUtils.getEigthImageUrl(), "eigth"));
        //recycler listener
//        recycler_view3.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view3, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                if (position == 0) {
//                    Intent intent = new Intent(getActivity(), MPOSRegistration.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                } else if (position == 1) {
//                    Intent intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
//                    intent.putExtra("typeput", "AEPS");
//                    intent.putExtra("persons", "pending");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                } else if (position == 2) {
//                    Intent intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
//                    intent.putExtra("typeput", "BBPS");
//                    intent.putExtra("persons", "pending");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                } else if (position == 3) {
//                    Intent intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
//                    intent.putExtra("typeput", "MATM");
//                    intent.putExtra("persons", "pending");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
        recycler_view6.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view6, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent intent = null;
                if (position == 0) {
                    customDialog_Ben("MPOS-SALE", "MposCashoutTransfer");
//                    intent = new Intent(getActivity(), CashOutClass.class);
//                    intent.putExtra("typeput", "CASHOUT");
//                    intent.putExtra("serviceType", "MPOS_CASHOUT");
//                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
//                    intent.putExtra("requestType", "MPOS-CASHOUT");
//                    intent.putExtra("reqFor", "MPOS");
                } else if (position == 1) {
                    customDialog_Ben("MPOS-SALE", "MposSaleTransfer");
//                    intent = new Intent(getActivity(), CashOutClass.class);
//                    intent.putExtra("typeput", "SALE");
//                    intent.putExtra("serviceType", "MPOS_SALE");
//                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
//                    intent.putExtra("requestType", "MPOS-SALE");
//                    intent.putExtra("reqFor", "MPOS");
                } else if (position == 2) {
                    customDialog_Ben("MPOS-SALE", "MposEmiTransfer");
//                    intent = new Intent(getActivity(), CashOutClass.class);
//                    intent.putExtra("typeput", "EMI");
//                    intent.putExtra("serviceType", "MPOS_EMI");
//                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
//                    intent.putExtra("requestType", "MPOS-EMI");
//                    intent.putExtra("reqFor", "MPOS");
                }
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view7.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view7, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
                    intent.putExtra("typeput", "Cash Withdrawal");
                    intent.putExtra("serviceType", "MATM_CASHOUT");
                    intent.putExtra("updateServiceType", "UPDATE_MATM_CASHOUT");
                    intent.putExtra("requestChannel", "MATM_CHANNEL");
                    intent.putExtra("requestType", "MATM-CASHOUT");
                    intent.putExtra("reqFor", "MATM");
                } else if (position == 1) {
                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
                    intent.putExtra("typeput", "Balance Enquiry");
                    intent.putExtra("serviceType", "MATM_BALANCE_ENQ");
                    intent.putExtra("updateServiceType", "UPDATE_MATM_BALANCE_ENQ");
                    intent.putExtra("requestChannel", "MATM_CHANNEL");
                    intent.putExtra("requestType", "MATM-BE");
                    intent.putExtra("reqFor", "MATM");
                } else if (position == 2) {
                    intent = new Intent(getActivity(), BankDetails.class);
                    intent.putExtra("typeput", "EMI");
                    intent.putExtra("serviceType", "MPOS_EMI");
                    intent.putExtra("updateServiceType", "MATM_CASHOUT");
                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
                    intent.putExtra("requestType", "MPOS_EMI");
                    intent.putExtra("reqFor", "MPOS");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view8.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view8, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
                    intent.putExtra("typeput", "Cash Withdrawal");
                    intent.putExtra("serviceType", "AEPS_CASHOUT");
                    intent.putExtra("updateServiceType", "UPDATE_AEPS_CASHOUT");
                    intent.putExtra("requestChannel", "AEPS_CHANNEL");
                    intent.putExtra("requestType", "AEPS-CASHOUT");
                    intent.putExtra("reqFor", "AEPS");
                } else if (position == 1) {
                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
                    intent.putExtra("typeput", "Balance Enquiry");
                    intent.putExtra("serviceType", "AEPS_BALANCE_ENQ");
                    intent.putExtra("updateServiceType", "UPDATE_AEPS_BALANCE_ENQ");
                    intent.putExtra("requestChannel", "AEPS_CHANNEL");
                    intent.putExtra("requestType", "AEPS-BE");
                    intent.putExtra("reqFor", "AEPS");
                } else if (position == 2) {
                    intent = new Intent(getActivity(), Fino_AEPS_BBPS_Activity.class);
                    intent.putExtra("typeput", "EMI");
                    intent.putExtra("serviceType", "MPOS_EMI");
                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
                    intent.putExtra("requestType", "MPOS_EMI");
                    intent.putExtra("reqFor", "MPOS");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(getActivity(), CreditTabPage.class);
                } else if (position == 1) {
                    intent = new Intent(getActivity(), NetworkTab.class);
                    intent.putExtra("CLICKED", "0");
//                } else if (position == 0) {
////                    if (MainActivity.pozoArrayList.size() != 0) {
////                        for (int i = 0; i < MainActivity.pozoArrayList.size(); i++) {
////                            if (MainActivity.pozoArrayList.get(i).getHeaderID().equalsIgnoreCase("10"))
//                    if (MainActivity.relailerDetails) {
//                        Toast.makeText(getActivity(), "Not Authorized to create New User!.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Intent intent = new Intent(getActivity(), RegisterKYCTab.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.putExtra("type", "outside");
//                        intent.putExtra("customerType", "A");
//                        intent.putExtra("mobileNo", "");
//                        startActivity(intent);
//                    }
////                        }
////                    }
                } else if (position == 2) {
                    intent = new Intent(getActivity(), PassbookActivity.class);
                    intent.putExtra("TYPE", "");
                } else if (position == 3) {
                    intent = new Intent(getActivity(), MPOSRegistration.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 4) {
                    intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
                    intent.putExtra("typeput", "AEPS");
                    intent.putExtra("persons", "pending");
                } else if (position == 5) {
                    intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
                    intent.putExtra("typeput", "BBPS");
                    intent.putExtra("persons", "pending");
                } else if (position == 6) {
                    intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
                    intent.putExtra("typeput", "MATM");
                    intent.putExtra("persons", "pending");
                }
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view4.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view4, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 1) {
                    customDialog_Ben("BC FUND TRANSFER", "FundTransfer");
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), PendingRefundActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 0) {
                    customDialog_Ben("RAPIPAY WALLET FUND TRANSFER", "WALLETTransfer");
                } else if (position == 3) {
                    Intent intent = new Intent(getActivity(), ChannelHistoryActivity.class);
                    intent.putExtra("TYPE", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 4) {
                    customDialog_Ben("INDO-NEPAL Remittance", "PMTTransfer");
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view5.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view5, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String id = MainActivity.pozoArrayList.get(position).getHeaderValue().trim();
                if (id.equalsIgnoreCase("My Network")) {
                    Intent intent = new Intent(getActivity(), NetworkTransferActivity.class);
                    intent.putExtra("CLICKED", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("Network Balance")) {
                    Intent intent = new Intent(getActivity(), NetworkTransferActivity.class);
                    intent.putExtra("CLICKED", "2");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("MY Commission")) {
                    Intent intent = new Intent(getActivity(), MyCommission.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("Network Transfer")) {
                    Intent intent = new Intent(getActivity(), NetworkTransHistory.class);
                    intent.putExtra("TYPE", "NODE");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("Usage")) {
                    Intent intent = new Intent(getActivity(), NewChannelHistoryActivity.class);
                    intent.putExtra("TYPE", "NODE");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("Available-Balance")) {
                    Intent intent = new Intent(getActivity(), PassbookActivity.class);
                    intent.putExtra("TYPE", "NODE");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("Loading")) {
                    Intent intent = new Intent(getActivity(), CreditTransHistActivity.class);
                    intent.putExtra("TYPE", "NODE");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("Create New")) {
                    Intent intent = new Intent(getActivity(), RegisterKYCTab.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("type", "outside");
                    intent.putExtra("customerType", "A");
                    intent.putExtra("mobileNo", "");
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view2.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view2, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), ReChargeActivity.class);
                    intent.putExtra("OPERATOR", "MOBILE");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), ReChargeActivity.class);
                    intent.putExtra("OPERATOR", "DTH");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), RechargeHistory.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Under Process", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        if (MainActivity.pozoArrayList.size() != 0) {
            ArrayList<HeaderePozo> list = MainActivity.pozoArrayList;
            initializeTransAdapter(list);
        }
    }

    private void initializeTransAdapter(ArrayList<HeaderePozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view5.setLayoutManager(layoutManager);
        recycler_view5.setAdapter(new HeaderAdapter(getActivity(), recycler_view5, list));
        if (MainActivity.bankdetails == null || MainActivity.bankdetails.isEmpty()) {
            bankdetails.setVisibility(View.GONE);
        } else {
            bankdetails.setText(MainActivity.bankdetails);
            bankdetails.setVisibility(View.VISIBLE);
        }
    }

    AlertDialog alertDialog;
    EditText input_number;
    private void customDialog_Ben(String title, final String type) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dash_pop_layout, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        input_number = (EditText) alertLayout.findViewById(R.id.input_number);
        final EditText input_amount = (EditText) alertLayout.findViewById(R.id.input_amount);
        final ImageView btn_search = (ImageView)alertLayout.findViewById(R.id.btn_search);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        if (type.equalsIgnoreCase("MposCashoutTransfer") || type.equalsIgnoreCase("MposSaleTransfer") || type.equalsIgnoreCase("MposEmiTransfer"))
            input_amount.setVisibility(View.VISIBLE);
        dialog.setCancelable(false);
        dialog.setView(alertLayout);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadIMEI();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("FundTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), FundTransferActivity.class);
                        intent.putExtra("MOBILENO", input_number.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("WALLETTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), WalletDetailsActivity.class);
                        intent.putExtra("mobileNo", input_number.getText().toString());
                        intent.putExtra("type", "");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                }else if (type.equalsIgnoreCase("RefundTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), PendingRefundActivity.class);
                        intent.putExtra("mobileNo", input_number.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("PMTTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), PMTRemittanceActivity.class);
                        intent.putExtra("MOBILENO", input_number.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("MposCashoutTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else if (input_amount.getText().toString().isEmpty()) {
                        input_amount.setError("Please enter the correct number");
                        input_amount.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), CashOutClass.class);
                        intent.putExtra("mobileNo", input_number.getText().toString());
                        intent.putExtra("amount", input_amount.getText().toString());
                        intent.putExtra("typeput", "CASHOUT");
                        intent.putExtra("serviceType", "MPOS_CASHOUT");
                        intent.putExtra("requestChannel", "MPOS_CHANNEL");
                        intent.putExtra("requestType", "MPOS-CASHOUT");
                        intent.putExtra("reqFor", "MPOS");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("MposSaleTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else if (input_amount.getText().toString().isEmpty()) {
                        input_amount.setError("Please enter the correct number");
                        input_amount.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), CashOutClass.class);
                        intent.putExtra("mobileNo", input_number.getText().toString());
                        intent.putExtra("amount", input_amount.getText().toString());
                        intent.putExtra("typeput", "SALE");
                        intent.putExtra("serviceType", "MPOS_SALE");
                        intent.putExtra("requestChannel", "MPOS_CHANNEL");
                        intent.putExtra("requestType", "MPOS-SALE");
                        intent.putExtra("reqFor", "MPOS");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("MposEmiTransfer")) {
                    if (input_number.length() != 10) {
                        input_number.setError("Please enter the correct number");
                        input_number.requestFocus();
                    } else if (input_amount.getText().toString().isEmpty()) {
                        input_amount.setError("Please enter the correct number");
                        input_amount.requestFocus();
                    } else {
                        hideKeyboard(getActivity());
                        Intent intent = new Intent(getActivity(), CashOutClass.class);
                        intent.putExtra("mobileNo", input_number.getText().toString());
                        intent.putExtra("amount", input_amount.getText().toString());
                        intent.putExtra("typeput", "EMI");
                        intent.putExtra("serviceType", "MPOS_EMI");
                        intent.putExtra("requestChannel", "MPOS_CHANNEL");
                        intent.putExtra("requestType", "MPOS-EMI");
                        intent.putExtra("reqFor", "MPOS");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }
    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_CONTACTS)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermissionGrantedStuffs();
            } else {
                alertPerm(getString(R.string.permissions_not_granted_read_phone_state), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadIMEI();
                    }
                });
            }
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                contactRead(data, input_number);
            }
        }
    }

    protected void contactRead(Intent data, TextView input_number) {
        if (data != null) {
            Uri contactData = data.getData();
            Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber.contains(" "))
                    phoneNumber = phoneNumber.replaceAll(" ", "");
                if (phoneNumber.startsWith("+")) {
                    if (phoneNumber.length() == 13) {
                        String str_getMOBILE = phoneNumber.substring(3);
                        input_number.setText(str_getMOBILE);
                    } else if (phoneNumber.length() == 11) {
                        String str_getMOBILE = phoneNumber.substring(1);
                        input_number.setText(str_getMOBILE);
                    } else if (phoneNumber.length() == 10) {
                        input_number.setText(phoneNumber);
                    }
                } else if (phoneNumber.startsWith("0")) {
                    if (phoneNumber.length() == 11) {
                        String str_getMOBILE = phoneNumber.substring(1);
                        input_number.setText(str_getMOBILE);
                    }
                } else if (phoneNumber.length() == 10) {
                    input_number.setText(phoneNumber);
                } else {
                    Toast.makeText(getActivity(), "Please select valid number.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
