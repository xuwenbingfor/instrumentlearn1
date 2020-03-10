package com.jz.javaagent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentMainTraceAgent {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException {
        System.out.println("run agentmain(String agentArgs, Instrumentation inst)");
        System.out.println("agentArgs : " + agentArgs);
        instrumentation.addTransformer(new DefineTransformer("AgentMainTraceAgent"), true);
        Class clszz = Class.forName("com.jz.javadependency.Car");
        instrumentation.retransformClasses(clszz);
    }
}
