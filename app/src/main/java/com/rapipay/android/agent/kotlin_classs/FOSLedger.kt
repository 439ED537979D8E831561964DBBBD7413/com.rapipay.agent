package com.rapipay.android.agent.kotlin_classs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.rapipay.android.agent.Model.PassbookPozo
import com.rapipay.android.agent.R
import com.rapipay.android.agent.adapter.PassbookAdapter
import com.rapipay.android.agent.interfaces.RequestHandler
import com.rapipay.android.agent.utils.*
import me.grantland.widget.AutofitTextView
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class FOSLedger : BaseFragment(), RequestHandler, View.OnClickListener {
    internal var months: String? = null
    internal var dayss: String? = null
    var agentID: String? = null
    var nodeAgentID: String? = null
    var sessionRefNo: String? = null
    var sessionKey: String? = null
    var first = 1
    var last = 25
    private var isLoading: Boolean = false
    var transactionPozoArrayList: ArrayList<PassbookPozo>? = null
    var trans_details: ListView? = null
    protected var headerData = WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD
    var adapters: PassbookAdapter? = null
    protected var selectedDate: Int = 0
    protected var selectedMonth: Int = 0
    protected var selectedYear: Int = 0
    protected var date2_text: AutofitTextView? = null
    protected var date1_text: AutofitTextView? = null
    protected var toimage: ImageView? = null
    protected var fromimage: ImageView? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.network_transfer_layout)
//        var intent = intent as Intent
//
//        init()
//        loadUrl()
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fos_layout_ledger, container, false) as View
        init(view)
//        loadUrl()
        return view
    }

    fun init(v: View) {
        nodeAgentID = arguments!!.getString("nodeAgentID")
        agentID = arguments!!.getString("AgentID")
        sessionRefNo = arguments!!.getString("sessionRefNo")
        sessionKey = arguments!!.getString("sessionKey")
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
                    AsyncPostMethod(WebConfig.CommonReport, getTransgerRequest(first, last).toString(), headerData, this@FOSLedger, activity, getString(R.string.responseTimeOut)).execute()
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
        AsyncPostMethod(WebConfig.CommonReport, getTransgerRequest(first, last).toString(), headerData, this, activity, getString(R.string.responseTimeOut)).execute()
    }

    override fun chechStat(`object`: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun chechStatus(`object`: JSONObject?) {
        try {
            if (`object`!!.has("responseCode") && `object`.getString("responseCode").equals("200", true)) {
                if (`object`.getString("serviceType").equals("SUB_AGENT_TXN_HISTORY", true)) {
                    if (`object`.has("subAgentTxnList"))
                        insertLastTransDetails(`object`.getJSONArray("subAgentTxnList"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatss(amount: String): String? {
        try {
            val formatter = NumberFormat.getInstance(Locale("en", "IN"))
            return formatter.format(formatter.parse(amount))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun insertLastTransDetails(array: JSONArray) {
        if (array.length() != 1)
            transactionPozoArrayList = ArrayList<PassbookPozo>()
        try {
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                transactionPozoArrayList!!.add(PassbookPozo(`object`.getString("payeeMobNo"), `object`.getString("txnServiceType"), formatss(`object`.getString("txnAmount")) + " / " + formatss(`object`.getString("crDrAmmount")) + " " + `object`.getString("crDrType"), `object`.getString("txnDate"), formatss(`object`.getString("openingBal")) + " / " + formatss(`object`.getString("closingBal")), `object`.getString("txnStatus")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (transactionPozoArrayList!!.size != 0)
            initializeTransAdapter(transactionPozoArrayList!!)
    }

    private fun initializeTransAdapter(list: ArrayList<PassbookPozo>) {
        if (first == 1) {
            adapters = PassbookAdapter(activity, list)
            trans_details!!.setAdapter(adapters)
        } else {
            adapters!!.addAll(list)
            adapters!!.notifyDataSetChanged()
        }
        isLoading = false
    }

    fun getTransgerRequest(first: Int, last: Int): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject.put("serviceType", "SUB_AGENT_TXN_HISTORY")
            jsonObject.put("requestType", "BC_CHANNEL")
            jsonObject.put("typeMobileWeb", "mobile")
            jsonObject.put("transactionID", ImageUtils.miliSeconds())
            jsonObject.put("nodeAgentId", nodeAgentID)
            jsonObject.put("sessionRefNo", sessionRefNo)
            jsonObject.put("agentId", agentID)
            jsonObject.put("fromIndex", first)
            jsonObject.put("toIndex", last)
            jsonObject.put("fromDate", date2_text!!.getText().toString())
            jsonObject.put("toDate", date1_text!!.getText().toString())
            jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {

            R.id.btn_fund ->
                if (date2_text!!.getText().toString().isEmpty()) {
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
}