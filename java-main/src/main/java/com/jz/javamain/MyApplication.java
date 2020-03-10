package com.jz.javamain;

import com.jz.javamain.jmx.Hello;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyApplication {
    /**
     * for jmx
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {
        // Get the Platform MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Construct the ObjectName for the Hello MBean we will register
        ObjectName mbeanName = new ObjectName("com.jz.javamain.jmx:type=Hello");

        // Create the Hello World MBean
        Hello mbean = new Hello();

        // Register the Hello World MBean
        mbs.registerMBean(mbean, mbeanName);

        // Wait forever
        System.out.println("Waiting for incoming requests...");


        new Thread(new Runnable(){
            @Override
            public void run(){
                Random random = new Random();
                while(true){
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(10));
                    } catch(Exception e){}
                    mbean.setCacheSize(random.nextInt(10) + mbean.getCacheSize());
                }
            }
        }).start();
        Thread.sleep(Long.MAX_VALUE);
    }


/*    public static void main(String[] args) {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Car.print();
//            System.out.println(new Date().toInstant());
        }

    }*/
}
