package com.kairong.three3d.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolExecutorUtil {

    //创建基本线程池
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 7, 20, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            initThreadPoolExecutor();
        }
        return threadPoolExecutor;
    }

    private static void initThreadPoolExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(5, 7, 20, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50));
    }
}
