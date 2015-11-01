package com.example.blanke.recyclerviewtext.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

/**
 * Created by blanke on 15-10-31.
 */
public class ImageUtils {
    private static LruCache<String, Bitmap> mImgLruCache;
    private static DiskLruCache mImgDiskLruCache;

    static {
        mImgLruCache = new LruCache<String, Bitmap>(50 * 1024 * 1024);
    }

    public static synchronized Bitmap getChche(Context context, String url) {
        try {
            mImgDiskLruCache = DiskLruCache.open(context.getCacheDir(), 1, 1, 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String hashUrl = MD5(url);
        Bitmap bitmap = mImgLruCache.get(hashUrl);
        if (bitmap == null) {
            try {
                DiskLruCache.Snapshot snapshot = mImgDiskLruCache.get(hashUrl);
                if (snapshot != null) {
                    InputStream is = snapshot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(is);
                } else {
                    bitmap = loadBitmapFromNet(url);
                    DiskLruCache.Editor edit = mImgDiskLruCache.edit(hashUrl);
                    OutputStream os = edit.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    edit.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImgLruCache.put(hashUrl, bitmap);
        }
        try {
            mImgDiskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获得网络图片Bitmap
     *
     * @param imageUrl
     * @return
     */
    private static Bitmap loadBitmapFromNet(String imageUrlStr) {
        Bitmap bitmap = null;
        URL imageUrl = null;

        if (imageUrlStr == null || imageUrlStr.length() == 0) {
            return null;
        }
        try {
            imageUrl = new URL(imageUrlStr);
            URLConnection conn = imageUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int length = conn.getContentLength();
            if (length != -1) {
                byte[] imgData = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) != -1) {
                    System.arraycopy(temp, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            }
        } catch (IOException e) {
            return null;
        }

        return bitmap;
    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
