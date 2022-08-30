package sbs.pros.parking.filters

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetFilters<V : View> : BottomSheetBehavior<V> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var swipeEnabled = false

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent
    ): Boolean {
        return if (swipeEnabled) {
            super.onInterceptTouchEvent(parent, child, event)
        } else {
            false
        }
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return if (swipeEnabled) {
            super.onTouchEvent(parent, child, event)
        } else {
            false
        }
    }

}