package com.example.blanke.recyclerviewtext;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by blanke on 15-10-30.
 */
public class ThreadPoolManager implements Runnable {
    enum Type {
        LIFO, FIFO;
    }

    private Executor mPoolExecutor;
    private int mSize, MAX_SIZE = 20, MIN_SIZE = 1;
    private Type mType;
    private LinkedList<Runnable> mThreadQueue;

    public ThreadPoolManager(int size, Type type) {
        mType = type;
        mSize = Math.max(MIN_SIZE, Math.min(size, MAX_SIZE));
        mPoolExecutor = Executors.newFixedThreadPool(mSize);
        mThreadQueue = new LinkedList<Runnable>();

        new Thread(this).start();
    }

    public synchronized void addTask(Runnable task) {
        synchronized (mThreadQueue) {
            if (task != null) {
                mThreadQueue.add(task);
            }
            mThreadQueue.notify();
        }
    }

    /**
     * 根据先进先出 ，后进先出 规则 出队 执行任务
     *
     * @return
     */
    public synchronized Runnable getTask() {
        if (mThreadQueue.size() == 0) {
            return null;
        }
        if (mType == Type.FIFO) {
            return mThreadQueue.pollFirst();
        } else if (mType == Type.LIFO) {
            return mThreadQueue.pollLast();
        }
        return null;
    }

    public void run() {
        for (; ; ) {
            Runnable taskItem = getTask();
            if (taskItem == null) {
                synchronized (mThreadQueue) {
                    try {
                        mThreadQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mPoolExecutor.execute(taskItem);
            }
        }
    }
}
