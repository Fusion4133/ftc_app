package org.firstinspires.ftc.fusion4133;

/**
 * Created by Fusion on 1/17/2016.
 */
public class AutonomousBooleanOption extends AutonomousOption {
    private boolean currentValue;

    public AutonomousBooleanOption (String iName, boolean iStartVal){
        name = iName;
        optionType = OptionTypes.BOOLEAN;
        currentValue = iStartVal;
    }

    public void nextValue (){
        currentValue = true;
    }

    public void previousValue (){
        currentValue = false;
    }

    public boolean getValue (){
        return currentValue;
    }

    public void setValue (boolean iVal){
        currentValue = iVal;
    }

}
