package com.rapipay.android.agent.kotlin_classs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.widget.TextView
import com.rapipay.android.agent.R
import com.rapipay.android.agent.fragments.BC7TransferFragment
import com.rapipay.android.agent.utils.BaseCompactActivity

class BC7Tab : BaseCompactActivity(), View.OnClickListener{

    var tabLayout: TabLayout?=null
    var fragment_credit: BC7TransferFragment?=null
    var transFragment: TransferHistory?=null
    var toolbar_title: TextView?=null
    var bundle:Bundle?=null
    var datedata:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bc7_tab)
        bundle = Bundle()
        datedata = intent.extras!!.getString("reqFor")
        bundle!!.putString("reqFor", datedata)
        initialize()
    }

    private fun initialize() {
        tabLayout = findViewById<View>(R.id.bottomNavigation) as TabLayout
        toolbar_title = findViewById<View>(R.id.toolbar_title) as TextView
        toolbar_title!!.setText(list.get(0).getAgentName())
        bindWidgetsWithAnEvent()
        setupTabLayout()
    }

    private fun setupTabLayout() {
        fragment_credit = BC7TransferFragment()
        transFragment = TransferHistory()
        transFragment!!.setArguments(bundle!!)
        tabLayout!!.addTab(tabLayout!!.newTab().setText("BC Transfer"), true)
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Transaction History"))
    }

    private fun bindWidgetsWithAnEvent() {
        tabLayout!!.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setCurrentTabFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setCurrentTabFragment(tabPosition: Int) {
        when (tabPosition) {
            0 -> replaceFragment(fragment_credit!!)
            1 -> replaceFragment(transFragment!!)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fm = getSupportFragmentManager()
        val ft = fm.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    override fun onBackPressed() {
        setBack_click(this)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_click -> {
                setBack_click(this)
                finish()
            }
        }
    }
}
