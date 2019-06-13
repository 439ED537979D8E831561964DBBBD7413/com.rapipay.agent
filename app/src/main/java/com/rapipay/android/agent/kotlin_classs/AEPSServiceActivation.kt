package com.rapipay.android.agent.kotlin_classs

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rapipay.android.agent.R
import com.rapipay.android.agent.utils.BaseFragment

class AEPSServiceActivation : BaseFragment(){
    var tabLayout: TabLayout?=null
    var fragment_credit: ServiceEnable?=null
    var transFragment: ActivationHistory?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rv = inflater.inflate(R.layout.tpin_tab_layout, container, false) as View
        initialize(rv)
        return rv
    }

    private fun initialize(rv: View) {
        tabLayout = rv.findViewById<View>(R.id.bottomNavigation) as TabLayout
        bindWidgetsWithAnEvent()
        setupTabLayout()
    }

    private fun setupTabLayout() {
        fragment_credit = ServiceEnable()
        transFragment = ActivationHistory()
        tabLayout!!.addTab(tabLayout!!.newTab().setText("AEPS Activation"), true)
        tabLayout!!.addTab(tabLayout!!.newTab().setText("AEPS Activation History"))
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
        val fm = childFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }
}