package com.sun.chat_04.ui.discovery

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.frienddetail.FriendDetailFragment
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_discovery.progressLoading
import kotlinx.android.synthetic.main.fragment_discovery.recyclerDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.searchDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.swipeRefreshDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.textNotifyResult

class DiscoveryFragment : Fragment(), DiscoveryContract.View, SearchView.OnQueryTextListener, OnRefreshListener {

    private lateinit var presenter: DiscoveryContract.Presenter
    private var users = ArrayList<User>()
    private lateinit var adapter: DiscoveryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initComponents()
        initPresenter()
        findUsersAroundHere()
    }

    override fun onFindUsersSuccess(users: List<User>) {
        if (::adapter.isInitialized) {
            adapter.refreshUsers(users)
        }
    }

    override fun onGetUserInfoSuccess(user: User) {
        if (::presenter.isInitialized) {
            presenter.findUserAroundHere(user)
        }
    }

    override fun onFindUserFailure(exception: Exception?) {
        Global.showMessage(context, resources.getString(R.string.user_not_found))
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            findUsersbyName(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when {
            !newText.isNullOrEmpty() -> findUsersbyName(newText)
            else -> findUsersAroundHere()
        }
        return true
    }

    override fun onRefresh() {
        when {
            !searchDiscovery.query.isNullOrEmpty() -> findUsersbyName(searchDiscovery.query.toString())
            else -> findUsersAroundHere()
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

    private fun initComponents() {
        searchDiscovery.setIconifiedByDefault(false)
        searchDiscovery.setOnQueryTextListener(this)
        swipeRefreshDiscovery.setOnRefreshListener(this)
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
        adapter = DiscoveryAdapter(users) { user -> userClickListener(user) }
        recyclerDiscovery.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerDiscovery.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        if (::adapter.isInitialized) {
            recyclerDiscovery.adapter = adapter
        }
    }

    private fun findUsersAroundHere() {
        checkPermissions()
    }

    private fun findUsersbyName(query: String) {
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

    private fun checkPermissions() {
        context?.apply {
            if (Global.checkGrantedPermission(this, Constants.INDEX_PERMISSION_ACCESS_COARSE_LOCATION) &&
                Global.checkGrantedPermission(this, Constants.INDEX_PERMISSION_ACCESS_FINE_LOCATION)) {
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

    override fun hideSwipeRefreshDiscovery() {
        swipeRefreshDiscovery?.let {
            it.isRefreshing = false
        }
    }

    override fun showTitleSuggestFriends() {
        textNotifyResult?.let {
            textNotifyResult.text = resources.getString(R.string.discovery_title_suggest)
        }
    }

    override fun showTitleFindFriendsByName() {
        textNotifyResult?.let {
            textNotifyResult.text = resources.getString(R.string.result_search)
        }
    }
}
