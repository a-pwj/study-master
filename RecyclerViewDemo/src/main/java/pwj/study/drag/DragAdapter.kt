package pwj.study.drag

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import pwj.study.R
import pwj.study.bean.Subject

class DragAdapter(private val data: List<Subject>?, private val context: Context) : RecyclerView.Adapter<DragAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_linear, p0, false))
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tv_title.text = data?.get(p1)?.title
        data?.get(p1)?.img?.let { p0.img.setImageResource(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tv_title: TextView = itemView.findViewById(R.id.tv_title) as TextView
        internal var img: ImageView = itemView.findViewById(R.id.img) as ImageView
        internal var ll_item: LinearLayout = itemView.findViewById(R.id.ll_item) as LinearLayout
        internal var ll_hidden: LinearLayout = itemView.findViewById(R.id.ll_hidden) as LinearLayout
    }
}
