package com.books.dubbo.demo.provider.mydispatcher;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.threadlocal.NamedInternalThreadFactory;
import org.apache.dubbo.common.threadpool.ThreadPool;
import org.apache.dubbo.common.threadpool.support.AbortPolicyWithReport;

public class MyThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(CommonConstants.THREAD_NAME_KEY, CommonConstants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(CommonConstants.CORE_THREADS_KEY, CommonConstants.DEFAULT_CORE_THREADS);
        int threads = url.getParameter(CommonConstants.THREADS_KEY, Integer.MAX_VALUE);
        int queues = url.getParameter(CommonConstants.QUEUES_KEY, CommonConstants.DEFAULT_QUEUES);
        int alive = url.getParameter(CommonConstants.ALIVE_KEY, CommonConstants.DEFAULT_ALIVE);
        return new ThreadPoolExecutor(cores, threads, alive, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<Runnable>() :
                        (queues < 0 ? new LinkedBlockingQueue<Runnable>()
                                : new LinkedBlockingQueue<Runnable>(queues)),
                new NamedInternalThreadFactory(name, true), new AbortPolicyWithReport(name, url));
    }
}