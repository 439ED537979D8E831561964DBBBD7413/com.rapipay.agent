package com.rapipay.android.agent.kotlin_classs

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.rapipay.android.agent.BuildConfig
import com.rapipay.android.agent.R
import com.rapipay.android.agent.interfaces.CustomInterface
import com.rapipay.android.agent.main_directory.LoginScreenActivity
import com.rapipay.android.agent.utils.BaseCompactActivity

class FOSMainActivity :BaseCompactActivity(),NavigationView.OnNavigationItemSelectedListener,CustomInterface {
    var agentID: String? = null
    var nodeAgentID: String? = null
    var sessionRefNo: String? = null
    var sessionKey: String? = null
    var drawer:DrawerLayout?=null
    var toolbar:Toolbar?=null
    var navigationView:NavigationView?=null
    var back_click:ImageView?=null
    companion object{
        var bankde:TextView?=null
        fun getCount(): String {
            return bankde!!.text.toString()
        }
        fun setCount(s:String) {
            bankde!!.text= s
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fosactivity_main)
        var intent = intent as Intent
        nodeAgentID = intent.getStringExtra("nodeAgentID")
        agentID = intent.getStringExtra("AgentID")
        sessionRefNo = intent.getStringExtra("sessionRefNo")
        sessionKey = intent.getStringExtra("sessionKey")
        initialise()
    }

    fun initialise(){
        bankde = findViewById<View>(R.id.bankde) as TextView
        bankde!!.visibility=View.VISIBLE
        back_click = findViewById<View>(R.id.back_click) as ImageView
        if (BuildConfig.APPTYPE == 1 || BuildConfig.APPTYPE == 3)
            back_click!!.setImageDrawable(resources.getDrawable(R.drawable.new_test))
        if (BuildConfig.APPTYPE == 2)
            back_click!!.setImageDrawable(resources.getDrawable(R.drawable.rapipay_parter))
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.setDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.hamburger)
        toggle.syncState()
        toggle.toolbarNavigationClickListener = View.OnClickListener { drawer!!.openDrawer(GravityCompat.START) }
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
        val verionName = navigationView!!.findViewById<TextView>(R.id.btn_sing_in)
        val contactus = navigationView!!.findViewById<TextView>(R.id.contactus)
        if (BuildConfig.APPTYPE == 1 || BuildConfig.APPTYPE == 3)
            contactus.visibility = View.VISIBLE
        try {
            verionName.text = "Version - " + packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        itemSelection(0)
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        }
        finish()
//        customDialog_Common("KYCLAYOUT", null, null, "Rapipay", null, "Are you sure you want to exit ?", this@FOSMainActivity)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment : Fragment?=null
        if(item.itemId==R.id.nav_home){
            fragment = FOSTransfer()
            var bundle = Bundle()
            bundle.putString("nodeAgentID",nodeAgentID);
            bundle.putString("AgentID",agentID);
            bundle.putString("sessionKey",sessionKey);
            bundle.putString("sessionRefNo",sessionRefNo);
            fragment.setArguments(bundle)
        }else if(item.itemId==R.id.nav_ledger){
            fragment = FOSLedger()
            var bundle = Bundle()
            bundle.putString("nodeAgentID",nodeAgentID);
            bundle.putString("AgentID",agentID);
            bundle.putString("sessionKey",sessionKey);
            bundle.putString("sessionRefNo",sessionRefNo);
            fragment.setArguments(bundle)
        }
        if(fragment!=null)
            replaceFragment(fragment)
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    fun itemSelection(i:Int){
        onNavigationItemSelected(navigationView!!.menu.getItem(0))
    }
    fun replaceFragment(fragment:Fragment){
        var fm = supportFragmentManager.beginTransaction() as FragmentTransaction
        fm.replace(R.id.frame_container,fragment)
        fm.addToBackStack(null)
        fm.commit()
    }


    override fun okClicked(type: String?, ob: Any?) {
        super.onBackPressed()
        val intent = Intent(this, LoginScreenActivity::class.java)
        startActivity(intent)
        finish()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelClicked(type: String?, ob: Any?) {
        dialog.dismiss()
    }

}