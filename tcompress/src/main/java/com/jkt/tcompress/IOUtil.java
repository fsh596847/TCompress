package com.jkt.tcompress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Allen at 2017/6/5 16:53
 */
public class IOUtil {
    static long copyBytes(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 8];
        long count = 0;
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
