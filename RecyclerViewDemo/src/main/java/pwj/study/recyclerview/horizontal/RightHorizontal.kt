package pwj.study.recyclerview.horizontal

import android.view.View
import android.widget.OverScroller

internal class RightHorizontal(menuView: View) : Horizontal(SwipeDirection.RIGHT_DIRECTION, menuView) {

    override fun isMenuOpen(scrollX: Int): Boolean {
        val i = -getMenuView().width * getDirection()
        return scrollX >= i && i != 0
    }

    override fun isMenuOpenNotEqual(scrollX: Int): Boolean {
        return scrollX > -getMenuView().width * getDirection()
    }

    override fun autoOpenMenu(scroller: OverScroller, scrollX: Int, duration: Int) {
        scroller.startScroll(Math.abs(scrollX), 0, getMenuView().width - Math.abs(scrollX), 0, duration)
    }

    override fun autoCloseMenu(scroller: OverScroller, scrollX: Int, duration: Int) {
        scroller.startScroll(-Math.abs(scrollX), 0, Math.abs(scrollX), 0, duration)
    }

    override fun checkXY(x: Int, y: Int): Horizontal.Checker {
        mChecker.x = x
        mChecker.y = y
        mChecker.shouldResetSwipe = false
        if (mChecker.x == 0) {
            mChecker.shouldResetSwipe = true
        }
        if (mChecker.x < 0) {
            mChecker.x = 0
        }
        if (mChecker.x > getMenuView().width) {
            mChecker.x = getMenuView().width
        }
        return mChecker
    }

    override fun isClickOnContentView(contentViewWidth: Int, x: Float): Boolean {
        return x < contentViewWidth - getMenuView().width
    }
}