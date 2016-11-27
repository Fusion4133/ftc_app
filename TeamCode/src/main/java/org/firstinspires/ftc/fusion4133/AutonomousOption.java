package org.firstinspires.ftc.fusion4133;

/**
 * Created by Fusion on 1/17/2016.
 */
abstract class AutonomousOption {
    String name;
    public enum OptionTypes {STRING, INT, BOOLEAN};
    OptionTypes optionType;

    abstract void nextValue ();

    abstract void previousValue ();

}

