package pwj.study.headerfooter

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recycler_head_foot.*
import pwj.study.R

private const val ARG_PARAM = "param"

class RecyclerHeadFootFragment : Fragment() {

    private var param: Int? = null
    private var listener: OnFragmentInteractionListener? = null
    private val mData = ArrayList<String>()
    private lateinit var mAdapter: HeadFootAdapter
    private lateinit var mManager: LinearLayoutManager
    private var mLoadMoreCount = 0
    private var mLastVisiblePos = 0

    companion object {
        @JvmStatic
        fun newInstance(type: Int): RecyclerHeadFootFragment {
            val f = RecyclerHeadFootFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM, type)
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_head_foot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            param = it.getInt(ARG_PARAM)
        }
        swipeRefreshLayout.setColorSchemeColors(activity?.resources!!.getColor(R.color.colorAccent))
        swipeRefreshLayout.isRefreshing = true

        mAdapter = HeadFootAdapter(activity!!, mData)//暂时
        mManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = mManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        loadData()
        swipeRefreshLayout.setOnRefreshListener {
            mData.clear()
            loadData()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mLastVisiblePos = mManager.findLastVisibleItemPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d("tag", "newState $newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisiblePos == (mAdapter.itemCount - 1)) {
                    loadMore()
                }
            }
        })
    }

    private fun loadData() {
        delay({
            for (i in 0..10) {
                mData.add("item $i")
            }
            mAdapter.setData(mData)
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }

    private fun loadMore() {
        if (mLoadMoreCount == 2) {
            return
        }

        delay({
            for (i in 0..4) {
                mData.add("more item")
            }
            mAdapter.setData(mData)
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
        mLoadMoreCount++
    }

    private fun delay(action: () -> Unit, delayTime: Long) {
        Handler().postDelayed(action, delayTime)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
