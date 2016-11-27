package org.firstinspires.ftc.fusion4133;

/**
 * Created by Fusion on 1/17/2016.
 */
public class AutonomousIntOption extends AutonomousOption {
    private int currentValue;
    private int maxValue;
    private int minValue;

    public AutonomousIntOption (String iName, int iStartVal, int iMinVal, int iMaxVal){
        name = iName;
        optionType = OptionTypes.INT;
        currentValue = iStartVal;
        maxValue = iMaxVal;
        minValue = iMinVal;
    }

    public void nextValue (){
        currentValue = currentValue +1;
        if (currentValue>maxValue) {
            currentValue = minValue;
        }
    }

    public void previousValue (){
        currentValue = currentValue -1;
        if (currentValue< minValue){
            currentValue = maxValue;
        }
    }

    public int getValue (){
        return currentValue;
    }

    public void setValue (int iVal){
        if (iVal>= minValue && iVal <= maxValue){
            currentValue = iVal;
        }
    }
}
