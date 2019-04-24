package pwj.study.drag.swipe

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.TextView
import pwj.study.R
import pwj.study.drag.DragAdapter

class SwipeSimpleRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private val TAG = "SwipeSimpleRecyclerView"

    private val mTouchSlop: Int
    private val maxLength: Int
    private val mScroller: Scroller
    private var xDown: Int = 0
    private var yDown: Int = 0
    private var xMove: Int = 0
    private var yMove: Int = 0

    /**
     * 当前选中的item索引（这个很重要）
     */
    private var curSelectPosition: Int = 0
    /**
     * 是否是第一次touch
     */
    private var isFirst = true

    private var mCurItemLayout: LinearLayout? = null
    private var mLastItemLayout: LinearLayout? = null
    private var mLlHidden: LinearLayout? = null//隐藏部分
    private val mItemContent: TextView? = null
    private var mItemDelete: LinearLayout? = null
    /**
     * 隐藏部分长度
     */
    private var mHiddenWidth: Int = 0
    /**
     * 记录连续移动的长度
     */
    private var mMoveWidth = 0

    init {
        //滑动到最小距离
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        //滑动的最大距离
        maxLength = (context.resources.displayMetrics.density * 180 + 0.5f).toInt()
        //初始化Scroller
        mScroller = Scroller(context, LinearInterpolator(context, null))
    }

    /**
     * 删除的监听事件
     */
    private var mRightListener: OnRightClickListener? = null

    fun setRightClickListener(listener: OnRightClickListener) {
        this.mRightListener = listener
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x.toInt()
        val y = e.y.toInt()
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = x
                yDown = y
                //计算选中哪个Item
                val firstPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val itemRect = Rect()

                val count: Int = childCount
                for (i in 0 until count) {
                    val child = getChildAt(i)
                    if (child.visibility == View.VISIBLE) {
                        child.getHitRect(itemRect)//获取View可点击矩形左、上、右、下边界相对于父View的左顶点的距离（偏移量）
                        if (itemRect.contains(x, y)) {
                            curSelectPosition = firstPosition + i//循环直至获取到点击的child
                            break
                        }
                    }
                }

                //重置判断
                if (isFirst) {//第一次时，不用重置上一次的Item
                    isFirst = false
                } else {
                    //屏幕再次接收到点击时，恢复上一次Item的状态
                    if (mLastItemLayout != null && mMoveWidth > 0) {
                        //将Item右移，恢复原位
                        scrollRight(mLastItemLayout!!, (0 - mMoveWidth))
                        //清空变量
                        mHiddenWidth = 0
                        mMoveWidth = 0
                    }
                }

                //取到当前选中的Item，赋给mCurItemLayout，以便对其进行左移
                val item = getChildAt(curSelectPosition - firstPosition)
                item?.let {
                    //获取当前选中的item
                    val viewHolder = getChildViewHolder(item) as DragAdapter.ViewHolder
                    mCurItemLayout = viewHolder.ll_item
                    //找到具体元素（这与实际业务相关了~~）
                    mLlHidden = mCurItemLayout?.findViewById(R.id.ll_hidden)
                    mItemDelete = mCurItemLayout?.findViewById(R.id.ll_hidden)
                    mItemDelete?.setOnClickListener {
                        mRightListener?.onRightClick(curSelectPosition, "")
                    }
                    //这里将删除按钮的宽度设为可以移动的距离
                    mHiddenWidth = mLlHidden!!.width
                }
            }
            MotionEvent.ACTION_MOVE -> {
                xMove = x
                yMove = y

                val dx = xMove - xDown//为负时：手指向左滑动；为正时：手指向右滑动。这与Android的屏幕坐标定义有关
                val dy = yMove - yDown

                if (dx < 0 && Math.abs(dx) > mTouchSlop && Math.abs(dy) < mTouchSlop) {
                    var newScrollX = Math.abs(dx)
                    if (mMoveWidth >= mHiddenWidth) {//超过了，不能再移动了
                        newScrollX = 0
                    } else if (mMoveWidth + newScrollX > mHiddenWidth) {//这次要超了，
                        newScrollX = mHiddenWidth - mMoveWidth
                    }

                    //左滑，每次滑动手指移动的距离
                    scrollLeft(mCurItemLayout!!, newScrollX)
                    //对移动的距离叠加
                    mMoveWidth += newScrollX
                } else if (dx > 0) {//右滑
                    //执行右滑，这里没有做跟随，瞬间恢复
                    scrollRight(mCurItemLayout!!, 0 - mMoveWidth)
                    mMoveWidth = 0
                }
            }
            MotionEvent.ACTION_UP -> {
                val scrollX = mCurItemLayout?.scrollX ?: 0

                if (mHiddenWidth > mMoveWidth) {
                    val toX = (mHiddenWidth - mMoveWidth)
                    mMoveWidth = if (scrollX > mHiddenWidth / 2) {//超过一半长度时松开，则自动滑到左侧
                        scrollLeft(mCurItemLayout!!, toX)
                        mHiddenWidth
                    } else {//不到一半时松开，则恢复原状
                        scrollRight(mCurItemLayout!!, 0 - mMoveWidth)
                        0
                    }
                }
                mLastItemLayout = mCurItemLayout
            }
        }
        return super.onTouchEvent(e)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.e(TAG, "computeScroll getCurrX ->" + mScroller.currX)
            mCurItemLayout?.scrollBy(mScroller.currX, 0)
            invalidate()
        }
    }

    /**
     * 向左滑动
     */
    private fun scrollLeft(item: View, scorllX: Int) {
        Log.e(TAG, " scroll left -> $scorllX")
        item.scrollBy(scorllX, 0)
    }

    /**
     * 向右滑动
     */
    private fun scrollRight(item: View, scorllX: Int) {
        Log.e(TAG, " scroll right -> $scorllX")
        item.scrollBy(scorllX, 0)
    }

    interface OnRightClickListener {
        fun onRightClick(position: Int, id: String)
    }

}