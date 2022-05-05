package com.aleskovacic.eventslocator4.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class SpacesItemDecorator extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecorator(Context context, int space) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, r.getDisplayMetrics());
        this.space = Math.round(px);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
        //outRect.left = space;
        //outRect.right = space;

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        }
    }
}