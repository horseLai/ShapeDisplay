package com.example.shapeimagedisplay.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.shapeimagedisplay.R;

/**
 * Created by laixiaolong on 2018/1/5.
 */

public class ShapeImageView extends android.support.v7.widget.AppCompatImageView
{

    private Path mPath;
    private RectF mRectF;
    private float[] mRadii;
    private Paint mPaint;
    private RectF mLayerRectF;
    private float mRadius;
    private float mLeftTopRadius;
    private float mLeftBottomRadius;
    private float mRightTopRadius;
    private float mRightBottomRadius;
    private boolean mAsCircle;
    private float mBorderWidth;
    private Paint mBorderPaint;
    private int mBorderColor;
    private RectF mBorderRectF;
    private Region mAreaCanTouch;


    public float getRadius()
    {
        return mRadius;
    }

    public void setRadius(float radius)
    {
        this.mRadius = radius;
    }

    public float getLeftTopRadius()
    {
        return mLeftTopRadius;
    }

    public void setLeftTopRadius(float leftTopRadius)
    {
        this.mLeftTopRadius = leftTopRadius;
    }

    public float getLeftBottomRadius()
    {
        return mLeftBottomRadius;
    }

    public void setLeftBottomRadius(float leftBottomRadius)
    {
        this.mLeftBottomRadius = leftBottomRadius;
    }

    public float getRightTopRadius()
    {
        return mRightTopRadius;
    }

    public void setRightTopRadius(float rightTopRadius)
    {
        this.mRightTopRadius = rightTopRadius;
    }

    public float getRightBottomRadius()
    {
        return mRightBottomRadius;
    }

    public void setRightBottomRadius(float rightBottomRadius)
    {
        this.mRightBottomRadius = rightBottomRadius;
    }

    public boolean isAsCircle()
    {
        return mAsCircle;
    }

    public void setAsCircle(boolean asCircle)
    {
        this.mAsCircle = asCircle;
    }

    public ShapeImageView(Context context)
    {
        this(context, null);
    }

    public ShapeImageView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        obtainAttributes(context, attrs, defStyleAttr);

        initialize();

        setScaleType(ScaleType.CENTER_CROP);
    }

    private void obtainAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView, defStyleAttr, 0);

        try {

            mRadius = typedArray.getDimension(R.styleable.ShapeImageView_radius, 0);
            mLeftTopRadius = typedArray.getDimension(R.styleable.ShapeImageView_radius_left_top, 0);
            mLeftBottomRadius =
                    typedArray.getDimension(R.styleable.ShapeImageView_radius_left_bottom, 0);
            mRightTopRadius =
                    typedArray.getDimension(R.styleable.ShapeImageView_radius_right_top, 0);
            mRightBottomRadius =
                    typedArray.getDimension(R.styleable.ShapeImageView_radius_right_bottom, 0);
            mBorderWidth = typedArray.getDimension(R.styleable.ShapeImageView_borderWidth, 0);
            mAsCircle = typedArray.getBoolean(R.styleable.ShapeImageView_asCircle, false);
            mBorderColor = typedArray.getColor(R.styleable.ShapeImageView_borderColor, Color.WHITE);

        } finally {
            typedArray.recycle();
        }

    }

    private float[] decideRadii()
    {

        if (mRadius <= 0) {
            // 顺时针方向上每个角的圆弧半径，(x,y)对
            return new float[]{ // 顺时针方向上每个角的圆弧半径，(x,y)对
                    mLeftTopRadius - mBorderWidth, mLeftTopRadius, // top-left
                    mRightTopRadius, mRightTopRadius, // top-right
                    mLeftBottomRadius, mLeftBottomRadius, // bottom-left
                    mRightBottomRadius, mRightBottomRadius// bottom-right
            };
        } else {
            return new float[]{ // 顺时针方向上每个角的圆弧半径，(x,y)对
                    mRadius, mRadius, // top-left
                    mRadius, mRadius, // top-right
                    mRadius, mRadius, // bottom-left
                    mRadius, mRadius// bottom-right
            };
        }
    }

    private void initialize()
    {

        if (!mAsCircle) {
            mRadii = decideRadii();
        }

        mLayerRectF = new RectF();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mBorderColor);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        mPath = new Path();
        mRectF = new RectF();
        mBorderRectF = new RectF();

        // region that can be touch
        Region regionClip =
                new Region((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom);
        mAreaCanTouch = new Region();
        regionClip.setPath(mPath, regionClip);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // only touch in our region
        if (!mAreaCanTouch.contains((int) event.getX(), (int) event.getY())) return false;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mAsCircle) { // 圆角时把View设定为正方形
            final int sizeW = MeasureSpec.getSize(widthMeasureSpec);
            final int sizeH = MeasureSpec.getSize(heightMeasureSpec);
            final int size = Math.min(sizeH, sizeW);
            setMeasuredDimension(size, size);
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mRectF.left = getPaddingLeft();
        mRectF.top = getPaddingTop();
        mRectF.right = w - getPaddingRight();
        mRectF.bottom = h - getPaddingBottom();

        // strok region , - 2.5f是为了覆盖角落圆角的一点多余的地方
        float r = Math.round(mBorderWidth / 2.0f) - 2.5f;

        mBorderRectF.left = mRectF.left + r;
        mBorderRectF.right = mRectF.right - r;
        mBorderRectF.top = mRectF.top + r;
        mBorderRectF.bottom = mRectF.bottom - r;

    }


    @Override
    protected void onDraw(Canvas canvas)
    {

        // 保存图层
        mLayerRectF.set(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.saveLayer(mLayerRectF, null, Canvas.ALL_SAVE_FLAG);

        // Destination
        super.onDraw(canvas);

        // draw Source
        //fill
        drawSourceFill(canvas);

        // strok
        if (mBorderWidth > 0) {
            drawBorder(canvas);
        }


        canvas.restore();

    }

    public void drawSourceFill(Canvas canvas)
    {
        mPath.rewind();
        if (mAsCircle) {
            mPath.addCircle(mRectF.centerX(), mRectF.centerY(), Math.min(mRectF.width(), mRectF.height()) / 2.0f, Path.Direction.CW);
        } else {
            mPath.addRoundRect(mRectF, mRadii, Path.Direction.CW);
        }
        canvas.drawPath(mPath, mPaint);
    }

    private void drawBorder(Canvas canvas)
    {
        mPath.rewind();
        if (mAsCircle) {
            mPath.addCircle(mBorderRectF.centerX(), mBorderRectF.centerY(), Math.min(mBorderRectF.width(), mBorderRectF.height()) / 2.0f, Path.Direction.CW);
        } else {
            mPath.addRoundRect(mBorderRectF, mRadii, Path.Direction.CW);
        }

        canvas.drawPath(mPath, mBorderPaint);
    }
}
