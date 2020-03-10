package com.jz.javamain.jmx;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class Hello extends NotificationBroadcasterSupport implements HelloMBean{
    private final String name = "Reginald";
    private static final int DEFAULT_CACHE_SIZE = 200;
    private int cacheSize = DEFAULT_CACHE_SIZE;
    //增加序号
    private long sequenceNumber = 1;

    @Override
    public void sayHello() {
        System.out.println("hello, world");
    }

    @Override
    public int add(int x, int y) {
        return x + y;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public synchronized void setCacheSize(int size) {
        int oldSize = this.cacheSize;
        this.cacheSize = size;
        System.out.println("Cache size now " + this.cacheSize);


        // 发送通知
        Notification n = new AttributeChangeNotification(this,
                sequenceNumber++,
                System.currentTimeMillis(),
                "CacheSize changed",
                "CacheSize",
                "int",
                oldSize, this.cacheSize);
        sendNotification(n);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[]{
                AttributeChangeNotification.ATTRIBUTE_CHANGE
        };

        String name = AttributeChangeNotification.class.getName();
        String description = "An attribute of this MBean has changed";
        MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[]{info};
    }
}
