package com.ormedia.qrscanner.barcode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by rin on 2/2/18.
 */

class DrawCaptureRect extends View {
    private int mcolorfill;
    private int mleft, mtop, mwidth, mheight;

    public DrawCaptureRect(Context context, int left, int top, int width, int height, int colorfill) {
        super(context);
        //  Auto-generated constructor stub
        this.mcolorfill = colorfill;
        this.mleft = left;
        this.mtop = top;
        this.mwidth = width;
        this.mheight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //  Auto-generated method stub
        Paint mpaint = new Paint();
        mpaint.setColor(mcolorfill);
        mpaint.setStyle(Paint.Style.FILL);
        mpaint.setStrokeWidth(1.0f);
        canvas.drawLine(mleft, mtop, mleft + mwidth, mtop, mpaint);
        canvas.drawLine(mleft + mwidth, mtop, mleft + mwidth, mtop + mheight, mpaint);
        canvas.drawLine(mleft, mtop, mleft, mtop + mheight, mpaint);
        canvas.drawLine(mleft, mtop + mheight, mleft + mwidth, mtop + mheight, mpaint);
        super.onDraw(canvas);
    }

}