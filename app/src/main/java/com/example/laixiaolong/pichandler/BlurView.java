package com.example.laixiaolong.pichandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BlurView extends View
{

    private Paint mBlurPaint;
    private Bitmap bitmapSrc;
    private Bitmap bitmapDest;
    private Paint mPaintDest;
    private Path mPath;
    private RectF mRectF;

    public BlurView(Context context)
    {
        super(context);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    {
        mBlurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlurPaint.setMaskFilter(new BlurMaskFilter(50.0f, BlurMaskFilter.Blur.OUTER));
        mBlurPaint.setStyle(Paint.Style.STROKE);

        mPaintDest = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDest.setStrokeJoin(Paint.Join.ROUND);

           /* try {
                ExifInterface exifInterface = new ExifInterface("");
               exifInterface.getAttribute(ExifInterface.TAG_ISO);

            } catch (IOException e) {
                e.printStackTrace();
            }*/
        bitmapSrc = BitmapFactory.decodeResource(getResources(), R.raw.notify_icon);

        bitmapDest = BitmapFactory.decodeResource(getResources(), R.raw.bg1080);


        mPath = new Path();
        mRectF = new RectF();

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        mBlurPaint.setColor(Color.RED);
        // axis y
        canvas.drawLine(0, -getHeight() / 2, 0, getHeight() / 2, mBlurPaint);
        // axis x
        canvas.drawLine(-getWidth() / 2, 0, getWidth() / 2, 0, mBlurPaint);


        mRectF.set(-50, -60, 0, 0);
        // CCW: 逆时针绘制    CW:顺时针绘制
        mPath.addRect(mRectF, Path.Direction.CCW);
        mPath.moveTo(0, 0); //移动到下一个动作的起点，不影响之前操作

        mPath.lineTo(0, 100);
        mPath.lineTo(100, 0);
        mPath.setLastPoint(200, 0);  // 设置终点，之前的操作会受牵连，并影响之后的操作


        mRectF.set(200, 0, 300, 100);
        mPath.arcTo(mRectF, 0, 90);

        mPath.rLineTo(0, 100);

        mRectF.set(10, 100, 200, 300);

        mPath.addRoundRect(mRectF, new float[]{ // 顺时针方向上每个角的圆弧半径，(x,y)对
                15, 15, // top-left
                15, 15, // top-right
                15, 15, // bottom-left
                0, 0    // bottom-right
        }, Path.Direction.CW);

        mBlurPaint.setColor(Color.BLACK);
        canvas.drawPath(mPath, mBlurPaint);

    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);


    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (bitmapSrc != null && !bitmapSrc.isRecycled()) {
            bitmapSrc.recycle();
        }
        if (bitmapDest != null && !bitmapDest.isRecycled()) {
            bitmapDest.recycle();
        }

        bitmapDest = null;
        bitmapSrc = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        return super.dispatchTouchEvent(event);
    }
}