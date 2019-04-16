package com.sun.chat_04.ui.discovery

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.model.UserDistanceWrapper
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.frienddetail.FriendDetailFragment
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_discovery.imageEmptyDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.imageSearchFilter
import kotlinx.android.synthetic.main.fragment_discovery.progressLoading
import kotlinx.android.synthetic.main.fragment_discovery.recyclerDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.searchDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.swipeRefreshDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.textNotifyResult

class DiscoveryFragment : Fragment(), DiscoveryContract.View, SearchView.OnQueryTextListener, OnRefreshListener,
    OnClickListener {

    private lateinit var presenter: DiscoveryContract.Presenter
    private lateinit var adapter: DiscoveryAdapter
    private val searchFilterDialog by lazy { SearchFilterDialog() }
    private lateinit var user: User
    private val userDistanceWrappers = ArrayList<UserDistanceWrapper>()
    private var distance = Constants.MIN_DISTANCE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initComponents()
        initPresenter()
        suggestUsersAroundHere()
    }

    override fun onFindUsersSuccess(userDistanceWrappers: List<UserDistanceWrapper>) {
        if (::adapter.isInitialized) {
            adapter.refreshUsers(userDistanceWrappers)
        }
    }

    override fun onGetUserInfoSuccess(user: User) {
        this.user = user
        findUserAroundHere()
    }

    override fun onFindUserFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.user_not_found))
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            findUsersByName(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when {
            !newText.isNullOrEmpty() -> findUsersByName(newText)
            else -> suggestUsersAroundHere()
        }
        return true
    }

    override fun onRefresh() {
        when {
            !searchDiscovery.query.isNullOrEmpty() -> findUsersByName(searchDiscovery.query.toString())
            else -> findUserAroundHere()
        }
    }

    override fun showProgress() {
        progressLoading?.let {
            progressLoading.visibility = View.VISIBLE
        }
    }

    override fun hideProgress() {
        progressLoading?.let {
            progressLoading.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_DIALOG && resultCode == RESULT_OK && data != null) {
            val dataFilter = data.extras?.getInt(Constants.BUNDLE_FILTER_DIALOG)
            dataFilter ?: return
            when (dataFilter) {
                Constants.CANCEL -> {
                    searchFilterDialog.dismiss()
                }
                else -> {
                    this.distance = dataFilter
                    findUserAroundHere()
                    searchFilterDialog.dismiss()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageSearchFilter -> showDiaLogSearchFilter()
        }
    }

    private fun showDiaLogSearchFilter() {
        searchFilterDialog.setTargetFragment(this, Constants.REQUEST_CODE_DIALOG)
        searchFilterDialog.show(activity?.supportFragmentManager, "")
    }

    private fun initComponents() {
        searchDiscovery.setIconifiedByDefault(false)
        searchDiscovery.setOnQueryTextListener(this)
        swipeRefreshDiscovery.setOnRefreshListener(this)
        imageSearchFilter.setOnClickListener(this)
        displayUsers()
    }

    private fun initPresenter() {
        presenter =
            DiscoveryPresenter(
                this,
                UserRepository(
                    UserRemoteDataSource(
                        Global.firebaseAuth,
                        Global.firebaseDatabase,
                        Global.firebaseStorage
                    )
                )
            )
    }

    private fun displayUsers() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.dp_8)
        adapter = DiscoveryAdapter(userDistanceWrappers) { user -> userClickListener(user) }
        recyclerDiscovery.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerDiscovery.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        if (::adapter.isInitialized) {
            recyclerDiscovery.adapter = adapter
        }
    }

    private fun suggestUsersAroundHere() {
        context?.apply {
            if (Global.checkGrantedPermission(this, Constants.INDEX_PERMISSION_ACCESS_COARSE_LOCATION) &&
                Global.checkGrantedPermission(this, Constants.INDEX_PERMISSION_ACCESS_FINE_LOCATION)
            ) {
                if (::presenter.isInitialized) {
                    presenter.getUserInfo()
                }
            } else {
                activity?.let { activity ->
                    ActivityCompat.requestPermissions(activity, Global.PERMISSIONS, Constants.REQUEST_PERMISSION_CODE)
                }
            }
        }
    }

    private fun findUserAroundHere() {
        if (::presenter.isInitialized) {
            presenter.findUserAroundHere(user, this.distance)
        }
    }

    private fun findUsersByName(query: String) {
        if (::presenter.isInitialized) {
            presenter.findUserByName(query)
        }
    }

    private fun userClickListener(user: User) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.parentLayout, FriendDetailFragment.newInstance(user))
            ?.addToBackStack("")
            ?.commit()
    }

    override fun hideSwipeRefreshDiscovery() {
        swipeRefreshDiscovery?.let {
            it.isRefreshing = false
        }
    }

    override fun showTitleSuggestFriends() {
        textNotifyResult?.let {
            val title = "${resources.getString(R.string.discovery_title_suggest)} ${this.distance}" +
                    " ${resources.getString(R.string.dialog_unit_distance)}"
            textNotifyResult.text = title
        }
    }

    override fun showTitleFindFriendsByName() {
        textNotifyResult?.let {
            textNotifyResult.text = resources.getString(R.string.result_search)
        }
    }

    override fun showImageEmpty() {
        imageEmptyDiscovery?.let {
            it.visibility = View.VISIBLE
        }
    }

    override fun hideImageEmpty() {
        imageEmptyDiscovery?.let {
            it.visibility = View.GONE
        }
    }
}
