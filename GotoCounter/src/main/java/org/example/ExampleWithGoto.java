package org.example;

public class ExampleWithGoto {

    public static void main(String[] args) {
        int count = 0;

        while (true) {
            count++;
            if (count >= 10) {
                break; // This will be a GOTO in bytecode
            }

        }

        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                continue; // This will be a GOTO in bytecode
            } else {
                i = i;
            }

            if (i == 8) {
                break;
            }
           // System.out.println(i);
        }

        System.out.println("Goto statements executed: " + GotoAgent.gotoCount);
    }
}