package pwj.study.recyclerview

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.OverScroller
import android.widget.TextView
import pwj.study.R
import pwj.study.recyclerview.horizontal.Horizontal
import pwj.study.recyclerview.horizontal.LeftHorizontal
import pwj.study.recyclerview.horizontal.RightHorizontal

class SwipeMenuLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr), Controller {

    val DEFAULT_SCROLLER_DURATION = 200

    private var mLeftViewId = 0
    private var mContentViewId = 0
    private var mRightViewId = 0

    private var mOpenPercent = 0.5f
    private var mScrollerDuration = DEFAULT_SCROLLER_DURATION

    private var mScaledTouchSlop: Int = 0
    private var mScaledMinimumFlingVelocity: Int = 0
    private var mScaledMaximumFlingVelocity: Int = 0
    private var mLastX: Int = 0
    private var mLastY: Int = 0
    private var mDownX: Int = 0
    private var mDownY: Int = 0

    private var mContentView: View? = null
    private var mSwipeLeftHorizontal: LeftHorizontal? = null
    private var mSwipeRightHorizontal: RightHorizontal? = null
    private var mSwipeCurrentHorizontal: Horizontal? = null
    private var shouldResetSwipe: Boolean = false
    private var mDragging: Boolean = false
    private var swipeEnable = true
    private var mScroller: OverScroller? = null
    private var mVelocityTracker: VelocityTracker? = null

    init {
        isClickable = true

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout)
        mLeftViewId = typedArray.getResourceId(R.styleable.SwipeMenuLayout_leftViewId, mLeftViewId)
        mContentViewId = typedArray.getResourceId(R.styleable.SwipeMenuLayout_contentViewId, mContentViewId)
        mRightViewId = typedArray.getResourceId(R.styleable.SwipeMenuLayout_rightViewId, mRightViewId)
        typedArray.recycle()

        val configuration = ViewConfiguration.get(context)
        mScaledTouchSlop = configuration.scaledTouchSlop
        mScaledMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        mScaledMaximumFlingVelocity = configuration.scaledMaximumFlingVelocity

        mScroller = OverScroller(context)
    }

    //    当我们的XML布局被加载完后，就会回调onFinshInfalte这个方法，在这个方法中我们可以初始化控件和数据。
    override fun onFinishInflate() {
        super.onFinishInflate()
        if (mLeftViewId != 0 && mSwipeLeftHorizontal == null) {
            val view: View = findViewById(mLeftViewId)
            mSwipeLeftHorizontal = LeftHorizontal(view)
        }
        if (mRightViewId != 0 && mSwipeRightHorizontal == null) {
            val view = findViewById<View>(mRightViewId)
            mSwipeRightHorizontal = RightHorizontal(view)
        }
        if (mContentViewId != 0 && mContentView == null) {
            mContentView = findViewById(mContentViewId)
        } else {
            val errorView = TextView(context)
            errorView.isClickable = true
            errorView.gravity = Gravity.CENTER
            errorView.textSize = 16f
            errorView.text = "You may not have set the ContentView."
            mContentView = errorView
            addView(mContentView)
        }
    }

    /**
     * Set whether open swipe. Default is true.
     *
     * @param swipeEnable true open, otherwise false.
     */
    fun setSwipeEnable(swipeEnable: Boolean) {
        this.swipeEnable = swipeEnable
    }

    /**
     * Open the swipe function of the Item?
     *
     * @return open is true, otherwise is false.
     */
    private fun isSwipeEnable(): Boolean {
        return swipeEnable
    }

    /**
     * Set open percentage.
     *
     * @param openPercent such as 0.5F.
     */
    fun setOpenPercent(openPercent: Float) {
        this.mOpenPercent = openPercent
    }

    /**
     * Get open percentage.
     *
     * @return such as 0.5F.
     */
    fun getOpenPercent(): Float {
        return mOpenPercent
    }

    /**
     * The duration of the set.
     *
     * @param scrollerDuration such as 500.
     */
    fun setScrollerDuration(scrollerDuration: Int) {
        this.mScrollerDuration = scrollerDuration
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val isIntercepted = super.onInterceptTouchEvent(ev)
        if (!isSwipeEnable()) {
            return isIntercepted
        }

        val action: Int = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = (ev.x).toInt()
                mLastX = mDownX
                mDownY = (ev.y).toInt()
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (ev.x - mDownX).toInt()
                val dy = (ev.y - mDownY).toInt()
                return Math.abs(dx) > mScaledTouchSlop && Math.abs(dx) > Math.abs(dy)
            }
            MotionEvent.ACTION_UP -> {
                val isClick: Boolean = mSwipeCurrentHorizontal?.isClickOnContentView(width, ev.x)
                        ?: false
                if (isMenuOpen && isClick) {
                    smoothOpenMenu()
                    return true
                }
                return false
            }
            MotionEvent.ACTION_CANCEL -> {
                if (!mScroller!!.isFinished) mScroller!!.abortAnimation()
                return false
            }
        }
        return isIntercepted
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isSwipeEnable()) {
            return super.onTouchEvent(event)
        }

        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain()
        mVelocityTracker!!.addMovement(event)//将事件加入到VelocityTracker类实例中

        var dx = 0
        var dy = 0
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = (event.x).toInt()
                mLastY = (event.y).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val disX = (mLastX - event.x).toInt()
                val disY = (mLastY - event.y).toInt()
                if (!mDragging && Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY)) {
                    mDragging = true
                }
                if (mDragging) {
                    if (mSwipeCurrentHorizontal == null || shouldResetSwipe) {
                        mSwipeCurrentHorizontal =
                                if (disX < 0) {
                                    if (mSwipeLeftHorizontal != null) {
                                        mSwipeLeftHorizontal
                                    } else {
                                        mSwipeRightHorizontal
                                    }
                                } else {
                                    if (mSwipeRightHorizontal != null) {
                                        mSwipeRightHorizontal
                                    } else {
                                        mSwipeLeftHorizontal
                                    }
                                }
                    }
                    scrollBy(disX, 0)
                    mLastX = (event.x).toInt()
                    mLastY = (event.y).toInt()
                    shouldResetSwipe = false
                }
            }
            MotionEvent.ACTION_UP -> {
                dx = (mDownX - event.x).toInt()
                dy = (mDownY - event.y).toInt()
                mDragging = false
                //设置maxVelocity值为mScaledMaximumFlingVelocity时，速率大于mScaledMaximumFlingVelocity时，显示的速率都是mScaledMaximumFlingVelocity,速率小于mScaledMaximumFlingVelocity时，显示正常
                mVelocityTracker?.let {
                    it.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity.toFloat())
                    val velocityX = it.xVelocity.toInt()
                    val velocity = Math.abs(velocityX)
                    if (velocity > mScaledMinimumFlingVelocity) {
                        mSwipeCurrentHorizontal?.let { horizontal ->
                            val duration = getSwipeDuration(event, velocity)
                            if (horizontal is RightHorizontal) {
                                if (velocityX < 0) {
                                    smoothOpenMenu(duration)
                                } else {
                                    smoothCloseMenu(duration)
                                }
                            } else {
                                if (velocityX > 0) {
                                    smoothOpenMenu(duration)
                                } else {
                                    smoothCloseMenu(duration)
                                }
                            }
                            ViewCompat.postInvalidateOnAnimation(this)
                        }
                    } else {
                        judgeOpenClose(dx, dy)
                    }
                }
                mVelocityTracker?.clear()
                mVelocityTracker?.recycle()
                mVelocityTracker = null
                if (Math.abs(mDownX - event.x) > mScaledTouchSlop ||
                        Math.abs(mDownY - event.y) > mScaledTouchSlop ||
                        isLeftMenuOpen || isRightMenuOpen) {
                    event.action = MotionEvent.ACTION_CANCEL
                    super.onTouchEvent(event)
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mDragging = false
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                } else {
                    dx = (mDownX - event.x).toInt()
                    dy = (mDownY - event.y).toInt()
                    judgeOpenClose(dx, dy)
                }
            }
        }
        return super.onTouchEvent(event)
    }


    /**
     * compute finish duration.
     *
     * @param ev up event.
     * @param velocity velocity x.
     *
     * @return finish duration.
     */
    private fun getSwipeDuration(ev: MotionEvent, velocity: Int): Int {

    }


    private fun judgeOpenClose(dx: Int, dy: Int) {

    }

    override fun isMenuOpen(): Boolean {
        return isLeftMenuOpen || isRightMenuOpen
    }

    override fun isLeftMenuOpen(): Boolean {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal!!.isMenuOpen(scrollX)
    }

    override fun isRightMenuOpen(): Boolean {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal!!.isMenuOpen(scrollX)
    }

    override fun isCompleteOpen(): Boolean {
        return isLeftCompleteOpen || isRightCompleteOpen
    }

    override fun isLeftCompleteOpen(): Boolean {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal!!.isCompleteClose(scrollX)
    }

    override fun isRightCompleteOpen(): Boolean {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal!!.isCompleteClose(scrollX)
    }

    override fun isMenuOpenNotEqual(): Boolean {

    }

    override fun isLeftMenuOpenNotEqual(): Boolean {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal!!.isMenuOpenNotEqual(scrollX)
    }

    override fun isRightMenuOpenNotEqual(): Boolean {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal!!.isMenuOpenNotEqual(scrollX)
    }

    override fun smoothOpenMenu() {
        smoothOpenMenu(mScrollerDuration)
    }

    override fun smoothOpenLeftMenu() {
        smoothOpenLeftMenu(mScrollerDuration)
    }

    override fun smoothOpenRightMenu() {
        smoothOpenRightMenu(mScrollerDuration)
    }

    override fun smoothOpenLeftMenu(duration: Int) {
        if (mSwipeLeftHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeLeftHorizontal
            smoothOpenMenu(duration)
        }
    }

    override fun smoothOpenRightMenu(duration: Int) {
        if (mSwipeRightHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeRightHorizontal
            smoothOpenMenu(duration)
        }
    }

    private fun smoothOpenMenu(duration: Int) {
        if (mSwipeCurrentHorizontal != null) {
            mSwipeCurrentHorizontal!!.autoOpenMenu(mScroller!!, scrollX, duration)
            invalidate()
        }
    }

    override fun smoothCloseMenu() {
        smoothCloseMenu(mScrollerDuration)
    }

    override fun smoothCloseLeftMenu() {
        if (mSwipeLeftHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeLeftHorizontal
            smoothCloseMenu()
        }
    }

    override fun smoothCloseRightMenu() {
        if (mSwipeRightHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeRightHorizontal
            smoothCloseMenu()
        }
    }

    override fun smoothCloseMenu(duration: Int) {
        if (mSwipeCurrentHorizontal != null) {
            mSwipeCurrentHorizontal!!.autoCloseMenu(mScroller!!, scrollX, duration)
            invalidate()
        }
    }
}