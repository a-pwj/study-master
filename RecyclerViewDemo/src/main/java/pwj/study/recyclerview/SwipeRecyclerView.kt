package pwj.study.recyclerview

import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewConfiguration
import pwj.study.recyclerview.horizontal.SwipeDirection.LEFT_DIRECTION
import pwj.study.recyclerview.horizontal.SwipeDirection.RIGHT_DIRECTION
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

open class SwipeRecyclerView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    @IntDef(LEFT_DIRECTION, RIGHT_DIRECTION)
    @Retention(RetentionPolicy.SOURCE)
    annotation class DirectionMode

    /**
     * Invalid position.
     */
    private val INVALID_POSITION = -1

    protected val mScaleTouchSlop: Int = ViewConfiguration.get(getContext()).scaledTouchSlop


}