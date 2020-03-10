package com.jz;

import com.jz.javaagent.jmx.HelloMBean;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MyApplication {
    /**
     * for jmx
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 9999;
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL serviceURL = new JMXServiceURL(url);
        final JMXConnector connector;
        Map<String, Object> environment = new HashMap<>();
        environment.put(JMXConnector.CREDENTIALS, new String[]{"admin", "password"});
        connector = JMXConnectorFactory.connect(serviceURL, environment);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
//        print(connection);
//        addNotificationListener(connection);
//        getAndSet(connection);
        proxy(connection);
    }

    private static void proxy(MBeanServerConnection connection) throws Exception {
        ObjectName objectName = new ObjectName("com.jz.javamain.jmx:type=Hello");
        HelloMBean mbeanProxy =
                JMX.newMBeanProxy(connection, objectName, HelloMBean.class, true);
        System.out.println("Get Value: " + mbeanProxy.getCacheSize());
        mbeanProxy.setCacheSize(100);
        mbeanProxy.sayHello();
        int result = mbeanProxy.add(1, 9);
        System.out.println("1 + 9 = " + result);
    }

    private static void getAndSet(MBeanServerConnection connection) throws Exception {
        ObjectName objectName = new ObjectName("com.jz.javamain.jmx:type=Hello");
        Object cacheSize = connection.getAttribute(objectName, "CacheSize");
        System.out.println("Get Value: " + cacheSize);

        connection.setAttribute(objectName, new Attribute("CacheSize", 100));

        connection.invoke(objectName, "sayHello", null, null);
        Object result = connection.invoke(objectName, "add",
                new Object[]{1, 9},
                new String[]{int.class.getCanonicalName(), int.class.getCanonicalName()});
        System.out.println("1 + 9 = " + result);
    }

    private static void addNotificationListener(MBeanServerConnection connection) throws Exception {
        connection.addNotificationListener(new ObjectName("com.jz.javamain.jmx:type=Hello"), new NotificationListener() {
            @Override
            public void handleNotification(Notification notification, Object handback) {
                System.out.println("\nReceived notification:");
                System.out.println("\tClassName: " + notification.getClass().getName());
                System.out.println("\tSource: " + notification.getSource());
                System.out.println("\tType: " + notification.getType());
                System.out.println("\tMessage: " + notification.getMessage());
                if (notification instanceof AttributeChangeNotification) {
                    AttributeChangeNotification acn =
                            (AttributeChangeNotification) notification;
                    System.out.println("\tAttributeName: " + acn.getAttributeName());
                    System.out.println("\tAttributeType: " + acn.getAttributeType());
                    System.out.println("\tNewValue: " + acn.getNewValue());
                    System.out.println("\tOldValue: " + acn.getOldValue());
                }
            }
        }, null, null);
        TimeUnit.SECONDS.sleep(100);
    }

    private static void print(MBeanServerConnection connection) throws Exception {
        Set<ObjectName> objectNames = connection.queryNames(new ObjectName("com.jz.javamain.jmx:type=Hello"), null);
        for (ObjectName objectName : objectNames) {
            System.out.println("========" + objectName + "========");
            MBeanInfo mBeanInfo = connection.getMBeanInfo(objectName);
            System.out.println("[Attributes]");
            for (MBeanAttributeInfo attr : mBeanInfo.getAttributes()) {
                Object value = null;
                try {
                    value = attr.isReadable() ? connection.getAttribute(objectName, attr.getName()) : "";
                } catch (Exception e) {
                    value = e.getMessage();
                }
                System.out.println(attr.getName() + ":" + value);
            }
            System.out.println("[Operations]");
            for (MBeanOperationInfo oper : mBeanInfo.getOperations()) {
                System.out.println(oper.getName() + ":" + oper.getDescription());
            }
            System.out.println("[Notifications]");
            for (MBeanNotificationInfo notice : mBeanInfo.getNotifications()) {
                System.out.println(notice.getName() + ":" + notice.getDescription());
            }
        }
    }
/*    public static void main(String[] args) throws Exception {
        //获取当前系统中所有 运行中的 虚拟机
        System.out.println("running JVM start ");
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            //然后加载 agent.jar 发送给该虚拟机
            System.out.println(vmd.displayName());
            if (vmd.displayName().endsWith("com.jz.javamain.MyApplication")) {
                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                virtualMachine.loadAgent("E:/ideaSpace/backend/java-agent/target/java-agent-1.0-SNAPSHOT.jar");
                virtualMachine.detach();
            }
        }
    }*/
}
