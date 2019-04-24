package pwj.study.recyclerview.horizontal

import android.view.View
import android.view.ViewGroup
import android.widget.OverScroller

abstract class Horizontal constructor(private var direction: Int, private var menuView: View) {

    protected val mChecker = Checker()

    fun canSwipe(): Boolean {
        return if (menuView is ViewGroup) {
            (menuView as ViewGroup).childCount > 0
        } else {
            false
        }
    }

    fun isCompleteClose(scrollX: Int): Boolean {
        val i = -menuView.width * direction
        return scrollX == 0 && i != 0
    }

    fun getDirection(): Int {
        return direction
    }

    fun getMenuView(): View {
        return menuView
    }

    fun getMenuWidth(): Int {
        return menuView.width
    }

    abstract fun isMenuOpen(scrollX: Int): Boolean

    abstract fun isMenuOpenNotEqual(scrollX: Int): Boolean

    abstract fun autoOpenMenu(scroller: OverScroller, scrollX: Int, duration: Int)

    abstract fun autoCloseMenu(scroller: OverScroller, scrollX: Int, duration: Int)

    abstract fun checkXY(x: Int, y: Int): Checker

    abstract fun isClickOnContentView(contentViewWidth: Int, x: Float): Boolean

    class Checker {
        var x: Int = 0
        var y: Int = 0
        var shouldResetSwipe: Boolean = false
    }
}