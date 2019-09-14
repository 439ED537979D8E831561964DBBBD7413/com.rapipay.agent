package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.makeText
import com.rapipay.android.agent.Model.ChannelHistoryPozo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.ChannelListAdapter
import com.rapipay.android.agent.interfaces.CustomInterface
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import me.grantland.widget.AutofitTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class TransactionReports : BaseFragment(), RequestHandler, View.OnClickListener, CustomInterface {
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var transactionPozoArrayList: ArrayList<ChannelHistoryPozo>? = null
    var reqFor: String? = null
    var adapters: ChannelListAdapter? = null
    var trans_details: ListView? = null
    protected var date2_text: AutofitTextView? = null
    protected var date1_text: AutofitTextView? = null
    protected var toimage: ImageView? = null
    protected var fromimage: ImageView? = null
    private var mLastClickTime = System.currentTimeMillis()
    private val CLICK_TIME_INTERVAL: Long = 1000
    protected var selectedDate: Int = 0
    protected var selectedMonth: Int = 0
    protected var selectedYear: Int = 0
    var first = 1
    var last = 25
    internal var months: String? = null
    internal var dayss: String? = null
    private var isLoading: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.txn_report_layout, container, false);
        reqFor = arguments!!.getString("reqFor")
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.details_Rapi)
            list = BaseCompactActivity.dbRealm.details
        init(view)
        return view;
    }

    fun init(v: View) {
        trans_details = v.findViewById<ListView>(R.id.trans_details)
        val calendar = Calendar.getInstance()
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH) + 1
        selectedYear = calendar.get(Calendar.YEAR)
        date2_text = v.findViewById<View>(R.id.date2) as AutofitTextView
        date1_text = v.findViewById<View>(R.id.date1) as AutofitTextView
        if (selectedMonth < 11)
            date2_text!!.setText("$selectedYear-" + "0$selectedMonth" + "-$selectedDate")
        else
            date2_text!!.setText("$selectedYear-$selectedMonth-$selectedDate")
        if (selectedMonth < 11)
            date1_text!!.setText("$selectedYear-" + "0$selectedMonth" + "-$selectedDate")
        else
            date1_text!!.setText("$selectedYear-$selectedMonth-$selectedDate")
        v.findViewById<View>(R.id.todate).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.btn_fund).setOnClickListener(this)
        v.findViewById<View>(R.id.date1).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.fromdate).setOnClickListener(fromDateClicked)
        v.findViewById<View>(R.id.date2).setOnClickListener(fromDateClicked)
        toimage = v.findViewById<View>(R.id.toimage) as ImageView
        toimage!!.setOnClickListener(toDateClicked)
        fromimage = v.findViewById<View>(R.id.fromimage) as ImageView
        fromimage!!.setOnClickListener(fromDateClicked)
        loadUrl()
        trans_details!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val now = System.currentTimeMillis()
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return@OnItemClickListener
            }
            mLastClickTime = now
            val pozo = transactionPozoArrayList!!.get(position)
            AsyncPostMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, this@TransactionReports, activity, getString(R.string.responseTimeOut), "RECEIPTREQUEST").execute()

        })
        trans_details!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val lastInScreen = firstVisibleItem + visibleItemCount
                if (totalItemCount != 0 && totalItemCount == last && lastInScreen == totalItemCount && !isLoading) {
                    first = last + 1
                    last += 25
                    AsyncPostMethod(WebConfig.CommonReport, getSubAgent(first, last).toString(), headerData, this@TransactionReports, activity, getString(R.string.responseTimeOut)).execute()
                    isLoading = true
                }
            }
        })
    }

    override fun onPause() {
        trans_details!!.setClickable(true)
        super.onPause()
    }

    override fun okClicked(type: String?, ob: Any?) {
    }

    override fun cancelClicked(type: String?, ob: Any?) {
    }

    fun receipt_request(pozo: ChannelHistoryPozo): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt")
            jsonObject.put("requestType", "DMT_CHANNEL")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("txnRef", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("agentId", list[0].mobilno)
            jsonObject.put("orgTxnRef", pozo.orgTxnid)
            jsonObject.put("sessionRefNo", list[0].aftersessionRefNo)
            jsonObject.put("routeType", pozo.transferType)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    protected var toDateClicked: View.OnClickListener = View.OnClickListener {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.datepickerview)
        dialog.setTitle("")

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker1)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { datePicker, year, month, dayOfMonth ->
            Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth)
            if ((month + 1).toString().length == 1)
                months = "0" + (month + 1).toString()
            else
                months = (month + 1).toString()
            if (dayOfMonth.toString().length == 1)
                dayss = "0$dayOfMonth"
            else
                dayss = dayOfMonth.toString()
            if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                date1_text!!.setText("$year-$months-$dayss")
                dialog.dismiss()
            } else {

                if (selectedDate != dayOfMonth) {
                    date1_text!!.setText("$year-$months-$dayss")
                    dialog.dismiss()
                } else {
                    if (selectedMonth != month) {
                        date1_text!!.setText("$year-$months-$dayss")
                        dialog.dismiss()
                    }
                }
            }
            date1_text!!.setError(null)
            selectedDate = dayOfMonth
            selectedMonth = month
            selectedYear = year
        }
        dialog.show()
    }
    protected var fromDateClicked: View.OnClickListener = View.OnClickListener {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.datepickerview)
        dialog.setTitle("")

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker1)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { datePicker, year, month, dayOfMonth ->
            Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth)
            if ((month + 1).toString().length == 1)
                months = "0" + (month + 1).toString()
            else
                months = (month + 1).toString()


            if (dayOfMonth.toString().length == 1)
                dayss = "0$dayOfMonth"
            else
                dayss = dayOfMonth.toString()
            if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                date2_text!!.setText("$year-$months-$dayss")
                dialog.dismiss()
            } else {

                if (selectedDate != dayOfMonth) {
                    date2_text!!.setText("$year-$months-$dayss")
                    dialog.dismiss()
                } else {
                    if (selectedMonth != month) {
                        date2_text!!.setText("$year-$months-$dayss")
                        dialog.dismiss()
                    }
                }
            }
            date2_text!!.setError(null)
            selectedDate = dayOfMonth
            selectedMonth = month
            selectedYear = year
        }
        dialog.show()
    }


    fun loadUrl() {
        AsyncPostMethod(WebConfig.CommonReport, getSubAgent(first, last).toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute()
    }

    fun getSubAgent(first: Int, last: Int): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "GET_TXN_HISTORY_BY_SERVICE")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("fromTxnDate", date2_text!!.getText().toString())
            jsonObject.put("toTxnDate", date1_text!!.getText().toString())
            jsonObject.put("fromIndex", first)
            jsonObject.put("toIndex", last)
            jsonObject.put("reqFor", reqFor)
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("requestType", "BC_CHANNEL")
            jsonObject.put("sessionRefNo", list.get(0).aftersessionRefNo)
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    /* fun lienHistory(date2_text: String, date1_text: String): JSONObject {
         val jsonObject = JSONObject()
         try {
             jsonObject.put("serviceType", "GET_AGENT_LIEN_DETAILS")
             jsonObject.put("requestType", "REPORT_CHANNEL")
             jsonObject.put("typeMobileWeb", "mobile")
             jsonObject.put("transactionID", ImageUtils.miliSeconds())
             jsonObject.put("nodeAgentId", list[0].mobilno)
             jsonObject.put("agentMobile", list[0].mobilno)
             jsonObject.put("sessionRefNo", list[0].aftersessionRefNo)
             jsonObject.put("txnIP", ImageUtils.ipAddress(activity!!))
             jsonObject.put("fromTxnDate", date2_text)
             jsonObject.put("toTxnDate", date1_text)
             jsonObject.put("checkSum", GenerateChecksum.checkSum(list[0].pinsession, jsonObject.toString()))

         } catch (e: Exception) {
             e.printStackTrace()
         }

         return jsonObject
     }*/

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_fund -> if (date2_text!!.getText().toString().isEmpty()) {
                date2_text!!.setError("Please enter mandatory field")
                date2_text!!.requestFocus()
            } else if (date1_text!!.getText().toString().isEmpty()) {
                date1_text!!.setError("Please enter mandatory field")
                date1_text!!.requestFocus()
            } else if (printDifference(mainDate(date2_text!!.getText().toString()), mainDate(date1_text!!.getText().toString()))) {
                loadUrl()
            } else {
                trans_details!!.setVisibility(View.GONE);
                customDialog_Common("Statement can only view from one month")
            }
        }
    }

    override fun chechStat(`object`: String?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            if (requestCode == 2) {
                dialog.dismiss()
            }
        } else {
            if (dialog != null)
                dialog.dismiss()
        }
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.getString("responseCode").equals("200")) {
                if (`object`.getString("serviceType").equals("GET_TXN_HISTORY_BY_SERVICE")) {
                    if (`object`.has("getTxnHistory")) {
                        insertLastTransDetails(`object`.getJSONArray("getTxnHistory"))
                    }
                } else if (`object`.getString("serviceType").equals("Get_Txn_Recipt", ignoreCase = true)) {
                    if (`object`.has("getTxnReceiptDataList"))
                        try {
                            val array = `object`.getJSONArray("getTxnReceiptDataList")
                            customReceiptNewTransaction("Transaction Receipt", `object`, this@TransactionReports)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            customDialog_Common("KYCLAYOUTS", null, null, "Transaction Receipt", "", "Cannot generate receipt now please try later!", this@TransactionReports)
                        }

                }
            } else if (`object`.getString("responseCode").equals("60147", ignoreCase = true)) run {
                Toast.makeText(context, `object`.getString("responseMessage"), Toast.LENGTH_LONG).show()
                setBack_click1(context)
            } else
                responseMSg(`object`)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertLastTransDetails(array: JSONArray) {
        try {
            transactionPozoArrayList = ArrayList<ChannelHistoryPozo>()
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                transactionPozoArrayList!!.add(ChannelHistoryPozo(`object`.getString("senderName") + " ( " + `object`.getString("mobileNo") + " )", `object`.getString("accountNo") + " ( " + `object`.getString("bankName") + " )", `object`.getString("requestAmt"), `object`.getString("txnStatus"), `object`.getString("txnDateTime"), `object`.getString("serviceProviderTXNID"), `object`.getString("transferType"), `object`.getString("userTxnId"), `object`.getString("serviceType"), `object`.getString("txnDateTime")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (transactionPozoArrayList!!.size != 0)
            initializeTransAdapter(transactionPozoArrayList!!)
    }

    private fun initializeTransAdapter(list: ArrayList<ChannelHistoryPozo>) {
        if (first == 1) {
            trans_details!!.setVisibility(View.VISIBLE);
            adapters = ChannelListAdapter(list, activity)
            trans_details!!.setAdapter(adapters)
        } else {
            adapters!!.addAll(list)
            adapters!!.notifyDataSetChanged()
        }
        isLoading = false
    }
}