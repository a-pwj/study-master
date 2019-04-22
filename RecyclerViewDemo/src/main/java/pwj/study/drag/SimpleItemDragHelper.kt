package pwj.study.drag

import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper


public class SimpleItemDragHelper : ItemTouchHelper.Callback() {

    private var onChannelListener: OnChannelListener? = null
    private var OnUnDragListener: OnUnDragListener? = null

    fun setOnChannelListener(listener: OnChannelListener) {
        onChannelListener = listener
    }

    fun setOnUnDraglListener(listener: OnUnDragListener) {
        OnUnDragListener = listener
    }

    /**
     * 是否处理滑动事件 以及拖拽和滑动的方向
     * 如果是列表类型的RecyclerView的只存在UP和DOWN，
     * 如果是网格类RecyclerView则还应该多有LEFT和RIGHT
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val layoutManager = p0.layoutManager
        val dragFlats = if (layoutManager is StaggeredGridLayoutManager || layoutManager is GridLayoutManager) {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        } else {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        val swipeFlags = 0
//        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        OnUnDragListener?.onUnDrag(p0)
        OnUnDragListener?.let {
            if (it.onUnDrag(p0)) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0)
            } else {
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlats, swipeFlags)
            }
        }
    }

    //当上下移动的时候回调的方法
    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        // 不同Type之间不可移动
        if (p1.itemViewType != p1.itemViewType) {
            return false
        }
        val fromPosition = p1.adapterPosition  //得到当拖拽的viewHolder的Position
        val toPosition = p2.adapterPosition     //拿到当前拖拽到的item的viewHolder
//        if (fromPosition < toPosition) {
//            //交换时，确保数据position不错乱，从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
//            //交换数据源两个数据的位置
//            for (i in fromPosition until toPosition) {
//                Collections.swap(p0.adapter.data, i, i + 1)
//            }
//        } else {
//            for (i in fromPosition downTo toPosition + 1) {
//                Collections.swap(p0.adapter.data, i, i - 1)
//            }
//        }
        p0.adapter?.notifyItemMoved(fromPosition, toPosition)
        onChannelListener?.onItemMove(fromPosition, toPosition)
        return true
    }

    //这个看了一下主要是做左右拖动的回调
    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {

    }

    /**
     * 长按拖拽开关
     *
     * @return 代表是否开启长按拖拽
     */
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    /**
     * 长按选中Item的时候开始调用
     *
     * @param viewHolder
     * @param actionState
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder!!.itemView.setBackgroundColor(Color.GRAY)
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * 手指松开的时候还原
     * @param recyclerView
     * @param viewHolder
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.setBackgroundColor(Color.WHITE)
        onChannelListener?.onFinish()
    }
}