package com.ljb.mvp.kotlin.fragment

import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.ljb.mvp.kotlin.R
import com.ljb.mvp.kotlin.adapter.rv.FollowersAdapter
import com.ljb.mvp.kotlin.adapter.rv.FollowersDecoration
import com.ljb.mvp.kotlin.common.fragment.BaseMvpFragment
import com.ljb.mvp.kotlin.contract.FollowersContract
import com.ljb.mvp.kotlin.domain.Follower
import com.ljb.mvp.kotlin.presenter.FollowersPresenter
import com.ljb.mvp.kotlin.widget.PageStateLayout
import com.ljb.mvp.kotlin.widget.PageStateLayout.PageState
import com.ljb.mvp.kotlin.widget.loadmore.LoadMoreRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.layout_recycler_view.*

/**
 * Created by L on 2017/7/19.
 */
class FollowersFragment : BaseMvpFragment<FollowersContract.IPresenter>(), FollowersContract.IView,
        PageStateLayout.PageStateCallBack,
        LoadMoreRecyclerAdapter.LoadMoreListener {

    private val mAdapter by lazy { FollowersAdapter(activity!!, mutableListOf()) }

    override fun getLayoutId() = R.layout.fragment_followers

    override fun registerPresenter() = FollowersPresenter::class.java

    override fun initView() {
        page_layout.apply {
            setContentView(View.inflate(activity, R.layout.layout_recycler_view, null))
            addCallBack(this@FollowersFragment)
        }
        recycler_view.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(FollowersDecoration())
            adapter = mAdapter
            mAdapter.setOnLoadMoreListener(this@FollowersFragment)
        }
    }

    override fun initData() {
        getPresenter().onRefresh()
    }

    override fun onLoadMore() {
        getPresenter().onLoadMore()
    }

    override fun onErrorClick() {
        page_layout.setPage(PageState.STATE_LOADING)
        getPresenter().onRefresh()
    }

    override fun showPage(data: MutableList<Follower>, page: Int) {
        if (page == 1) {
            if (data.isEmpty()) {
                page_layout.setPage(PageState.STATE_EMPTY)
            } else {
                page_layout.setPage(PageState.STATE_SUCCEED)
                mAdapter.mData.clear()
                mAdapter.mData.addAll(data)
                mAdapter.initLoadStatusForSize(data)
                mAdapter.notifyDataSetChanged()
            }
        } else {
            if (data.isEmpty()) {
                mAdapter.onNotMore()
            } else {
                mAdapter.mData.addAll(data)
                mAdapter.initLoadStatusForSize(data)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun errorPage(t: Throwable, page: Int) {
        if (page == 1) {
            page_layout.setPage(PageState.STATE_ERROR)
        } else {
            mAdapter.onError()
        }
    }
}