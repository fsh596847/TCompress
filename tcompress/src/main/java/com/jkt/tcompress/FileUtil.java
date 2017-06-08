package com.jkt.tcompress;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by Allen at 2017/6/5 16:30
 */
public class FileUtil {
    public static File createTempFile(Context context, Uri uri) {
        File tempFile = null;
        try {
            String fileName = getFileName(context, uri);
            String[] pAs = getPrefixAndSuffix(fileName);
            Log.i("prefix",pAs[0]+"--------------"+pAs[1]);
            tempFile = File.createTempFile(pAs[0], pAs[1]);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtil.copyBytes(inputStream, outputStream);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            tempFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String[] getPrefixAndSuffix(String fileName) {
        String prefix = fileName;
        String suffix = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            prefix = fileName.substring(0, i);
            suffix = fileName.substring(i);
        }

        return new String[]{prefix, suffix};
    }

    public static String getFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,###.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
