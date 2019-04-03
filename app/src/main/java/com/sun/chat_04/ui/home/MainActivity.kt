package com.sun.chat_04.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.TabLayout.OnTabSelectedListener
import android.support.design.widget.TabLayout.Tab
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.sun.chat_04.R
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.discovery.DiscoveryFragment
import com.sun.chat_04.ui.friend.FriendFragment
import com.sun.chat_04.ui.profile.ProfileFragment
import com.sun.chat_04.ui.request.FriendRequestFragment
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.activity_main.tablayoutHome
import kotlinx.android.synthetic.main.activity_main.viewpagerHome

class MainActivity : AppCompatActivity(), HomeContract.View, OnTabSelectedListener {

    private val TAB_ICON = arrayOf(
        R.drawable.ic_message, R.drawable.ic_message_req,
        R.drawable.ic_search, R.drawable.ic_profile
    )
    private val TAB_ICON_SELECTED = arrayOf(
        R.drawable.ic_message_selected, R.drawable.ic_message_req_selected,
        R.drawable.ic_seach_selected, R.drawable.ic_profile_selected
    )

    private var homePresenter: HomeContract.Presenter? = null
    private var homeAdapter: HomeAdapter? = null
    private var locationManager: LocationManager? = null
    private var fragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initPresenter()
        checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    upgradeLocationUser()
                }
                return
            }
        }
    }

    private fun checkPermissions() {
        if (!Global.checkGrantedPermission(this, Constants.INDEX_1) ||
            !Global.checkGrantedPermission(this, Constants.INDEX_2)) {
            ActivityCompat.requestPermissions(this, Global.PERMISSIONS, Constants.REQUEST_PERMISSION_CODE)
        } else {
            upgradeLocationUser()
        }
    }

    override fun onTabReselected(tab: Tab?) {
    }

    override fun onTabUnselected(tab: Tab) {
        tablayoutHome.getTabAt(tab.position)?.setIcon(TAB_ICON[tab.position])
    }

    override fun onTabSelected(tab: Tab) {
        tablayoutHome.getTabAt(tab.position)?.setIcon(TAB_ICON_SELECTED[tab.position])
    }

    override fun onUpgradeLocationSuccessful() {
        // Upgrade success
    }

    override fun onUpgradeLocationFailure() {
        // Failure
    }

    private fun initComponents() {
        fragments.add(FriendFragment())
        fragments.add(FriendRequestFragment())
        fragments.add(DiscoveryFragment())
        fragments.add(ProfileFragment())
        homeAdapter = HomeAdapter(fragments, supportFragmentManager)
        homeAdapter?.let { viewpagerHome.adapter = homeAdapter }
        tablayoutHome.setupWithViewPager(viewpagerHome)
        setIconTab()
        tablayoutHome.addOnTabSelectedListener(this)
    }

    private fun setIconTab() {
        tablayoutHome.getTabAt(TAB_MESSAGE)?.setIcon(TAB_ICON_SELECTED[TAB_MESSAGE])
        tablayoutHome.getTabAt(TAB_REQUEST_FRIEND)?.setIcon(TAB_ICON[TAB_REQUEST_FRIEND])
        tablayoutHome.getTabAt(TAB_DISCOVERY)?.setIcon(TAB_ICON[TAB_DISCOVERY])
        tablayoutHome.getTabAt(TAB_PROFILE)?.setIcon(TAB_ICON[TAB_PROFILE])
    }

    private fun initPresenter() {
        homePresenter = HomePresenter(
            this,
            UserRepository(UserRemoteDataSource(Global.firebaseAuth, Global.firebaseDatabase))
        )
    }

    @SuppressLint("MissingPermission")
    private fun upgradeLocationUser() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            Constants.MIN_TIME_UPGRADE_LOCATION,
            Constants.MIN_DISTANCE_UPGRADE_LOCATION,
            object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location != null) {
                        homePresenter?.upgradeLocationUser(location)
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                    message(resources.getString(R.string.requireGPS))
                }
            })
    }

    private fun Context.message(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    companion object {
        const val TAB_MESSAGE = 0
        const val TAB_REQUEST_FRIEND = 1
        const val TAB_DISCOVERY = 2
        const val TAB_PROFILE = 3
    }
}
