package com.rapipay.android.agent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.HeaderAdapter;
import com.rapipay.android.agent.adapter.SimpleStringRecyclerViewAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.main_directory.AEPS_BBPS_RegistrationActivity;
import com.rapipay.android.agent.main_directory.CashOutClass;
import com.rapipay.android.agent.main_directory.ChannelHistoryActivity;
import com.rapipay.android.agent.main_directory.CreditTabPage;
import com.rapipay.android.agent.main_directory.DailyCommissionActivity;
//import com.rapipay.android.agent.main_directory.Fino_AEPS_BBPS_Activity;
import com.rapipay.android.agent.main_directory.FundTransferActivity;
//import com.rapipay.android.agent.main_directory.MICRO_AEPS_Activity;
import com.rapipay.android.agent.main_directory.MPOSRegistration;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.main_directory.NetworkTab;
import com.rapipay.android.agent.main_directory.NetworkTransferActivity;
import com.rapipay.android.agent.main_directory.PMTRemittanceActivity;
import com.rapipay.android.agent.main_directory.PassbookActivity;
import com.rapipay.android.agent.main_directory.PendingRefundActivity;
import com.rapipay.android.agent.main_directory.ReChargeActivity;
import com.rapipay.android.agent.main_directory.RechargeHistory;
import com.rapipay.android.agent.main_directory.RegisterKYCTab;
import com.rapipay.android.agent.main_directory.WalletDetailsActivity;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;

public class DashBoardFragments extends Fragment {
    RecyclerView recycler_view, recycler_view2, recycler_view3, recycler_view4, recycler_view5, recycler_view6,recycler_view7,recycler_view8;
    View rv;

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
        recycler_view3 = (RecyclerView) view.findViewById(R.id.recycler_view3);
        recycler_view4 = (RecyclerView) view.findViewById(R.id.recycler_view4);
        recycler_view5 = (RecyclerView) view.findViewById(R.id.recycler_view5);
        recycler_view6 = (RecyclerView) view.findViewById(R.id.recycler_view6);
        recycler_view7 = (RecyclerView) view.findViewById(R.id.recycler_view7);
        recycler_view8 = (RecyclerView) view.findViewById(R.id.recycler_view8);
        if(BuildConfig.APPTYPE==3){
            view.findViewById(R.id.matm).setVisibility(View.VISIBLE);
            view.findViewById(R.id.aeps).setVisibility(View.VISIBLE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view, ImageUtils.getFirstImageUrl(), "first"));
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view2.setLayoutManager(layoutManager2);
        recycler_view2.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view2, ImageUtils.getSecondImageUrl(), "second"));
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view3.setLayoutManager(layoutManager3);
        recycler_view3.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), recycler_view3, ImageUtils.getThirdImageUrl(), "third"));
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
        recycler_view3.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view3, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), MPOSRegistration.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
                    intent.putExtra("typeput", "AEPS");
                    intent.putExtra("persons", "pending");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
                    intent.putExtra("typeput", "BBPS");
                    intent.putExtra("persons", "pending");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(getActivity(), AEPS_BBPS_RegistrationActivity.class);
                    intent.putExtra("typeput", "MATM");
                    intent.putExtra("persons", "pending");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view6.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view6, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(getActivity(), CashOutClass.class);
                    intent.putExtra("typeput", "CASHOUT");
                    intent.putExtra("serviceType", "MPOS_CASHOUT");
                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
                    intent.putExtra("requestType", "MPOS-CASHOUT");
                    intent.putExtra("reqFor", "MPOS");
                } else if (position == 1) {
                    intent = new Intent(getActivity(), CashOutClass.class);
                    intent.putExtra("typeput", "SALE");
                    intent.putExtra("serviceType", "MPOS_SALE");
                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
                    intent.putExtra("requestType", "MPOS-SALE");
                    intent.putExtra("reqFor", "MPOS");
                } else if (position == 2) {
                    intent = new Intent(getActivity(), CashOutClass.class);
                    intent.putExtra("typeput", "EMI");
                    intent.putExtra("serviceType", "MPOS_EMI");
                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
                    intent.putExtra("requestType", "MPOS-EMI");
                    intent.putExtra("reqFor", "MPOS");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view7.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view7, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = null;
//                if (position == 0) {
//                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
//                    intent.putExtra("typeput", "Cash Withdrawal");
//                    intent.putExtra("serviceType", "MATM_CASHOUT");
//                    intent.putExtra("updateServiceType", "UPDATE_MATM_CASHOUT");
//                    intent.putExtra("requestChannel", "MATM_CHANNEL");
//                    intent.putExtra("requestType", "MATM-BE");
//                    intent.putExtra("reqFor", "MATM");
//                } else if (position == 1) {
//                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
//                    intent.putExtra("typeput", "Balance Enquiry");
//                    intent.putExtra("serviceType", "MATM_BALANCE_ENQ");
//                    intent.putExtra("updateServiceType", "UPDATE_MATM_BALANCE_ENQ");
//                    intent.putExtra("requestChannel", "MATM_CHANNEL");
//                    intent.putExtra("requestType", "MATM-BE");
//                    intent.putExtra("reqFor", "MATM");
//                } else if (position == 2) {
//                    intent = new Intent(getActivity(), Fino_AEPS_BBPS_Activity.class);
//                    intent.putExtra("typeput", "EMI");
//                    intent.putExtra("serviceType", "MPOS_EMI");
//                    intent.putExtra("updateServiceType", "MATM_CASHOUT");
//                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
//                    intent.putExtra("requestType", "MPOS_EMI");
//                    intent.putExtra("reqFor", "MPOS");
//                }
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
//                if (position == 0) {
//                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
//                    intent.putExtra("typeput", "Cash Withdrawal");
//                    intent.putExtra("serviceType", "AEPS_CASHOUT");
//                    intent.putExtra("updateServiceType", "UPDATE_AEPS_CASHOUT");
//                    intent.putExtra("requestChannel", "AEPS_CHANNEL");
//                    intent.putExtra("requestType", "AEPS_CASHOUT");
//                    intent.putExtra("reqFor", "AEPS");
//                } else if (position == 1) {
//                    intent = new Intent(getActivity(), MICRO_AEPS_Activity.class);
//                    intent.putExtra("typeput", "Balance Enquiry");
//                    intent.putExtra("serviceType", "AEPS_BALANCE_ENQ");
//                    intent.putExtra("updateServiceType", "UPDATE_AEPS_BALANCE_ENQ");
//                    intent.putExtra("requestChannel", "AEPS_CHANNEL");
//                    intent.putExtra("requestType", "AEPS-BE");
//                    intent.putExtra("reqFor", "AEPS");
//                } else if (position == 2) {
//                    intent = new Intent(getActivity(), Fino_AEPS_BBPS_Activity.class);
//                    intent.putExtra("typeput", "EMI");
//                    intent.putExtra("serviceType", "MPOS_EMI");
//                    intent.putExtra("requestChannel", "MPOS_CHANNEL");
//                    intent.putExtra("requestType", "MPOS_EMI");
//                    intent.putExtra("reqFor", "MPOS");
//                }
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
                if (position == 1) {
                    Intent intent = new Intent(getActivity(), CreditTabPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), NetworkTab.class);
                    intent.putExtra("CLICKED", "0");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 0) {
                    if(MainActivity.pozoArrayList.size()!=0) {
                        for (int i = 0; i < MainActivity.pozoArrayList.size(); i++) {
                            if (MainActivity.pozoArrayList.get(i).getHeaderID().equalsIgnoreCase("10"))
                                if (MainActivity.pozoArrayList.get(i).getHeaderData().equalsIgnoreCase("Retailer")) {
                                    Toast.makeText(getActivity(), "Not Authorized to create New User!.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(getActivity(), RegisterKYCTab.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("type", "outside");
                                    intent.putExtra("customerType", "A");
                                    intent.putExtra("mobileNo", "");
                                    startActivity(intent);
                                }
                        }
                    }
                } else if (position == 3) {
                    Intent intent = new Intent(getActivity(), PassbookActivity.class);
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
                    Intent intent = new Intent(getActivity(), FundTransferActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), PendingRefundActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 0) {
                    Intent intent = new Intent(getActivity(), WalletDetailsActivity.class);
                    intent.putExtra("mobileNo", "");
                    intent.putExtra("type", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(getActivity(), ChannelHistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(getActivity(), PMTRemittanceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler_view5.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recycler_view5, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String id = MainActivity.pozoArrayList.get(position).getHeaderID();
                if (id.equalsIgnoreCase("3")) {
                    Intent intent = new Intent(getActivity(), NetworkTransferActivity.class);
                    intent.putExtra("CLICKED", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("2")) {
                    Intent intent = new Intent(getActivity(), NetworkTransferActivity.class);
                    intent.putExtra("CLICKED", "2");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("4")) {
                    Intent intent = new Intent(getActivity(), DailyCommissionActivity.class);
                    intent.putExtra("TYPE", "D");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("5")) {
                    Intent intent = new Intent(getActivity(), DailyCommissionActivity.class);
                    intent.putExtra("TYPE", "M");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id.equalsIgnoreCase("6")) {
                    Intent intent = new Intent(getActivity(), DailyCommissionActivity.class);
                    intent.putExtra("TYPE", "U");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        if (MainActivity.pozoArrayList.size() != 0)
            initializeTransAdapter(MainActivity.pozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<HeaderePozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view5.setLayoutManager(layoutManager);
        recycler_view5.setAdapter(new HeaderAdapter(getActivity(), recycler_view5, list));
    }


}
