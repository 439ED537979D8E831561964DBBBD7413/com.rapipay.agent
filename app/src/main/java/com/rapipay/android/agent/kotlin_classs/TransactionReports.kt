package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class TransactionReports : BaseFragment(), RequestHandler, View.OnClickListener {
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
    var first = 1
    var last = 25
    internal var months: String? = null
    internal var dayss: String? = null
    private var isLoading: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.txn_report_layout, container, false);
        reqFor = arguments!!.getString("reqFor")
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.details_Rapi)
            list = BaseCompactActivity.db.details
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
        v.findViewById<View>(R.id.todate).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.btn_fund).setOnClickListener(this)
        v.findViewById<View>(R.id.date1).setOnClickListener(toDateClicked)
        v.findViewById<View>(R.id.fromdate).setOnClickListener(fromDateClicked)
        v.findViewById<View>(R.id.date2).setOnClickListener(fromDateClicked)
        toimage = v.findViewById<View>(R.id.toimage) as ImageView
        toimage!!.setOnClickListener(toDateClicked)
//        toimage!!.setColorFilter(resources.getColor(R.color.colorPrimaryDark))
        fromimage = v.findViewById<View>(R.id.fromimage) as ImageView
        fromimage!!.setOnClickListener(fromDateClicked)
//        fromimage!!.setColorFilter(resources.getColor(R.color.colorPrimaryDark))
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

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_fund -> if (date2_text!!.getText().toString().isEmpty()) {
                date2_text!!.setError("Please enter mandatory field")
                date2_text!!.requestFocus()
            } else if (date1_text!!.getText().toString().isEmpty()) {
                date1_text!!.setError("Please enter mandatory field")
                date1_text!!.requestFocus()
            } else if (printDifference(mainDate(date2_text!!.getText().toString()), mainDate(date1_text!!.getText().toString())))
                loadUrl()
            else
                Toast.makeText(activity, "Please select correct date", Toast.LENGTH_SHORT).show()
        }
    }

    override fun chechStat(`object`: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.getString("responseCode").equals("200")) {
                if (`object`.getString("serviceType").equals("GET_TXN_HISTORY_BY_SERVICE")) {
                    if (`object`.has("getTxnHistory")) {
                        insertLastTransDetails(`object`.getJSONArray("getTxnHistory"))
                    }
                }
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
        if (first == 1) {
            adapters = ChannelListAdapter(list, activity)
            trans_details!!.setAdapter(adapters)
        } else {
            adapters!!.addAll(list)
            adapters!!.notifyDataSetChanged()
        }
        isLoading = false
    }
}