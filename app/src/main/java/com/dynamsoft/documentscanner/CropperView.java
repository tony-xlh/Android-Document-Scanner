package com.dynamsoft.documentscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class CropperView extends OverlayView {

    private Bitmap background;
    public CropperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawBackground();
        super.surfaceCreated(surfaceHolder);
        drawCorners();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void setPoints(Point[] points) {
        drawBackground();
        super.setPoints(points);
        drawCorners();
    }

    public void setBackgroundImage(Bitmap bitmap) {
        //BitmapDrawable bmp = new BitmapDrawable(context.getResources(), bitmap);
        //this.setBackground(bmp);
        background = bitmap;

    }

    private void drawBackground(){
        Log.d("DDN","draw background");
        Canvas canvas = getSurfaceHolder().lockCanvas();
        if (canvas == null) {
            return;
        }

        //Bitmap resizedBitmap = Bitmap.createBitmap(background, 0, 0, background.getWidth(), background.getHeight(), matrix, false);
        //canvas.drawBitmap(resizedBitmap,0,0,getStroke());
        Rect src = new Rect(0,0,background.getWidth(),background.getHeight());
        Rect tgt = new Rect(0,0,canvas.getWidth(),canvas.getHeight());
        canvas.drawBitmap(background,src,tgt,getStroke());
        canvas.drawCircle(10,10,5,getStroke());
        // Unlock the canvas object and post the new draw.
        getSurfaceHolder().unlockCanvasAndPost(canvas);
        Log.d("DDN","draw background done");
    }

    public void drawCorners()
    {
        // Get and lock canvas object from surfaceHolder.
        Canvas canvas = getSurfaceHolder().lockCanvas();

        if (canvas == null) {
            return;
        }
        Point[] points = getPoints();

        for (int i = 0; i < 4; i++) {
            canvas.drawCircle(points[i].x,points[i].y,5,getStroke());
        }

        // Unlock the canvas object and post the new draw.
        getSurfaceHolder().unlockCanvasAndPost(canvas);
        Log.d("DDN","draw corners complete.");

    }
}
