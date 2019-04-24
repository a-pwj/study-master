package pwj.study.recyclerview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller
import android.widget.TextView
import pwj.study.R
import pwj.study.recyclerview.horizontal.Horizontal
import pwj.study.recyclerview.horizontal.LeftHorizontal
import pwj.study.recyclerview.horizontal.RightHorizontal

class SwipeMenuLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

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
    fun isSwipeEnable(): Boolean {
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
}