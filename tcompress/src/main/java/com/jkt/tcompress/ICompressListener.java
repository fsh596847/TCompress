package com.jkt.tcompress;

/**
 * Created by Allen at 2017/6/8 16:51
 */
public interface ICompressListener<T> {
    void onCompressFinish(boolean success, T t);
}
