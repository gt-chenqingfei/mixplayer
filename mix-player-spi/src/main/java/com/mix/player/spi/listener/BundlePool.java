package com.mix.player.spi.listener;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class BundlePool {
    private static final int POOL_SIZE = 3;
    public static final String BYTE_DATA = "byte_data";
    public static final String INT_DATA = "int_data";
    public static final String BOOL_DATA = "bool_data";
    public static final String FLOAT_DATA = "float_data";
    public static final String LONG_DATA = "long_data";
    public static final String DOUBLE_DATA = "double_data";
    public static final String STRING_DATA = "string_data";

    public static final String SERIALIZABLE_DATA = "serializable_data";
    public static final String SERIALIZABLE_EXTRA_DATA = "serializable_extra_data";

    public static final String INT_ARG1 = "int_arg1";
    public static final String INT_ARG2 = "int_arg2";
    public static final String INT_ARG3 = "int_arg3";
    public static final String INT_ARG4 = "int_arg4";

    private static List<Bundle> mPool;

    static {
        mPool = new ArrayList<>();
        for (int i = 0; i < POOL_SIZE; i++)
            mPool.add(new Bundle());
    }

    public synchronized static Bundle obtain() {
        for (int i = 0; i < POOL_SIZE; i++) {
            if (mPool.get(i).isEmpty()) {
                return mPool.get(i);
            }
        }
        return new Bundle();
    }
}
