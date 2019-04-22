package pwj.study.drag

import android.support.v7.widget.RecyclerView

interface OnUnDragListener {
    abstract fun onUnDrag(recyclerView: RecyclerView): Boolean
}