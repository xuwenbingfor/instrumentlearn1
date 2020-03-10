package com.jz.javaagent;

import java.lang.instrument.Instrumentation;

public class PreMainTraceAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("run premain(String agentArgs, Instrumentation inst)");
        System.out.println("agentArgs : " + agentArgs);
        inst.addTransformer(new DefineTransformer("PreMainTraceAgent"), true);
    }

    public static void premain(String agentArgs) {
        System.out.println("run premain(String agentArgs)");
    }
}
