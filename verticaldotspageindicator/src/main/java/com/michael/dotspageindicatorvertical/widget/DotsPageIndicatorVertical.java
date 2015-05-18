package com.michael.dotspageindicatorvertical.widget;

/**
 * Created by zhoulujue on 15/5/15.
 */
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.GridViewPager.OnAdapterChangeListener;
import android.support.wearable.view.GridViewPager.OnPageChangeListener;
import android.support.wearable.view.SimpleAnimatorListener;
import android.util.AttributeSet;
import android.view.View;

import com.michael.dotspageindicatorvertical.R.style;
import com.michael.dotspageindicatorvertical.R.styleable;

import java.util.concurrent.TimeUnit;

public class DotsPageIndicatorVertical extends View implements OnPageChangeListener, OnAdapterChangeListener {
    private int mDotSpacing;
    private float mDotRadius;
    private float mDotRadiusSelected;
    private int mDotColor;
    private int mDotColorSelected;
    private boolean mDotFadeWhenIdle;
    private int mDotFadeOutDelay;
    private int mDotFadeOutDuration;
    private int mDotFadeInDuration;
    private float mDotShadowDx;
    private float mDotShadowDy;
    private float mDotShadowRadius;
    private int mDotShadowColor;
    private GridPagerAdapter mAdapter;
    private int mRowCount;
    private int mSelectedColumn;
    private int mSelectedRow;
    private int mCurrentState;
    private final Paint mDotPaint;
    private final Paint mDotPaintShadow;
    private final Paint mDotPaintSelected;
    private final Paint mDotPaintShadowSelected;
    private boolean mVisible;
    private GridViewPager mPager;
    private OnPageChangeListener mPageChangeListener;
    private OnAdapterChangeListener mAdapterChangeListener;

    public DotsPageIndicatorVertical(Context context) {
        this(context, (AttributeSet)null);
    }

    public DotsPageIndicatorVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotsPageIndicatorVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, styleable.DotsPageIndicator, 0, style.DotsPageIndicatorStyle);
        this.mDotSpacing = a.getDimensionPixelOffset(styleable.DotsPageIndicator_dotSpacing, 0);
        this.mDotRadius = a.getDimension(styleable.DotsPageIndicator_dotRadius, 0.0F);
        this.mDotRadiusSelected = a.getDimension(styleable.DotsPageIndicator_dotRadiusSelected, 0.0F);
        this.mDotColor = a.getColor(styleable.DotsPageIndicator_dotColor, 0);
        this.mDotColorSelected = a.getColor(styleable.DotsPageIndicator_dotColorSelected, 0);
        this.mDotFadeOutDelay = a.getInt(styleable.DotsPageIndicator_dotFadeOutDelay, 0);
        this.mDotFadeOutDuration = a.getInt(styleable.DotsPageIndicator_dotFadeOutDuration, 0);
        this.mDotFadeInDuration = a.getInt(styleable.DotsPageIndicator_dotFadeInDuration, 0);
        this.mDotFadeWhenIdle = a.getBoolean(styleable.DotsPageIndicator_dotFadeWhenIdle, false);
        this.mDotShadowDx = a.getDimension(styleable.DotsPageIndicator_dotShadowDx, 0.0F);
        this.mDotShadowDy = a.getDimension(styleable.DotsPageIndicator_dotShadowDy, 0.0F);
        this.mDotShadowRadius = a.getDimension(styleable.DotsPageIndicator_dotShadowRadius, 0.0F);
        this.mDotShadowColor = a.getColor(styleable.DotsPageIndicator_dotShadowColor, 0);
        a.recycle();
        this.mDotPaint = new Paint(1);
        this.mDotPaint.setColor(this.mDotColor);
        this.mDotPaint.setStyle(Style.FILL);
        this.mDotPaintSelected = new Paint(1);
        this.mDotPaintSelected.setColor(this.mDotColorSelected);
        this.mDotPaintSelected.setStyle(Style.FILL);
        this.mDotPaintShadow = new Paint(1);
        this.mDotPaintShadowSelected = new Paint(1);
        this.mCurrentState = 0;
        if(this.isInEditMode()) {
            this.mRowCount = 5;
            this.mSelectedRow = 1;
            this.mDotFadeWhenIdle = false;
        }

        if(this.mDotFadeWhenIdle) {
            this.mVisible = false;
            this.animate().alpha(0.0F).setStartDelay(2000L).setDuration((long)this.mDotFadeOutDuration).start();
        }

        this.updateShadows();
    }

    private void updateShadows() {
        this.updateDotPaint(this.mDotPaint, this.mDotPaintShadow, this.mDotRadius, this.mDotShadowRadius, this.mDotColor, this.mDotShadowColor);
        this.updateDotPaint(this.mDotPaintSelected, this.mDotPaintShadowSelected, this.mDotRadiusSelected, this.mDotShadowRadius, this.mDotColor, this.mDotShadowColor);
    }

    private void updateDotPaint(Paint dotPaint, Paint shadowPaint, float baseRadius, float shadowRadius, int color, int shadowColor) {
        float radius = baseRadius + shadowRadius;
        float shadowStart = baseRadius / radius;
        RadialGradient gradient = new RadialGradient(0.0F, 0.0F, radius, new int[]{shadowColor, shadowColor, 0}, new float[]{0.0F, shadowStart, 1.0F}, TileMode.CLAMP);
        shadowPaint.setShader(gradient);
        dotPaint.setColor(color);
        dotPaint.setStyle(Style.FILL);
    }

    public void setPager(GridViewPager pager) {
        if(this.mPager != pager) {
            if(this.mPager != null) {
                this.mPager.setOnPageChangeListener((OnPageChangeListener)null);
                this.mPager.setOnAdapterChangeListener((OnAdapterChangeListener)null);
                this.mPager = null;
            }

            this.mPager = pager;
            if(this.mPager != null) {
                this.mPager.setOnPageChangeListener(this);
                this.mPager.setOnAdapterChangeListener(this);
                this.mAdapter = this.mPager.getAdapter();
            }
        }

        if(this.mAdapter != null && this.mAdapter.getRowCount() > 0) {
            this.rowChanged(0, 0);
        }

    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mPageChangeListener = listener;
    }

    public void setOnAdapterChangeListener(OnAdapterChangeListener listener) {
        this.mAdapterChangeListener = listener;
    }

    public float getDotSpacing() {
        return (float)this.mDotSpacing;
    }

    public void setDotSpacing(int spacing) {
        if(this.mDotSpacing != spacing) {
            this.mDotSpacing = spacing;
            this.requestLayout();
        }

    }

    public float getDotRadius() {
        return this.mDotRadius;
    }

    public void setDotRadius(int radius) {
        if(this.mDotRadius != (float)radius) {
            this.mDotRadius = (float)radius;
            this.updateShadows();
            this.invalidate();
        }

    }

    public float getDotRadiusSelected() {
        return this.mDotRadiusSelected;
    }

    public void setDotRadiusSelected(int radius) {
        if(this.mDotRadiusSelected != (float)radius) {
            this.mDotRadiusSelected = (float)radius;
            this.updateShadows();
            this.invalidate();
        }

    }

    public int getDotColor() {
        return this.mDotColor;
    }

    public void setDotColor(int color) {
        if(this.mDotColor != color) {
            this.mDotColor = color;
            this.invalidate();
        }

    }

    public int getDotColorSelected() {
        return this.mDotColorSelected;
    }

    public void setDotColorSelected(int color) {
        if(this.mDotColorSelected != color) {
            this.mDotColorSelected = color;
            this.invalidate();
        }

    }

    public boolean getDotFadeWhenIdle() {
        return this.mDotFadeWhenIdle;
    }

    public void setDotFadeWhenIdle(boolean fade) {
        this.mDotFadeWhenIdle = fade;
        if(!fade && !this.mVisible) {
            this.fadeIn();
        }

    }

    public int getDotFadeOutDuration() {
        return this.mDotFadeOutDuration;
    }

    public void setDotFadeOutDuration(int duration, TimeUnit unit) {
        this.mDotFadeOutDuration = (int)TimeUnit.MILLISECONDS.convert((long)duration, unit);
    }

    public int getDotFadeInDuration() {
        return this.mDotFadeInDuration;
    }

    public void setDotFadeInDuration(int duration, TimeUnit unit) {
        this.mDotFadeInDuration = (int)TimeUnit.MILLISECONDS.convert((long)duration, unit);
    }

    public int getDotFadeOutDelay() {
        return this.mDotFadeOutDelay;
    }

    public void setDotFadeOutDelay(int delay) {
        this.mDotFadeOutDelay = delay;
    }

    public float getDotShadowRadius() {
        return this.mDotShadowRadius;
    }

    public void setDotShadowRadius(float radius) {
        if(this.mDotShadowRadius != radius) {
            this.mDotShadowRadius = radius;
            this.updateShadows();
            this.invalidate();
        }

    }

    public float getDotShadowDx() {
        return this.mDotShadowDx;
    }

    public void setDotShadowDx(float dx) {
        this.mDotShadowDx = dx;
        this.invalidate();
    }

    public float getDotShadowDy() {
        return this.mDotShadowDy;
    }

    public void setDotShadowDy(float dy) {
        this.mDotShadowDy = dy;
        this.invalidate();
    }

    public int getDotShadowColor() {
        return this.mDotShadowColor;
    }

    public void setDotShadowColor(int color) {
        this.mDotShadowColor = color;
        this.updateShadows();
        this.invalidate();
    }

    private void columnChanged(int column) {
        this.mSelectedColumn = column;
        this.invalidate();
    }

    private void rowChanged(int row, int column) {
        int count = this.mAdapter.getRowCount();
        if(count != this.mRowCount) {
            this.mRowCount = count;
            this.mSelectedRow = row;
            this.requestLayout();
        } else if(row != this.mSelectedRow) {
            this.mSelectedRow = row;
            this.invalidate();
        }
    }

    private void fadeIn() {
        this.mVisible = true;
        this.animate().cancel();
        this.animate().alpha(1.0F).setStartDelay(0L).setDuration((long)this.mDotFadeInDuration).start();
    }

    private void fadeOut() {
        this.mVisible = false;
        this.animate().cancel();
        this.animate().alpha(0.0F).setStartDelay(0L).setDuration((long)this.mDotFadeOutDuration).start();
    }

    private void fadeInOut() {
        this.mVisible = true;
        this.animate().cancel();
        this.animate().alpha(1.0F).setStartDelay(0L).setDuration((long)this.mDotFadeInDuration).setListener(new SimpleAnimatorListener() {
            public void onAnimationComplete(Animator animator) {
                DotsPageIndicatorVertical.this.mVisible = false;
                DotsPageIndicatorVertical.this.animate().alpha(0.0F).setListener((AnimatorListener)null).setStartDelay((long) DotsPageIndicatorVertical.this.mDotFadeOutDelay).setDuration((long) DotsPageIndicatorVertical.this.mDotFadeOutDuration).start();
            }
        }).start();
    }

    public void onPageScrollStateChanged(int state) {
        if(this.mCurrentState != state) {
            this.mCurrentState = state;
            if(this.mDotFadeWhenIdle && state == GridViewPager.SCROLL_STATE_SETTLING) {
                if(this.mVisible) {
                    this.fadeOut();
                } else {
                    this.fadeInOut();
                }
            }
        }

        if(this.mPageChangeListener != null) {
            this.mPageChangeListener.onPageScrollStateChanged(state);
        }

    }

    public void onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels) {
        if(this.mDotFadeWhenIdle) {
            if(rowOffset != 0.0F) {
                if(!this.mVisible) {
                    this.fadeIn();
                }
            } else if(this.mCurrentState == GridViewPager.SCROLL_STATE_DRAGGING && this.mVisible) {
                this.fadeOut();
            }
        }

        if(this.mPageChangeListener != null) {
            this.mPageChangeListener.onPageScrolled(row, column, rowOffset, columnOffset, rowOffsetPixels, columnOffsetPixels);
        }

    }

    public void onPageSelected(int row, int column) {
        if(row != this.mSelectedRow) {
            this.rowChanged(row, column);
        } else if(column != this.mSelectedColumn) {
            this.columnChanged(column);
        }

        if(this.mPageChangeListener != null) {
            this.mPageChangeListener.onPageSelected(row, column);
        }

    }

    public void onAdapterChanged(GridPagerAdapter oldAdapter, GridPagerAdapter newAdapter) {
        this.mAdapter = newAdapter;
        if(this.mAdapter != null) {
            this.rowChanged(0, 0);
            this.fadeInOut();
        }

        if(this.mAdapterChangeListener != null) {
            this.mAdapterChangeListener.onAdapterChanged(oldAdapter, newAdapter);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int contentWidth;
        int totalWidth;
        if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            float maxRadius = Math.max(this.mDotRadius + this.mDotShadowRadius, this.mDotRadiusSelected + this.mDotShadowRadius);
            contentWidth = (int)Math.ceil((double)(maxRadius * 2.0F));
            contentWidth = (int)((float)contentWidth + this.mDotShadowDy);
            totalWidth = contentWidth + this.getPaddingLeft() + this.getPaddingRight();
        }

        int contentHeight;
        int totalHeight;
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            contentHeight = this.mRowCount * this.mDotSpacing;
            totalHeight = contentHeight + this.getPaddingLeft() + this.getPaddingRight();
        }

        this.setMeasuredDimension(resolveSizeAndState(totalWidth, widthMeasureSpec, 0), resolveSizeAndState(totalHeight, heightMeasureSpec, 0));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.mRowCount > 1) {
            canvas.save();
            canvas.translate(this.getWidth()/2.0F, (this.mDotRadiusSelected + this.mDotShadowRadius));

            for(int i = 0; i < this.mRowCount; ++i) {
                float radius;
                if(i == this.mSelectedRow) {
                    radius = this.mDotRadiusSelected + this.mDotShadowRadius;
                    canvas.drawCircle(this.mDotShadowDx, this.mDotShadowDy, radius, this.mDotPaintShadowSelected);
                    canvas.drawCircle(0.0F, 0.0F, this.mDotRadiusSelected, this.mDotPaint);
                } else {
                    radius = this.mDotRadius + this.mDotShadowRadius;
                    canvas.drawCircle(this.mDotShadowDx, this.mDotShadowDy, radius, this.mDotPaintShadow);
                    canvas.drawCircle(0.0F, 0.0F, this.mDotRadius, this.mDotPaint);
                }

                canvas.translate(0.0F, (float)this.mDotSpacing);
            }

            canvas.restore();
        }

    }

    public void onDataSetChanged() {
        if(this.mAdapter.getRowCount() > 0) {
            this.rowChanged(0, 0);
        }

        if(this.mAdapterChangeListener != null) {
            this.mAdapterChangeListener.onDataSetChanged();
        }

    }
}
