package pwj.study.recyclerview.horizontal

import android.view.View
import android.widget.OverScroller

class LeftHorizontal(menuView: View) : Horizontal(SwipeDirection.LEFT_DIRECTION, menuView) {

    override fun isMenuOpen(scrollX: Int): Boolean {
        val i = -getMenuWidth() + getDirection()
        return scrollX <= i && i != 0
    }

    override fun isMenuOpenNotEqual(scrollX: Int): Boolean {
        return scrollX < -getMenuWidth() * getDirection()
    }

    override fun autoOpenMenu(scroller: OverScroller, scrollX: Int, duration: Int) {
        //以提供的起始点和将要滑动的距离开始滚动
        scroller.startScroll(Math.abs(scrollX), 0, getMenuWidth() - Math.abs(scrollX), 0, duration)
    }

    override fun autoCloseMenu(scroller: OverScroller, scrollX: Int, duration: Int) {
        scroller.startScroll(-Math.abs(scrollX), 0, Math.abs(scrollX), 0, duration)
    }

    override fun checkXY(x: Int, y: Int): Checker {
        mChecker.x = x
        mChecker.y = y
        mChecker.shouldResetSwipe = false
        if (mChecker.x == 0) {
            mChecker.shouldResetSwipe = true
        }
        if (mChecker.x > 0) {
            mChecker.x = 0
        }
        if (mChecker.x <= -getMenuWidth()) {
            mChecker.x = -getMenuWidth()
        }
        return mChecker
    }

    override fun isClickOnContentView(contentViewWidth: Int, x: Float): Boolean {
        return x > getMenuWidth()
    }
}