package org.firstinspires.ftc.fusion4133;

import android.hardware.Sensor;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Fusion on 11/6/2016.
 */
public abstract class LeptonAutoSetup extends LinearOpMode {
    AutonomousTextOption allianceColor = new AutonomousTextOption("Alliance Color", "blue", new String[] {"Blue", "Red"});
    AutonomousTextOption startPos = new AutonomousTextOption("Start Position", "Middle", new String [] {"Corner", "Center"});
    AutonomousIntOption waitStart = new AutonomousIntOption("Wait at Start", 0, 0, 20);
    AutonomousBooleanOption parkCenter = new AutonomousBooleanOption("Park Center", true);
    AutonomousBooleanOption pressBeacons = new AutonomousBooleanOption("Press Beacons", true);
    AutonomousBooleanOption parkCorner = new AutonomousBooleanOption("Park Corner", true);

    AutonomousOption [] autoOptions = {allianceColor, startPos, waitStart, parkCenter, pressBeacons, parkCorner};
    int currentOption = 0;

    boolean aPressed = false;
    boolean bPressed = false;
    boolean xPressed = false;
    boolean yPressed = false;

    LeptonHardwareSetup robot  = new LeptonHardwareSetup();
    final double ticksPerInch  = 180.0;  //tick of the encoder * gear ratio / circumference of the wheel
    final double inchesPerDeg  = .143;  //wheel base of robot * pi / 360
    final double tickPerDeg    = ticksPerInch * inchesPerDeg;
    ElapsedTime movementTime  = new ElapsedTime();
    double leftDirAdj;
    double rightDirAdj;


    public enum driveDirections{
        FORWARD, BACKWARD
    }
    public enum turnDirections{
        LEFT, RIGHT
    }

    public void showOptions (){
        int index = 0;
        String str = "";

        while (index < autoOptions.length){
            switch (autoOptions[index].optionType){
                case STRING:
                    str = ((AutonomousTextOption)autoOptions[index]).getValue();
                    break;
                case INT:
                    str = Integer.toString(((AutonomousIntOption)autoOptions[index]).getValue());
                    break;
                case BOOLEAN:
                    str = String.valueOf(((AutonomousBooleanOption)autoOptions[index]).getValue());
                    break;
            }

            if (index == currentOption){
                telemetry.addData(Integer.toString(index) + ") ==> " + autoOptions[index].name,str);
            } else {
                telemetry.addData(Integer.toString(index) + ")     " + autoOptions[index].name, str);
            }

            index = index + 1;
        }
        telemetry.update();
    }

    public void selectOptions () throws InterruptedException {

        while (currentOption< autoOptions.length && !opModeIsActive()){
            showOptions();

                if (gamepad1.a && !aPressed) {
                    currentOption = currentOption + 1;
                    telemetry.clearAll();
                    aPressed = true;
                } else {
                    aPressed = gamepad1.a;
                }

                if (gamepad1.y && !yPressed) {
                    currentOption = currentOption - 1;
                    telemetry.clearAll();
                    yPressed = true;
                } else {
                    yPressed = gamepad1.y;
                }

                if (gamepad1.b && !bPressed) {
                    autoOptions[currentOption].nextValue();
                    bPressed = true;
                } else {
                    bPressed = gamepad1.b;
                }

                if (gamepad1.x && !xPressed) {
                    autoOptions[currentOption].previousValue();
                    xPressed = true;
                } else {
                    xPressed = gamepad1.x;
                }

            telemetry.update();
            this.idle();
        }

        telemetry.clearAll();
        telemetry.addData("Robot","READY!!");
        telemetry.update();
    }

    public void driveENC (double ispeed, int idist, driveDirections idir) {
        int leftTargetFront;
        int leftTargetBack;
        int rightTargetFront;
        int rightTargetBack;

        int leftStartFront =  0;
        int leftStartBack =   0;
        int rightStartFront = 0;
        int rightStartBack =  0;

        int leftFinalFront =  0;
        int leftFinalBack =   0;
        int rightFinalFront = 0;
        int rightFinalBack =  0;

        resetEncoders();

        if(idir == driveDirections.FORWARD){
            leftDirAdj  = 1.0;
            rightDirAdj = 1.0;
        }
        else{
            leftDirAdj  = -1.0;
            rightDirAdj = -1.0;
        }

        leftStartFront = robot.leftMotorFront.getCurrentPosition();
        leftStartBack = robot.leftMotorBack.getCurrentPosition();
        rightStartFront = robot.rightMotorFront.getCurrentPosition();
        rightStartBack = robot.rightMotorBack.getCurrentPosition();

        leftTargetFront  = leftStartFront  + (int) (idist*ticksPerInch*leftDirAdj);
        leftTargetBack   = leftStartBack   + (int) (idist*ticksPerInch*leftDirAdj);
        rightTargetFront = rightStartFront + (int) (idist*ticksPerInch*rightDirAdj);
        rightTargetBack  = rightStartBack  + (int) (idist*ticksPerInch*rightDirAdj);

        telemetry.addData("leftFront",Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront)+ "; " + Integer.toString(leftFinalFront));
        telemetry.addData("leftBack",Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack)+ "; " + Integer.toString(leftFinalBack));
        telemetry.addData("rightFront",Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront)+ "; " + Integer.toString(rightFinalFront));
        telemetry.addData("rightBack",Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack)+ "; " + Integer.toString(rightFinalBack));

        telemetry.update();

        robot.leftMotorFront.setTargetPosition(leftTargetFront);
        robot.leftMotorBack.setTargetPosition(leftTargetBack);
        robot.rightMotorFront.setTargetPosition(rightTargetFront);
        robot.rightMotorBack.setTargetPosition(rightTargetBack);

        robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);



        robot.rightMotorBack.setPower(ispeed*rightDirAdj);
        robot.rightMotorFront.setPower(ispeed*rightDirAdj);
        robot.leftMotorBack.setPower(ispeed*leftDirAdj);
        robot.leftMotorFront.setPower(ispeed*leftDirAdj);

        while (opModeIsActive()&&
               robot.leftMotorFront.isBusy()&&
               robot.leftMotorBack.isBusy()&&
               robot.rightMotorFront.isBusy()&&
               robot.rightMotorBack.isBusy()) {
        }

        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

        robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(500);

        leftFinalFront = robot.leftMotorFront.getCurrentPosition();
        leftFinalBack = robot.leftMotorBack.getCurrentPosition();
        rightFinalFront = robot.rightMotorFront.getCurrentPosition();
        rightFinalBack = robot.rightMotorBack.getCurrentPosition();

        telemetry.addData("leftFront",Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront)+ "; " + Integer.toString(leftFinalFront));
        telemetry.addData("leftBack",Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack)+ "; " + Integer.toString(leftFinalBack));
        telemetry.addData("rightFront",Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront)+ "; " + Integer.toString(rightFinalFront));
        telemetry.addData("rightBack",Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack)+ "; " + Integer.toString(rightFinalBack));

        telemetry.update();
    }

    public void resetEncoders(){
        robot.leftMotorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotorBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotorBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        while (robot.rightMotorBack.getCurrentPosition() != 0) {
            idle();
        }
    }

    public void driveTime(double ispeed, int itime, driveDirections idir) {

        if(idir == driveDirections.BACKWARD){
            leftDirAdj  = -1.0;
            rightDirAdj = -1.0;
        }
        else {
            leftDirAdj  = 1.0;
            rightDirAdj = 1.0;
        }

        robot.rightMotorBack.setPower(ispeed*rightDirAdj);
        robot.rightMotorFront.setPower(ispeed*rightDirAdj);
        robot.leftMotorBack.setPower(ispeed*leftDirAdj);
        robot.leftMotorFront.setPower(ispeed*leftDirAdj);

        movementTime.reset();

        while (opModeIsActive() && movementTime.milliseconds() < itime) {
        }

        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);


    }
    public void spinTime (double ispeed, int itime, turnDirections idir) {

        if(idir == turnDirections.RIGHT){
            rightDirAdj = -1.0;
            leftDirAdj  = 1.0;
        }
        else{
            rightDirAdj = 1.0;
            leftDirAdj  = -1.0;
        }

        robot.rightMotorBack.setPower(ispeed*rightDirAdj);
        robot.rightMotorFront.setPower(ispeed*rightDirAdj);
        robot.leftMotorBack.setPower(ispeed*leftDirAdj);
        robot.leftMotorFront.setPower(ispeed*leftDirAdj);

        movementTime.reset();
        while (opModeIsActive() && movementTime.milliseconds() < itime) {
        }

        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

    }

    public void spinENC (double ispeed, int idist, turnDirections idir){

        int leftTargetFront;
        int leftTargetBack;
        int rightTargetFront;
        int rightTargetBack;

        resetEncoders();

        if(idir == turnDirections.LEFT){
            leftDirAdj  = -1.0;
            rightDirAdj = 1.0;
        }
        else {
            leftDirAdj  = 1.0;
            rightDirAdj = -1.0;
        }

        leftTargetFront  = robot.leftMotorFront.getCurrentPosition()  + (int) (idist*tickPerDeg*leftDirAdj);
        leftTargetBack   = robot.leftMotorBack.getCurrentPosition()   + (int) (idist*tickPerDeg*leftDirAdj);
        rightTargetFront = robot.rightMotorFront.getCurrentPosition() + (int) (idist*tickPerDeg*rightDirAdj);
        rightTargetBack  = robot.rightMotorBack.getCurrentPosition()  + (int) (idist*tickPerDeg*rightDirAdj);

        robot.leftMotorFront.setTargetPosition(leftTargetFront);
        robot.leftMotorBack.setTargetPosition(leftTargetBack);
        robot.rightMotorFront.setTargetPosition(rightTargetFront);
        robot.rightMotorBack.setTargetPosition(rightTargetBack);

        robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.rightMotorBack.setPower(ispeed*rightDirAdj);
        robot.rightMotorFront.setPower(ispeed*rightDirAdj);
        robot.leftMotorBack.setPower(ispeed*leftDirAdj);
        robot.leftMotorFront.setPower(ispeed*leftDirAdj);

        while (opModeIsActive()&&
                robot.leftMotorFront.isBusy()&&
                robot.leftMotorBack.isBusy()&&
                robot.rightMotorFront.isBusy()&&
                robot.rightMotorBack.isBusy()) {
        }

        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

        robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void spinGyro (double ispeed, int idist, turnDirections idir){

        int curGyro = 0;
        int startGyro = robot.gyro.getHeading();
        int targetGyro;
        boolean wrapGyro = false;
        boolean direction;

        if (ispeed < 0.0) {
            direction = false;
            targetGyro = startGyro - idist;

            if (targetGyro < 0) {
                targetGyro = targetGyro + 360;
                wrapGyro = true;
            }

        } else {
            direction = true;
            targetGyro = startGyro + idist;

            if (targetGyro > 360) {
                targetGyro = targetGyro - 360;
                wrapGyro = true;
            }
        }

        robot.rightMotorBack.setPower(ispeed*rightDirAdj);
        robot.rightMotorFront.setPower(ispeed*rightDirAdj);
        robot.leftMotorBack.setPower(ispeed*leftDirAdj);
        robot.leftMotorFront.setPower(ispeed*leftDirAdj);

        curGyro = Sensor.TYPE_GYROSCOPE;
        while (wrapGyro || (direction && curGyro < targetGyro) || (!direction && curGyro > targetGyro)) {
//        while (Math.abs(offset - curGyro) < idist) || curGyro < 2 || curGyro > 358) {
            curGyro = Sensor.TYPE_GYROSCOPE;
            if (wrapGyro && direction && curGyro < 20) {
                wrapGyro = false;
            } else if (wrapGyro && !direction && curGyro > 340) {
                wrapGyro = true;
            }

            telemetry.update();
            this.idle();

        }
        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

    }

    public void pointTime (double ispeed, int itime, turnDirections idir){

        if(idir == turnDirections.LEFT) {
            rightDirAdj = 1.0;
            leftDirAdj = 0.0;
        }
        else{
            rightDirAdj= 0.0;
            leftDirAdj= 1.0;
        }

        robot.rightMotorBack.setPower(ispeed*rightDirAdj);
        robot.rightMotorFront.setPower(ispeed*rightDirAdj);
        robot.leftMotorBack.setPower(ispeed*leftDirAdj);
        robot.leftMotorFront.setPower(ispeed*leftDirAdj);

        movementTime.reset();
        while (opModeIsActive() && movementTime.milliseconds() < itime) {
        }

        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

    }

    public void pointENC (double ispeed, int idist, turnDirections idir){
        int leftTargetFront;
        int leftTargetBack;
        int rightTargetFront;
        int rightTargetBack;

        resetEncoders();

        if(idir == turnDirections.LEFT){

            rightTargetFront = robot.rightMotorFront.getCurrentPosition() + (int) (idist*2.0*tickPerDeg);
            rightTargetBack  = robot.rightMotorBack.getCurrentPosition()  + (int) (idist*2.0*tickPerDeg);


            robot.rightMotorFront.setTargetPosition(rightTargetFront);
            robot.rightMotorBack.setTargetPosition(rightTargetBack);

            robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.rightMotorBack.setPower(ispeed);
            robot.rightMotorFront.setPower(ispeed);
            robot.leftMotorBack.setPower(0.0);
            robot.leftMotorFront.setPower(0.0);

            while (opModeIsActive()&&

                    robot.rightMotorFront.isBusy()&&
                    robot.rightMotorBack.isBusy()) {
            }


        }
        else{

            leftTargetFront  = robot.leftMotorFront.getCurrentPosition()  + (int) (idist*2.0*tickPerDeg);
            leftTargetBack   = robot.leftMotorBack.getCurrentPosition()   + (int) (idist*2.0*tickPerDeg);

            robot.leftMotorFront.setTargetPosition(leftTargetFront);
            robot.leftMotorBack.setTargetPosition(leftTargetBack);

            robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.rightMotorFront.setPower(0.0);
            robot.rightMotorBack.setPower(0.0);
            robot.leftMotorBack.setPower(-ispeed);
            robot.leftMotorFront.setPower(-ispeed);

            while (opModeIsActive()&&
                    robot.leftMotorFront.isBusy()&&
                    robot.leftMotorBack.isBusy()) {
            }

        }


        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

        robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

}
