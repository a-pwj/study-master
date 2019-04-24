package pwj.study.headerfooter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pwj.study.R

class HeadFootAdapter(private val mContext: Context,
                      private var mData: ArrayList<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_ITEM = 0
        val TYPE_HEADER = 1
        val TYPE_FOOTER = 2
        val TYPE_REFRESH_FOOTER = 3
    }

    override fun getItemCount(): Int = if (mData.size == 0) 0 else mData.size + 3

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0 is ItemHolder) {
            p0.tvText.text = mData[p1 - 1]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val headerView = LayoutInflater.from(mContext).inflate(R.layout.header_recyclerview, parent, false)
                HeaderHolder(headerView)
            }
            TYPE_FOOTER -> {
                val footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_recyclerview, parent, false)
                FooterHolder(footerView)
            }
            TYPE_REFRESH_FOOTER -> {
                val refreshFooterView = LayoutInflater.from(mContext).inflate(R.layout.default_refresh_footer, parent, false)
                FooterHolder(refreshFooterView)
            }
            else -> {
                val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview, parent, false)
                ItemHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            itemCount - 2 -> TYPE_FOOTER
            itemCount - 1 -> TYPE_REFRESH_FOOTER
            else -> TYPE_ITEM
        }
    }

    fun setData(data: ArrayList<String>) {
        mData = data
        notifyDataSetChanged()
    }

    class HeaderHolder(var mItemView: View) : RecyclerView.ViewHolder(mItemView)
    class FooterHolder(var mItemView: View) : RecyclerView.ViewHolder(mItemView)
    class RefreshFooterHolder(var mItemView: View) : RecyclerView.ViewHolder(mItemView)
    class ItemHolder(var mItemView: View) : RecyclerView.ViewHolder(mItemView) {
        val tvText: TextView = mItemView.findViewById(R.id.tvText)
    }
}