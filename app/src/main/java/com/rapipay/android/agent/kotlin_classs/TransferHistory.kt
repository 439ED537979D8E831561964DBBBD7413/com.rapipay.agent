package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.rapipay.android.agent.Model.ChannelHistoryPozo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.ChannelListAdapter
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import me.grantland.widget.AutofitTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class TransferHistory : BaseFragment(), RequestHandler, View.OnClickListener {
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var transactionPozoArrayList: ArrayList<ChannelHistoryPozo>? = null
    var reqFor: String? = null
    var adapters: ChannelListAdapter? = null
    var trans_details: ListView? = null
    protected var date2_text: AutofitTextView? = null
    protected var date1_text: AutofitTextView? = null
    protected var toimage: ImageView? = null
    protected var fromimage: ImageView? = null
    protected var selectedDate: Int = 0
    protected var selectedMonth: Int = 0
    protected var selectedYear: Int = 0
    internal var months: String? = null
    internal var dayss: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.txn_history_layout, container, false);
        reqFor = arguments!!.getString("reqFor")
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.details_Rapi)
            list = BaseCompactActivity.dbRealm.details
        trans_details = v.findViewById<View>(R.id.trans_details) as ListView
        date2_text = v.findViewById<View>(R.id.date2) as AutofitTextView
        date1_text = v.findViewById<View>(R.id.date1) as AutofitTextView
        v.findViewById<View>(R.id.todate).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.date1).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.fromdate).setOnClickListener(fromDateClicked)
        v.findViewById<View>(R.id.date2).setOnClickListener(fromDateClicked)
        toimage = v.findViewById<View>(R.id.toimage) as ImageView
        v.findViewById<View>(R.id.btn_fund).setOnClickListener(this)
        val calendar = Calendar.getInstance()
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH)
        selectedMonth = calendar.get(Calendar.MONTH) + 1
        selectedYear = calendar.get(Calendar.YEAR)
        date2_text!!.setText("$selectedYear-$selectedMonth-$selectedDate")
        date1_text!!.setText("$selectedYear-$selectedMonth-$selectedDate")
        loadUrl()
        toimage!!.setOnClickListener(toDateClicked)
        fromimage = v.findViewById<View>(R.id.fromimage) as ImageView
        fromimage!!.setOnClickListener(fromDateClicked)
        return v
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

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_fund -> if (date2_text!!.getText().toString().isEmpty()) {
                date2_text!!.setError("Please enter mandatory field")
                date2_text!!.requestFocus()
            } else if (date1_text!!.getText().toString().isEmpty()) {
                date1_text!!.setError("Please enter mandatory field")
                date1_text!!.requestFocus()
            } else if (printDifference(mainDate(date2_text!!.getText().toString()), mainDate(date1_text!!.getText().toString()))) {
                trans_details!!.setVisibility(View.VISIBLE)
                loadUrl()
            } else {
                customDialog_Common("Statement can only view from one month")
                trans_details!!.setVisibility(View.GONE)
            }
        }
    }

    fun loadUrl() {
        AsyncPostMethod(WebConfig.CommonReport, getSubAgent().toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute()
    }

    fun getSubAgent(): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "GET_TXN_HISTORY_BY_SERVICE")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", list[0].mobilno)
            jsonObject.put("fromTxnDate", date2_text!!.getText().toString())
            jsonObject.put("toTxnDate", date1_text!!.getText().toString())
            jsonObject.put("fromIndex", 1)
            jsonObject.put("toIndex", 5)
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

    override fun chechStat(`object`: String?) {
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.getString("responseCode").equals("200")) {
                if (`object`.getString("serviceType").equals("GET_TXN_HISTORY_BY_SERVICE")) {
                    if (`object`.has("getTxnHistory")) {
                        insertLastTransDetails(`object`.getJSONArray("getTxnHistory"))
                    }
                }
            } else if (`object`.getString("responseCode").equals("60147", ignoreCase = true)) run {
                Toast.makeText(context, `object`.getString("responseMessage"), Toast.LENGTH_LONG).show()
                setBack_click1(context)
            } else {
                responseMSg(`object`)
                trans_details!!.setVisibility(View.GONE)
            }
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
        adapters = ChannelListAdapter(list, activity)
        trans_details!!.setAdapter(adapters)
    }
}