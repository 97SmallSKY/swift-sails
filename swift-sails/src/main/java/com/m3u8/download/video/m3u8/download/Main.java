package com.m3u8.download.video.m3u8.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        // 创建多个实例
        MyClass instance1 = new MyClass("Instance 1");
        MyClass instance2 = new MyClass("Instance 2");

        // 创建观察者
        Observer observer = new MyObserver();

        // 将观察者添加到每个实例中
        instance1.addObserver(observer);
        instance2.addObserver(observer);

        // 启动多个线程
        new Thread(instance1).start();
        new Thread(instance2).start();
    }
}

// 实例类
class MyClass extends Observable implements Runnable {
    private String name;
    private ConcurrentHashMap<String, Integer> data;

    public MyClass(String name) {
        this.name = name;
        this.data = new ConcurrentHashMap<>();
    }

    public void run() {
        while (true) {
            // 模拟数据更新
            updateData();

            // 通知观察者
            setChanged();
            notifyObservers(data);

            // 等待一段时间
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateData() {
        // 在线程安全的数据结构中更新数据
        data.put("key", data.getOrDefault("key", 0) + 1);
    }
}

// 观察者类
class MyObserver implements Observer {
    public void update(Observable obj, Object arg) {
        ConcurrentHashMap<String, Integer> data = (ConcurrentHashMap<String, Integer>) arg;
        System.out.println(Thread.currentThread().getName() + ": " + data);
    }
}
