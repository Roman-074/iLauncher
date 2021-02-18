package com.benny.openlauncher.core.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.widget.SmoothViewPager.OnPageChangeListener;

public class PagerIndicator extends View implements OnPageChangeListener {
    private static float pad;
    private Paint dotPaint;
    private float dotSize;
    private SmoothViewPager pager;
    public int prePageCount;
    private int previousPage = -1;
    private int realPreviousPage;
    private float scaleFactor = Tool.DEFAULT_BACKOFF_MULT;
    private float scaleFactor2 = 1.5f;

    public PagerIndicator(Context context) {
        super(context);
        init();
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pad = (float) Tool.dp2px(3, getContext());
        setWillNotDraw(false);
        this.dotPaint = new Paint();
        this.dotPaint.setColor(-1);
        this.dotPaint.setAntiAlias(true);
    }

    public void setOutlinePaint() {
        this.dotPaint.setStyle(Style.STROKE);
        invalidate();
    }

    public void setFillPaint() {
        this.dotPaint.setStyle(Style.FILL);
        invalidate();
    }

    public void setColor(int c) {
        this.dotPaint.setColor(c);
        invalidate();
    }

    public void setViewPager(SmoothViewPager pager) {
        if (pager != null) {
            this.pager = pager;
            this.prePageCount = pager.getAdapter().getCount();
            pager.addOnPageChangeListener(this);
            Tool.print(Integer.valueOf(pager.getAdapter().getCount()));
            invalidate();
        } else if (this.pager != null) {
            this.pager.removeOnPageChangeListener(this);
            this.pager = null;
            invalidate();
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (this.prePageCount != this.pager.getAdapter().getCount()) {
            this.prePageCount = this.pager.getAdapter().getCount();
        }
        invalidate();
    }

    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.dotSize = ((float) getHeight()) - (pad * 1.25f);
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void onDraw(Canvas canvas) {
        this.dotSize = ((float) getHeight()) - (pad * 1.25f);
        if (this.pager != null) {
            canvas.translate(((float) (getWidth() / 2)) - ((((float) this.pager.getAdapter().getCount()) * (this.dotSize + (pad * Tool.DEFAULT_IMAGE_BACKOFF_MULT))) / Tool.DEFAULT_IMAGE_BACKOFF_MULT), 0.0f);
            getWidth();
            if (this.realPreviousPage != this.pager.getCurrentItem()) {
                this.scaleFactor = Tool.DEFAULT_BACKOFF_MULT;
                this.realPreviousPage = this.pager.getCurrentItem();
            }
            int i = 0;
            while (i < this.pager.getAdapter().getCount()) {
                if (i == this.previousPage && i != this.pager.getCurrentItem()) {
                    this.scaleFactor2 = Tool.clampFloat(this.scaleFactor2 - 0.05f, Tool.DEFAULT_BACKOFF_MULT, 1.5f);
                    Tool.print(Float.valueOf(this.scaleFactor2));
                    canvas.drawCircle(((this.dotSize / Tool.DEFAULT_IMAGE_BACKOFF_MULT) + pad) + ((this.dotSize + (pad * Tool.DEFAULT_IMAGE_BACKOFF_MULT)) * ((float) i)), (float) (getHeight() / 2), (this.scaleFactor2 * this.dotSize) / Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.dotPaint);
                    if (this.scaleFactor2 != Tool.DEFAULT_BACKOFF_MULT) {
                        invalidate();
                    } else {
                        this.scaleFactor2 = 1.5f;
                        this.previousPage = -1;
                    }
                } else if (this.pager.getCurrentItem() == i) {
                    if (this.previousPage == -1) {
                        this.previousPage = i;
                    }
                    this.scaleFactor = Tool.clampFloat(this.scaleFactor + 0.05f, Tool.DEFAULT_BACKOFF_MULT, 1.5f);
                    canvas.drawCircle(((this.dotSize / Tool.DEFAULT_IMAGE_BACKOFF_MULT) + pad) + ((this.dotSize + (pad * Tool.DEFAULT_IMAGE_BACKOFF_MULT)) * ((float) i)), (float) (getHeight() / 2), (this.scaleFactor * this.dotSize) / Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.dotPaint);
                    if (this.scaleFactor != 1.5f) {
                        invalidate();
                    }
                } else {
                    canvas.drawCircle(((this.dotSize / Tool.DEFAULT_IMAGE_BACKOFF_MULT) + pad) + ((this.dotSize + (pad * Tool.DEFAULT_IMAGE_BACKOFF_MULT)) * ((float) i)), (float) (getHeight() / 2), this.dotSize / Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.dotPaint);
                }
                i++;
            }
        }
    }
}
