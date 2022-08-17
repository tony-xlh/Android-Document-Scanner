package com.dynamsoft.documentscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;

public class Utils {

    public static float intersectionOverUnion(Point[] pts1,Point[] pts2){
        Rect rect1 = getRectFromPoints(pts1);
        Rect rect2 = getRectFromPoints(pts2);
        return intersectionOverUnion(rect1,rect2);
    }

    public static float intersectionOverUnion(Rect rect1, Rect rect2){
        int leftColumnMax = Math.max(rect1.left, rect2.left);
        int rightColumnMin = Math.min(rect1.right,rect2.right);
        int upRowMax = Math.max(rect1.top, rect2.top);
        int downRowMin = Math.min(rect1.bottom,rect2.bottom);

        if (leftColumnMax>=rightColumnMin || downRowMin<=upRowMax){
            return 0;
        }

        int s1 = rect1.width()*rect1.height();
        int s2 = rect2.width()*rect2.height();
        float sCross = (downRowMin-upRowMax)*(rightColumnMin-leftColumnMax);
        return sCross/(s1+s2-sCross);
    }

    public static Rect getRectFromPoints(Point[] points){
        int left,top,right,bottom;
        left = points[0].x;
        top = points[0].y;
        right = 0;
        bottom = 0;
        for (Point point:points) {
            left = Math.min(point.x,left);
            top = Math.min(point.y,top);
            right = Math.max(point.x,right);
            bottom = Math.max(point.y,bottom);
        }
        return new Rect(left,top,right,bottom);
    }

    public static Bitmap bitmapFromFile(File file){
        byte[] b = readFile(file);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    public static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
