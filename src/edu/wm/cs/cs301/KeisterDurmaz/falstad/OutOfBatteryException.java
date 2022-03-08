package edu.wm.cs.cs301.KeisterDurmaz.falstad;

public class OutOfBatteryException extends Exception {
    public OutOfBatteryException() {
    	System.out.println("ERROR: Out of energy.");
    }
}
