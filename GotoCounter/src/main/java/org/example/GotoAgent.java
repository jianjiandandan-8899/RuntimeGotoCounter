package org.example;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class GotoAgent {
    public static int gotoCount = 0;

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new GotoTransformer());
    }

    public static class GotoTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (className.equals("org/example/ExampleWithGoto")) {
                try {
                    return SootUtils.modifyClass(classfileBuffer, className.replace('/', '.'));
                } catch (IOException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }
            // System.out.println("i000: " + className);
            return classfileBuffer;
        }
    }
}