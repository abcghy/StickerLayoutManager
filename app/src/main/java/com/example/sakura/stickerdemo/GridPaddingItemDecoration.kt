package com.example.sakura.stickerdemo

import android.graphics.Rect
import android.support.annotation.Px
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class GridPaddingItemDecoration(val columns: Int, @Px val verticalSpace: Int,
                                @Px val horizontalSpace: Int = verticalSpace, val orientation: Int = GridLayoutManager.VERTICAL) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        with(outRect) {
            val tvOrder = view.findViewById<TextView>(R.id.tv_order)
            tvOrder?.text = "${parent.getChildAdapterPosition(view)}"
            if (orientation == GridLayoutManager.HORIZONTAL) {

            } else if (orientation == GridLayoutManager.VERTICAL) {
                top = if (columns > parent.getChildAdapterPosition(view)) {
                    0
                } else {
                    verticalSpace
                }
                top = verticalSpace
                left = if (parent.getChildAdapterPosition(view) % columns == 0) 0 else horizontalSpace
            }
        }
    }

}