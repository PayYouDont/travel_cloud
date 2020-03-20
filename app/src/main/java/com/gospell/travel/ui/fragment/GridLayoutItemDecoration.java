package com.gospell.travel.ui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int dividerWidth;
    private int dividerWidthTop;
    private int dividerWidthBot;

    private Paint dividerPaint;
    /**
     * @param spanCount gridLayoutManager 列数
     * @param space 分割块宽高,单位:dp
     */
    public GridLayoutItemDecoration(Context context, int spanCount, int space) {
        this.spanCount = spanCount;

        this.dividerPaint = new Paint ();
        this.dividerPaint.setColor(Color.BLUE);

        this.dividerWidth = dip2px(context, space);
        this.dividerWidthTop = dividerWidth / 2;
        this.dividerWidthBot = -dividerWidthTop;

    }
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw (c, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver (c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        int column = (pos) % spanCount;// 计算这个child 处于第几列
        outRect.top = dividerWidthTop;
        outRect.bottom = dividerWidthBot;
        outRect.left = (column * dividerWidth / spanCount);
        outRect.right = dividerWidth - (column + 1) * dividerWidth / spanCount;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
