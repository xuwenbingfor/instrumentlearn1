package com.jz.javaagent;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DefineTransformer implements ClassFileTransformer {
    private String type;

    public DefineTransformer(String type) {
        this.type = type;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println(type + " load Class:" + className);
        if ("com/jz/javadependency/Car".equals(className)) {
            try {
                final ClassPool classPool = ClassPool.getDefault();
                System.out.println("this的类加载器:" + this.getClass().getClassLoader().getName());
                System.out.println("javassist ClassPool的类加载器:"
                        + (ClassPool.class.getClassLoader() == null ? null : ClassPool.class.getClassLoader().getName()));
                String clazzName = null;
                if ("AgentMainTraceAgent".equals(type)) {
                    Class clszz = Class.forName("com.jz.javadependency.Car");
                    classPool.insertClassPath(new ClassClassPath(clszz));
                    clazzName = clszz.getName();
                } else {
                    clazzName = "com.jz.javadependency.Car";
                }
                final CtClass clazz = classPool.get(clazzName);
                CtMethod convertToAbbr = clazz.getDeclaredMethod("print");
                String methodBody = "{System.out.println(\"car2\");}";
                convertToAbbr.setBody(methodBody);

                byte[] byteCode = clazz.toBytecode();
                clazz.detach();
                return byteCode;
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        return classfileBuffer;
    }

/*    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println(type + " load Class:" + className);
        // 操作Date类
        if ("java/util/Date".equals(className)) {
            try {
//                 从ClassPool获得CtClass对象
                final ClassPool classPool = ClassPool.getDefault();
                System.out.println("this的类加载器:" + this.getClass().getClassLoader().getName());
                System.out.println("javassist ClassPool的类加载器:"
                        + (ClassPool.class.getClassLoader() == null ? null : ClassPool.class.getClassLoader().getName()));
                // 用于取得字节码类，必须在当前的classpath中，使用全称 ,这部分是关于javassist的知识
                final CtClass clazz = classPool.get("java.util.Date");
                CtMethod convertToAbbr = clazz.getDeclaredMethod("toInstant");
//                这里对 java.util.Date.convertToAbbr() 方法进行了改写，在 return之前增加了一个 打印操作
                String methodBody = "{System.out.println(\"完成修改字节码\");" +
                        "return java.time.Instant.ofEpochMilli(getTime());}";
                convertToAbbr.setBody(methodBody);

//                 返回字节码，并且detachCtClass对象
                byte[] byteCode = clazz.toBytecode();
//                detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                clazz.detach();
                return byteCode;
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        return classfileBuffer;
    }*/
}
