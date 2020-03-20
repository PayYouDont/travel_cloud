package com.gospell.travel.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.gospell.travel.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class BitmapUtil {
    /**
    * @Author peiyongdong
    * @Description ( 给bitmap添加一个椭圆形边框并返回指定大小bitmap )
    * @Date 11:22 2020/3/20
    * @Param [bitmap, outWidth, outHeight, radius, boarder]
    * @return android.graphics.Bitmap
    **/
    public static Bitmap addBitmapBorder(Bitmap bitmap,int outWidth, int outHeight, int radius, float boarder){
        //创建输出的bitmap
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        Canvas canvas = new Canvas(desBitmap);
        RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
        if(bitmap!=null){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float widthScale = outWidth * 1f / width;
            float heightScale = outHeight * 1f / height;
            Matrix matrix = new Matrix();
            matrix.setScale(widthScale, heightScale);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //创建着色器
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            //给着色器配置matrix
            bitmapShader.setLocalMatrix(matrix);
            paint.setShader(bitmapShader);
            //创建矩形区域并且预留出border
            //把传入的bitmap绘制到圆角矩形区域内
            canvas.drawRoundRect(rect, radius, radius, paint);
        }
        if (boarder > 0) {
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(Color.parseColor("#7C7C7C"));
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(boarder);
            canvas.drawRoundRect(rect, radius, radius, boarderPaint);
        }
        return desBitmap;
    }
    /**
    * @Author peiyongdong
    * @Description ( 根据文件路径，生成对应的文件类型图标bitmap )
    * @Date 11:24 2020/3/20
    * @Param [context, filePath, outWidth, outHeight, radius, boarder]
    * @return android.graphics.Bitmap
    **/
    public static Bitmap parseToBitmap(Context context, String filePath, int outWidth, int outHeight, int radius, int boarder){
        try {
            //创建输出的bitmap
            Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
            //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
            Canvas canvas = new Canvas(desBitmap);
            RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
            Bitmap bitmap = null;
            if(!filePath.equals ("")){
                File file = new File (filePath);
                if(file.exists ()){
                    FileInputStream in = new FileInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(in);
                }
                if(bitmap == null){
                    bitmap =  createBitmapByPath (context,filePath);
                }
            }else{
                Drawable drawable = context.getDrawable (R.drawable.ic_app_logo);
                bitmap = crateByDrawable (drawable);
            }
           return addBitmapBorder (bitmap,outWidth,outHeight,radius,boarder);
        }catch (Exception e){
            Log.e ("FileUtil",e.getMessage (),e);
        }
        return null;
    }
    /**
    * @Author peiyongdong
    * @Description ( 根据文件路径生成其对应的文件类型图标bitmap )
    * @Date 11:25 2020/3/20
    * @Param [context, path]
    * @return android.graphics.Bitmap
    **/
    public static Bitmap createBitmapByPath(Context context,String path){
        Bitmap bitmap = null;
        if(path.indexOf (".")!=-1){
            String suffix = path.substring (path.lastIndexOf ("."));
            int resourceId = getDrawResourceId (suffix);
            if(resourceId!=-1){
                Drawable drawable = context.getDrawable (resourceId);
                bitmap = crateByDrawable (drawable);
            }
        }
        return bitmap;
    }
    private static int getDrawResourceId(String suffix){
        if(suffix.indexOf ("doc")!=-1){
            return R.drawable.ic_file_doc;
        }else if(suffix.indexOf ("xls")!=-1){
            return R.drawable.ic_file_xls;
        }else if(suffix.indexOf ("ppt")!=-1){
            return R.drawable.ic_file_ppt;
        }else if(suffix.indexOf ("txt")!=-1){
            return R.drawable.ic_file_txt;
        }
        return -1;
    }
    private static Bitmap crateByDrawable(Drawable drawable){
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imgBytes = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}
