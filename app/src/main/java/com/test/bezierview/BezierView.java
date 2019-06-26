package com.test.bezierview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BezierView extends View {

    private static final float C = 0.551915024494f;     // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置

    private Paint mPaint;
    private int mCenterX, mCenterY;
    private PointF mCenter = new PointF(0, 0);
    private float mCircleRadius = 200;                  // 圆的半径
    private float mDifference = mCircleRadius * C;        // 圆形的控制点与数据点的差值

    private float[] mData = new float[8];               // 顺时针记录绘制圆形的四个数据点
    private float[] mCtrl = new float[16];              // 顺时针记录绘制圆形的八个控制点

    private float mDuration = 1000;                     // 变化总时长
    private float mCurrent = 0;                         // 当前已进行时长
    private float mCount = 100;                         // 将时长总共划分多少份
    private float mPiece = mDuration / mCount;            // 每一份的时长

    private Path path;

    private Paint fuzhuPaint;

    public BezierView(Context context) {
        super(context);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        fuzhuPaint = new Paint();
        fuzhuPaint.setColor(Color.RED);
        fuzhuPaint.setStrokeWidth(5);
        fuzhuPaint.setStyle(Paint.Style.STROKE);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
//        mPaint.setTextSize(60);
        // 初始化数据点
        mData[0] = 0;
        mData[1] = mCircleRadius;

        mData[2] = mCircleRadius;
        mData[3] = 0;

        mData[4] = 0;
        mData[5] = -mCircleRadius;

        mData[6] = -mCircleRadius;
        mData[7] = 0;

        // 初始化控制点
        mCtrl[0] = mData[0] + mDifference;
        mCtrl[1] = mData[1];

        mCtrl[2] = mData[2];
        mCtrl[3] = mData[3] + mDifference;

        mCtrl[4] = mData[2];
        mCtrl[5] = mData[3] - mDifference;

        mCtrl[6] = mData[4] + mDifference;
        mCtrl[7] = mData[5];

        mCtrl[8] = mData[4] - mDifference;
        mCtrl[9] = mData[5];

        mCtrl[10] = mData[6];
        mCtrl[11] = mData[7] - mDifference;

        mCtrl[12] = mData[6];
        mCtrl[13] = mData[7] + mDifference;

        mCtrl[14] = mData[0] - mDifference;
        mCtrl[15] = mData[1];
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawCoordinateSystem(canvas);//坐标线
        canvas.translate(mCenterX, mCenterY); // 将坐标系移动到画布中央
        canvas.scale(1,-1);                 // 翻转Y轴

//        drawAuxiliaryLine(canvas);//辅助线

        // 绘制贝塞尔曲线
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8);


        path.reset();
        //起始点S
        path.moveTo(mData[0],mData[1]);
        //控制点A，控制点B，结束点E
        path.cubicTo(mCtrl[0],  mCtrl[1],  mCtrl[2],  mCtrl[3],     mData[2], mData[3]);
        path.cubicTo(mCtrl[4],  mCtrl[5],  mCtrl[6],  mCtrl[7],     mData[4], mData[5]);
        path.cubicTo(mCtrl[8],  mCtrl[9],  mCtrl[10], mCtrl[11],    mData[6], mData[7]);
        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15],    mData[0], mData[1]);

        canvas.drawPath(path, mPaint);

        mCurrent += mPiece;
        if (mCurrent < mDuration){

            mData[1] -= 118/mCount;
            mCtrl[7] += 80/mCount;
            mCtrl[9] += 80/mCount;

            mCtrl[4] -= 24/mCount;
            mCtrl[10] += 24/mCount;

            postInvalidateDelayed((long) mPiece);
        }

        //二阶曲线，一个控制点和一个结束点
        //path.quadTo(control.x,control.y,end.x,end.y);

    }

    private void drawAuxiliaryLine(Canvas canvas) {

        // 绘制数据点和控制点
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(20);

        //点
        for (int i = 0; i < mData.length; i+=2) {
            canvas.drawPoint(mData[i],mData[i+1],mPaint);
        }

        for (int i = 0; i < mCtrl.length; i+=2) {
            canvas.drawPoint(mCtrl[i],mCtrl[i+1],mPaint);
        }


        // 绘制辅助线
        mPaint.setStrokeWidth(4);

        for (int i=2, j=2; i<8; i+=2, j+=4){
            canvas.drawLine(mData[i],mData[i+1],mCtrl[j],mCtrl[j+1],mPaint);
            canvas.drawLine(mData[i],mData[i+1],mCtrl[j+2],mCtrl[j+3],mPaint);
        }
        canvas.drawLine(mData[0],mData[1],mCtrl[0],mCtrl[1],mPaint);
        canvas.drawLine(mData[0],mData[1],mCtrl[14],mCtrl[15],mPaint);


    }

    private void drawCoordinateSystem(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenterX,mCenterY);
        canvas.scale(1f,-1f);
        canvas.drawLine(0,2000,0,-2000,fuzhuPaint);
        canvas.drawLine(2000,0,-2000,0,fuzhuPaint);
        canvas.restore();
    }
}
