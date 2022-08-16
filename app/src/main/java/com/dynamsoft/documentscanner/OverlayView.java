package com.dynamsoft.documentscanner;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


public class OverlayView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder = null;
    private Point[] points = null;
    private Paint stroke = new Paint();
    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);

        if(surfaceHolder == null) {
            // Get surfaceHolder object.
            surfaceHolder = getHolder();
            // Add this as surfaceHolder callback object.
            surfaceHolder.addCallback(this);
        }
        stroke.setColor(Color.GREEN);
        // Set the parent view background color. This can not set surfaceview background color.
        this.setBackgroundColor(Color.TRANSPARENT);

        // Set current surfaceview at top of the view tree.
        this.setZOrderOnTop(true);

        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public void setStroke(Paint paint){
        stroke = paint;
    }

    public Point[] getPoints(){
        return points;
    }

    public void setPoints(Point[] points){
        this.points = points;
        drawPolygon();
    }

    public void drawPolygon()
    {
        try {
            // Get and lock canvas object from surfaceHolder.
            Canvas canvas = surfaceHolder.lockCanvas();
            // Clear canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            for (int index = 0; index <= points.length - 1; index++) {
                if (index == points.length - 1) {
                    canvas.drawLine(points[index].x,points[index].y,points[0].x,points[0].y,stroke);
                }else{
                    canvas.drawLine(points[index].x,points[index].y,points[index+1].x,points[index+1].y,stroke);
                }
            }

            // Unlock the canvas object and post the new draw.
            surfaceHolder.unlockCanvasAndPost(canvas);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
