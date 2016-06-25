package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by olesya on 08-Feb-16.
 */
public class CompassView extends View {
    private Paint paint;
    private Path path;
    private int height, width, left, top;
    public float angle;

    public CompassView(Context context) {
        super(context);
        init(null, 0, context);
    }


    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0,context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle,context);
    }

    //Initialization function
    private void init(AttributeSet attrs, int defStyle, Context context)
    {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();
        angle = -90;

    }

    public void setAngle(int angle){
        this.angle = angle;
    }
    //Function to treat size changing
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - (getPaddingLeft() + getPaddingRight());
        height = h - (getPaddingTop() + getPaddingBottom());

    }

    //Function to draw the screen
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();

        Rect rect = canvas.getClipBounds();

        height = rect.bottom-rect.top;
        width = rect.right-rect.left;
        left = rect.left;
        top = rect.top;

        if(height>width){
            top+=(height-width)/2;
            height=width;
        }
        if(width>height){
            left+=(width-height)/2;
            width=height;
        }

        float centerwidth = width/2f;
        float centerheight = height/2f;

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float startX = left+(float)(centerwidth+Math.cos(deg2rad(angle))*width/3.0);
        float startY = top+(float)(centerheight+Math.sin(deg2rad(angle))*height/3.0);


        path.moveTo(
                startX,
                startY);
        path.lineTo(
                left+(float)(centerwidth+Math.cos(deg2rad(angle+140))*width/4.0),
                top+(float)(centerheight+Math.sin(deg2rad(angle+140))*height/4.0));
        path.lineTo(
                left+(float)centerwidth,
                top+(float)centerheight
        );
        path.lineTo(
                left+(float)(centerwidth+Math.cos(deg2rad(angle+220))*width/4.0),
                top+(float)(centerheight+Math.sin(deg2rad(angle+220))*height/4.0)
        );

        path.lineTo(
                startX,
                startY
        );

        canvas.drawPath(path, paint);

    }

    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
/* :: This function converts decimal degrees to radians : */
/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
}
