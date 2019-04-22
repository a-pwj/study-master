package pwj.study.drag

import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent

abstract class OnRecyclerItemClickListener(recyclerView: RecyclerView) : RecyclerView.OnItemTouchListener {

    private val mRecyclerView: RecyclerView = recyclerView
    private val mGestureDetectorCompat: GestureDetectorCompat =
            GestureDetectorCompat(mRecyclerView.context, ItemTouchHelperGestureListener())


    override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) {
        mGestureDetectorCompat.onTouchEvent(p1)
    }

    override fun onInterceptTouchEvent(p0: RecyclerView, p1: MotionEvent): Boolean {
        mGestureDetectorCompat.onTouchEvent(p1)
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {
    }

    abstract fun onItemClick(viewHolder: RecyclerView.ViewHolder)
    abstract fun onItemLongClick(viewHolder: RecyclerView.ViewHolder)


    inner class ItemTouchHelperGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            val childViewUnder = mRecyclerView.findChildViewUnder(e!!.x, e.y)
            childViewUnder?.let {
                val holder = mRecyclerView.getChildViewHolder(childViewUnder)
                onItemClick(holder)
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            val childViewUnder = mRecyclerView.findChildViewUnder(e!!.x, e.y)
            childViewUnder?.let {
                val holder = mRecyclerView.getChildViewHolder(childViewUnder)
                onItemLongClick(holder)
            }
        }
    }
}