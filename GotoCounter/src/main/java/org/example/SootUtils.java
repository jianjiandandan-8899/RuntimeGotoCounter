package org.example;

import jasmin.ClassFile;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.JasminOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import static soot.SootClass.SIGNATURES;


public class SootUtils {

    public static void f() {
        System.out.println("ffff");
    }
    public static byte[] modifyClass(byte[] classBytes, String className) throws IOException {

        G.reset();

        Options.v().set_keep_line_number(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_output_format(Options.output_format_class);
        String sootClassPath = Scene.v().getSootClassPath() + File.pathSeparator + System.getProperty("java.class.path");
        Scene.v().setSootClassPath(sootClassPath);
        Scene.v().addBasicClass("org.example.GotoAgent",SIGNATURES);

        SootClass sootClass = loadClass(classBytes, className);
//

        // Modify the class to count goto statements
        for (SootMethod method : sootClass.getMethods()) {
            //System.out.println("Processing method: " + method.getSignature());
            if (method.isConcrete()) {
                try {
                    Body body = method.retrieveActiveBody();
                    Iterator<Unit> iterator = body.getUnits().snapshotIterator();
                    while (iterator.hasNext()) {
                        Unit unit = iterator.next();
                        //System.out.println("Original unit: " + unit);
                        if (unit instanceof GotoStmt ) {
                            //System.out.println("Found GOTO or IF statement in method: " + method.getName() + unit);
                            insertGotoCount(body, unit);
                            //System.out.println("Updated unit: " + unit );
                        }
                    }
                    // 打印插入指令后的所有单元，便于调试
//                    for (Unit u : body.getUnits()) {
//                        System.out.println("Updated unit: =======" + u);
//                    }
                } catch (RuntimeException e) {
                    System.out.println("Could not retrieve body for method: " + method.getSignature());
                    e.printStackTrace();
                }
            }
        }

        // Write the modified class to a byte array
        byte [] bytes = writeClass(sootClass);
        Files.write(Paths.get("./Example.class"), bytes);
        return writeClass(sootClass);
    }


    private static SootClass loadClass(byte[] classBytes, String className) {
        InputStream is = new ByteArrayInputStream(classBytes);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Scene.v().setSootClassPath(Scene.v().getSootClassPath());

        // Create a SootClass from the byte array
        SootClass sootClass = new SootClass(className, Modifier.PUBLIC);
        sootClass.setApplicationClass();
        Scene.v().addClass(sootClass);
        Scene.v().loadNecessaryClasses();

        // Load the class file into Soot
        SourceLocator.v().getClassSource(className).resolve(sootClass);

        return sootClass;
    }



    private static void insertGotoCount(Body body, Unit unit) {

        JimpleBody jBody = (JimpleBody) body;
        UnitPatchingChain units = jBody.getUnits();
        SootField gotoCountField = Scene.v().getSootClass("org.example.GotoAgent").getFieldByName("gotoCount");

        // 确保局部变量 tmpLocal 仅被添加一次
        Local tmpLocal = getOrCreateLocal(jBody, "tmp", IntType.v());

        // 创建新的指令，用于更新 gotoCount
        AssignStmt assignToTmp = Jimple.v().newAssignStmt(tmpLocal, Jimple.v().newStaticFieldRef(gotoCountField.makeRef()));
        AssignStmt incrementTmp = Jimple.v().newAssignStmt(tmpLocal, Jimple.v().newAddExpr(tmpLocal, IntConstant.v(1)));
        AssignStmt updateGotoCount = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(gotoCountField.makeRef()), tmpLocal);

//        // 将这些指令插入到 GOTO 语句之前
//        units.insertBefore(assignToTmp, unit);
//        units.insertBefore(incrementTmp, unit);
//        units.insertBefore(updateGotoCount, unit);
//        System.out.println("gotoCount: " + GotoAgent.gotoCount);
        
        // 根据语句类型插入指令
        if (((GotoStmt)unit).getTarget() instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) ((GotoStmt)unit).getTarget();
            units.insertBefore(assignToTmp, ifStmt.getTarget());
            units.insertBefore(incrementTmp, ifStmt.getTarget());
            units.insertBefore(updateGotoCount, ifStmt.getTarget());
        } else {
            units.insertBefore(assignToTmp, unit);
            units.insertBefore(incrementTmp, unit);
            units.insertBefore(updateGotoCount, unit);
        }

    }


    private static Local getOrCreateLocal(JimpleBody body, String name, Type type) {
        for (Local local : body.getLocals()) {
            if (local.getName().equals(name) && local.getType().equals(type)) {
                return local;
            }
        }
        Local newLocal = Jimple.v().newLocal(name, type);
        body.getLocals().add(newLocal);
        return newLocal;
    }

    private static byte[] writeClass(SootClass sootClass) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PrintWriter writer = new PrintWriter(new JasminOutputStream(baos));
            JasminClass jasminClass = new soot.jimple.JasminClass(sootClass);
            jasminClass.print(writer);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
