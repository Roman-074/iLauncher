package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.benny.openlauncher.SimpleFingerGestures;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.interfaces.SwipeListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.Tool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CellContainer extends ViewGroup {
    static final int MIN_DISTANCE = 100;
    private boolean animateBackground;
    private Paint bgPaint;
    public boolean blockTouch = false;
    public int cellHeight;
    public int cellSpanH = 0;
    public int cellSpanV = 0;
    public int cellWidth;
    private Rect[][] cells;
    private Long down = Long.valueOf(0);
    public SimpleFingerGestures gestures;
    private boolean hideGrid = true;
    private Paint mPaint;
    private boolean[][] occupied;
    public OnItemRearrangeListener onItemRearrangeListener;
    private PeekDirection peekDirection;
    private Long peekDownTime = Long.valueOf(-1);
    private Point preCoordinate = new Point(-1, -1);
    private SwipeListener swipeListener;
    private long timeClick = 0;
    private final int waittingTimeDoubleTap = 500;
    private float x1;
    private float x2;
    private float y1;
    private float y2;

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        public int x;
        public int xSpan = 1;
        public int y;
        public int ySpan = 1;

        public LayoutParams(int w, int h, int x, int y) {
            super(w, h);
            this.x = x;
            this.y = y;
        }

        public LayoutParams(int w, int h, int x, int y, int xSpan, int ySpan) {
            super(w, h);
            this.x = x;
            this.y = y;
            this.xSpan = xSpan;
            this.ySpan = ySpan;
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }

    public interface OnItemRearrangeListener {
        void onItemRearrange(Point point, Point point2);
    }

    private enum PeekDirection {
        UP,
        LEFT,
        RIGHT,
        DOWN
    }

    public CellContainer(Context c) {
        super(c);
        init();
    }

    public OnItemRearrangeListener getOnItemRearrangeListener() {
        return onItemRearrangeListener;
    }

    public void setOnItemRearrangeListener(OnItemRearrangeListener onItemRearrangeListener) {
        this.onItemRearrangeListener = onItemRearrangeListener;
    }

    public CellContainer(Context c, AttributeSet attr) {
        super(c, attr);
        init();
    }

    public void setGridSize(int x, int y) {
        this.cellSpanV = y;
        this.cellSpanH = x;
        this.occupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.cellSpanH, this.cellSpanV});
        for (int i = 0; i < this.cellSpanH; i++) {
            for (int j = 0; j < this.cellSpanV; j++) {
                this.occupied[i][j] = false;
            }
        }
        requestLayout();
    }

    public void setHideGrid(boolean hideGrid) {
        this.hideGrid = hideGrid;
        invalidate();
    }

    public void resetOccupiedSpace() {
        if (this.cellSpanH > 0 && this.cellSpanH > 0) {
            this.occupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.cellSpanH, this.cellSpanV});
        }
    }

    public void removeAllViews() {
        resetOccupiedSpace();
        super.removeAllViews();
    }

    private PeekDirection getPeekDirectionFromCoordinate(Point from, Point to) {
        if (from.y - to.y > 0) {
            return PeekDirection.UP;
        }
        if (from.y - to.y < 0) {
            return PeekDirection.DOWN;
        }
        if (from.x - to.x > 0) {
            return PeekDirection.LEFT;
        }
        if (from.x - to.x < 0) {
            return PeekDirection.RIGHT;
        }
        return null;
    }

    public void peekItemAndSwap(DragEvent event) {
        Point coordinate = touchPosToCoordinate((int) event.getX(), (int) event.getY(), 1, 1, false);
        if (coordinate != null) {
            if (!this.preCoordinate.equals(coordinate)) {
                this.peekDirection = getPeekDirectionFromCoordinate(this.preCoordinate, coordinate);
                this.peekDownTime = Long.valueOf(-1);
            }
            if (this.peekDownTime.longValue() == -1) {
                this.peekDownTime = Long.valueOf(System.currentTimeMillis());
                this.preCoordinate = coordinate;
            }
            if (System.currentTimeMillis() - this.peekDownTime.longValue() <= 600) {
                this.preCoordinate = coordinate;
            } else if (this.occupied[coordinate.x][coordinate.y]) {
                View targetView = coordinateToChildView(coordinate);
                if (targetView != null) {
                    LayoutParams targetParams = (LayoutParams) targetView.getLayoutParams();
                    if (targetParams.xSpan <= 1 && targetParams.ySpan <= 1) {
                        this.occupied[targetParams.x][targetParams.y] = false;
                        Point targetPoint = findFreeSpace(targetParams.x, targetParams.y, this.peekDirection);
                        if (targetPoint!=null){
                            Tool.print(targetPoint);
                            onItemRearrange(new Point(targetParams.x, targetParams.y), targetPoint);
                            targetParams.x = targetPoint.x;
                            targetParams.y = targetPoint.y;
                            this.occupied[targetPoint.x][targetPoint.y] = true;
                            requestLayout();
                        }

                    }
                }
            }
        }
    }

    public void onItemRearrange(Point from, Point to) {
        if (this.onItemRearrangeListener != null) {
            this.onItemRearrangeListener.onItemRearrange(from, to);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case 0:
                this.x1 = event.getX();
                this.y1 = event.getY();
                this.down = Long.valueOf(System.currentTimeMillis());
                break;
            case 1:
                this.x2 = event.getX();
                this.y2 = event.getY();
                float deltaX = this.x2 - this.x1;
                float deltaY = this.y2 - this.y1;
                if (Math.abs(deltaX) <= Math.abs(deltaY) || Math.abs(deltaX) < 100.0f) {
                    if (Math.abs(deltaX) > Math.abs(deltaY) || Math.abs(deltaY) < 100.0f) {
                        if (System.currentTimeMillis() - this.timeClick < 500 && this.swipeListener != null) {
                            this.swipeListener.onDoubleTap();
                        }
                        this.timeClick = System.currentTimeMillis();
                    } else if (this.y2 > this.y1) {
                        if (this.swipeListener != null) {
                            this.swipeListener.onSwipeDown();
                        }
                    } else if (this.swipeListener != null) {
                        this.swipeListener.onSwipeUp();
                    }
                }
                if (System.currentTimeMillis() - this.down.longValue() < 260 && this.blockTouch) {
                    performClick();
                }
                if (this.swipeListener != null) {
                    this.swipeListener.onUpAndCancel();
                    break;
                }
                break;
            case 2:
                float deltaMoveY = event.getY() - this.y1;
                if (deltaMoveY <= 35.0f) {
                    deltaMoveY = 0.0f;
                }
                if (deltaMoveY > 300.0f) {
                    deltaMoveY = 300.0f;
                }
                if (this.swipeListener != null) {
                    this.swipeListener.onMoveDown(deltaMoveY);
                    break;
                }
                break;
            case 3:
                if (this.swipeListener != null) {
                    this.swipeListener.onUpAndCancel();
                    break;
                }
                break;
        }
        if (this.blockTouch) {
            return true;
        }
        if (this.gestures != null) {
            try {
                this.gestures.onTouch(this, event);
            } catch (Exception e) {
            }
        }
        return super.onTouchEvent(event);
    }

    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.x1 = ev.getX();
                this.y1 = ev.getY();
                break;
            case 1:
                this.x2 = ev.getX();
                this.y2 = ev.getY();
                float deltaX = this.x2 - this.x1;
                float deltaY = this.y2 - this.y1;
                if (Math.abs(deltaX) <= Math.abs(deltaY) || Math.abs(deltaX) < 100.0f) {
                    if (Math.abs(deltaX) > Math.abs(deltaY) || Math.abs(deltaY) < 100.0f) {
                        if (System.currentTimeMillis() - this.timeClick < 500 && this.swipeListener != null) {
                            this.swipeListener.onDoubleTap();
                        }
                        this.timeClick = System.currentTimeMillis();
                    } else if (this.y2 > this.y1) {
                        if (this.swipeListener != null) {
                            this.swipeListener.onSwipeDown();
                        }
                    } else if (this.swipeListener != null) {
                        this.swipeListener.onSwipeUp();
                    }
                }
                if (this.swipeListener != null) {
                    this.swipeListener.onUpAndCancel();
                    break;
                }
                break;
            case 2:
                float deltaMoveY = ev.getY() - this.y1;
                if (deltaMoveY <= 35.0f) {
                    deltaMoveY = 0.0f;
                }
                if (deltaMoveY > 300.0f) {
                    deltaMoveY = 300.0f;
                }
                if (this.swipeListener != null) {
                    this.swipeListener.onMoveDown(deltaMoveY);
                    break;
                }
                break;
            case 3:
                if (this.swipeListener != null) {
                    this.swipeListener.onUpAndCancel();
                    break;
                }
                break;
        }
        if (this.blockTouch) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void init() {
        setWillNotDraw(false);
        this.mPaint = new Paint(1);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeWidth(Tool.DEFAULT_IMAGE_BACKOFF_MULT);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setColor(-1);
        this.mPaint.setAlpha(0);
        this.bgPaint = new Paint(1);
        this.bgPaint.setStyle(Style.FILL);
        this.bgPaint.setColor(-1);
        this.bgPaint.setAlpha(0);
    }

    public void animateBackgroundShow() {
        this.animateBackground = true;
        invalidate();
    }

    public void animateBackgroundHide() {
        this.animateBackground = false;
        invalidate();
    }

    public Point findFreeSpace() {
        for (int y = 0; y < this.occupied[0].length; y++) {
            for (int x = 0; x < this.occupied.length; x++) {
                if (!this.occupied[x][y]) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public Point findFreeSpace(int spanX, int spanY) {
        int y = 0;
        while (y < this.occupied[0].length) {
            int x = 0;
            while (x < this.occupied.length) {
                if (!this.occupied[x][y] && !checkOccupied(new Point(x, y), spanX, spanY)) {
                    return new Point(x, y);
                }
                x++;
            }
            y++;
        }
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Point findFreeSpace(int i, int i2, PeekDirection peekDirection) {
        if (peekDirection != null) {
            switch (peekDirection) {
                case DOWN:
                    Point peekDirection1 = new Point(i, i2 - 1);
                    if (isValid(peekDirection1.x, peekDirection1.y) && !this.occupied[peekDirection1.x][peekDirection1.y]) {
                        return peekDirection1;
                    }
                case LEFT:
                    Point peekDirection2 = new Point(i + 1, i2);
                    if (isValid(peekDirection2.x, peekDirection2.y) && !this.occupied[peekDirection2.x][peekDirection2.y]) {
                        return peekDirection2;
                    }
                case RIGHT:
                    Point peekDirection3 = new Point(i - 1, i2);
                    if (isValid(peekDirection3.x, peekDirection3.y) && !this.occupied[peekDirection3.x][peekDirection3.y]) {
                        return peekDirection3;
                    }
                case UP:
                    Point peekDirection4 = new Point(i, i2 + 1);
                    if (isValid(peekDirection4.x, peekDirection4.y) && !this.occupied[peekDirection4.x][peekDirection4.y]) {
                        return peekDirection4;
                    }
                default:
                    break;
            }
        }
        LinkedList<Point> peekDirection5 = new LinkedList();
        HashSet hashSet = new HashSet();
        peekDirection5.add(new Point(i, i2));

        while (peekDirection5.isEmpty()) {
            Point point = (Point) peekDirection5.remove();

            if (isValid(point.x, point.y - 1) ) {
                Point i1 = new Point(point.x, point.y - 1);
                if (!hashSet.contains(i1)) {
                    if (!this.occupied[i1.x][i1.y]) {
                        return i1;
                    }
                    peekDirection5.add(i1);
                    hashSet.add(point);
                }
            }
            if (isValid(point.x, point.y + 1) ) {
                Point i3 = new Point(point.x, point.y + 1);
                if (!hashSet.contains(i3)) {
                    if (!this.occupied[i3.x][i3.y]) {
                        return i3;
                    }
                    peekDirection5.add(i3);
                    hashSet.add(point);
                }
            }
            if (isValid(point.x - 1, point.y) ) {
                Point i4 = new Point(point.x - 1, point.y);
                if (!hashSet.contains(i4)) {
                    if (!this.occupied[i4.x][i4.y]) {
                        return i4;
                    }
                    peekDirection5.add(i4);
                    hashSet.add(point);
                }
            }
            if (isValid(point.x + 1, point.y) ) {
                Point i5 = new Point(point.x + 1, point.y);
                if (hashSet.contains(i5)) {
                    continue;
                } else if (!this.occupied[i5.x][i5.y]) {
                    return i5;
                } else {
                    peekDirection5.add(i5);
                    hashSet.add(point);
                }
            }
        }
        return null;
    }


    public boolean isValid(int x, int y) {
        return x >= 0 && x <= this.occupied.length - 1 && y >= 0 && y <= this.occupied[0].length - 1;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.bgPaint);
        int x = 0;
        while (x < this.cellSpanH) {
            int y = 0;
            while (y < this.cellSpanV) {
                if (x < this.cells.length && y < this.cells[x].length) {
                    Rect cell = this.cells[x][y];
                    canvas.save();
                    canvas.rotate(45.0f, (float) cell.left, (float) cell.top);
                    canvas.drawRect(((float) cell.left) - 7.0f, ((float) cell.top) - 7.0f, ((float) cell.left) + 7.0f, ((float) cell.top) + 7.0f, this.mPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.rotate(45.0f, (float) cell.left, (float) cell.bottom);
                    canvas.drawRect(((float) cell.left) - 7.0f, ((float) cell.bottom) - 7.0f, ((float) cell.left) + 7.0f, ((float) cell.bottom) + 7.0f, this.mPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.rotate(45.0f, (float) cell.right, (float) cell.top);
                    canvas.drawRect(((float) cell.right) - 7.0f, ((float) cell.top) - 7.0f, ((float) cell.right) + 7.0f, ((float) cell.top) + 7.0f, this.mPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.rotate(45.0f, (float) cell.right, (float) cell.bottom);
                    canvas.drawRect(((float) cell.right) - 7.0f, ((float) cell.bottom) - 7.0f, ((float) cell.right) + 7.0f, ((float) cell.bottom) + 7.0f, this.mPaint);
                    canvas.restore();
                }
                y++;
            }
            x++;
        }
        if (this.hideGrid && this.mPaint.getAlpha() != 0) {
            this.mPaint.setAlpha(Math.max(this.mPaint.getAlpha() - 20, 0));
            invalidate();
        } else if (!(this.hideGrid || this.mPaint.getAlpha() == 255)) {
            this.mPaint.setAlpha(Math.min(this.mPaint.getAlpha() + 20, 255));
            invalidate();
        }
        if (!this.animateBackground && this.bgPaint.getAlpha() != 0) {
            this.bgPaint.setAlpha(Math.max(this.bgPaint.getAlpha() - 10, 0));
            invalidate();
        } else if (this.animateBackground && this.bgPaint.getAlpha() != 100) {
            this.bgPaint.setAlpha(Math.min(this.bgPaint.getAlpha() + 10, 100));
            invalidate();
        }
    }

    public void addView(View child) {
        try {
            if (child.getParent() != null) {
                ((ViewGroup) child.getParent()).removeView(child);
            }
            setOccupied(true, (LayoutParams) child.getLayoutParams());
            super.addView(child);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeView(View view) {
        try {
            setOccupied(false, (LayoutParams) view.getLayoutParams());
            super.removeView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addViewToGrid(View view, int x, int y, int xSpan, int ySpan) {
        view.setLayoutParams(new LayoutParams(-2, -2, x, y, xSpan, ySpan));

        addView(view);
    }

    public void addViewToGrid(View view) {
        addView(view);
    }

    public List<View> getAllCells() {
        ArrayList<View> views = new ArrayList();
        for (int i = 0; i < getChildCount(); i++) {
            views.add(getChildAt(i));
        }
        return views;
    }

    public void setOccupied(boolean b, LayoutParams lp) {
        for (int x = lp.x; x < lp.x + lp.xSpan; x++) {
            for (int y = lp.y; y < lp.y + lp.ySpan; y++) {
                this.occupied[x][y] = b;
            }
        }
    }

    public boolean checkOccupied(Point start, int spanX, int spanY) {
        if (start.x + spanX > this.occupied.length || start.y + spanY > this.occupied[0].length) {
            return true;
        }
        for (int y = start.y; y < start.y + spanY; y++) {
            for (int x = start.x; x < start.x + spanX; x++) {
                if (this.occupied[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }

    public View coordinateToChildView(Point pos) {
        if (pos == null) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            if (pos.x >= lp.x && pos.y >= lp.y && pos.x < lp.x + lp.xSpan && pos.y < lp.y + lp.ySpan) {
                return getChildAt(i);
            }
        }
        return null;
    }

    public LayoutParams coordinateToLayoutParams(int mX, int mY, int xSpan, int ySpan) {
        Point pos = touchPosToCoordinate(mX, mY, xSpan, ySpan, true);
        if (pos == null) {
            return null;
        }
        return new LayoutParams(-2, -2, pos.x, pos.y, xSpan, ySpan);
    }

    public Point touchPosToCoordinate(int mX, int mY, int xSpan, int ySpan, boolean checkAvailability) {
        mX -= ((xSpan - 1) * this.cellWidth) / 2;
        mY -= ((ySpan - 1) * this.cellHeight) / 2;
        if (this.cells == null) {
            return null;
        }
        int x = 0;
        while (x < this.cellSpanH) {
            int y = 0;
            while (y < this.cellSpanV) {
                Rect cell = this.cells[x][y];
                if (mY < cell.top || mY > cell.bottom || mX < cell.left || mX > cell.right) {
                    y++;
                } else {
                    if (checkAvailability) {
                        if (this.occupied[x][y]) {
                            return null;
                        }
                        int dy = (y + ySpan) - 1;
                        if ((x + xSpan) - 1 >= this.cellSpanH - 1) {
                            x = ((this.cellSpanH - 1) + 1) - xSpan;
                        }
                        if (dy >= this.cellSpanV - 1) {
                            y = ((this.cellSpanV - 1) + 1) - ySpan;
                        }
                        for (int x2 = x; x2 < x + xSpan; x2++) {
                            for (int y2 = y; y2 < y + ySpan; y2++) {
                                if (this.occupied[x2][y2]) {
                                    return null;
                                }
                            }
                        }
                    }
                    return new Point(x, y);
                }
            }
            x++;
        }
        return null;
    }

    @SuppressLint("WrongConstant")
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!(this instanceof Dock)) {
            int A = ((getWidth() - (Setup.appSettings().getDesktopColumnCount() * BaseUtils.genpx(getContext(), Setup.appSettings().getDesktopIconSize()))) * ((int) AppItemView.partPadding)) / ((((Setup.appSettings().getDesktopColumnCount() * 2) * ((int) AppItemView.partPadding)) + (Setup.appSettings().getDesktopColumnCount() * 4)) + (((int) AppItemView.partPadding) * 2));
            setPadding(A, 0, A, 0);
        }
        int width = ((r - l) - getPaddingLeft()) - getPaddingRight();
        int height = ((b - t) - getPaddingTop()) - getPaddingBottom();
        if (this.cellSpanH == 0) {
            this.cellSpanH = 1;
        }
        if (this.cellSpanV == 0) {
            this.cellSpanV = 1;
        }
        this.cellWidth = width / this.cellSpanH;
        this.cellHeight = height / this.cellSpanV;
        initCellInfo(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(lp.xSpan * this.cellWidth, 1073741824), MeasureSpec.makeMeasureSpec(lp.ySpan * this.cellHeight, 1073741824));
                Rect upRect = this.cells[lp.x][lp.y];
                Rect downRect = new Rect();
                if ((lp.x + lp.xSpan) - 1 < this.cellSpanH && (lp.y + lp.ySpan) - 1 < this.cellSpanV) {
                    downRect = this.cells[(lp.x + lp.xSpan) - 1][(lp.y + lp.ySpan) - 1];
                }
                if (lp.xSpan == 1 && lp.ySpan == 1) {
                    child.layout(upRect.left, upRect.top, upRect.right, upRect.bottom);
                } else if (lp.xSpan > 1 && lp.ySpan > 1) {
                    child.layout(upRect.left, upRect.top, downRect.right, downRect.bottom);
                } else if (lp.xSpan > 1) {
                    child.layout(upRect.left, upRect.top, downRect.right, upRect.bottom);
                } else if (lp.ySpan > 1) {
                    child.layout(upRect.left, upRect.top, upRect.right, downRect.bottom);
                }
            }
        }
    }

    private void initCellInfo(int l, int t, int r, int b) {
        this.cells = (Rect[][]) Array.newInstance(Rect.class, new int[]{this.cellSpanH, this.cellSpanV});
        int curLeft = l;
        int curTop = t;
        int curRight = l + this.cellWidth;
        int curBottom = t + this.cellHeight;
        for (int i = 0; i < this.cellSpanH; i++) {
            if (i != 0) {
                curLeft += this.cellWidth;
                curRight += this.cellWidth;
            }
            for (int j = 0; j < this.cellSpanV; j++) {
                if (j != 0) {
                    curTop += this.cellHeight;
                    curBottom += this.cellHeight;
                }
                this.cells[i][j] = new Rect(curLeft, curTop, curRight, curBottom);
            }
            curTop = t;
            curBottom = t + this.cellHeight;
        }
    }
}
