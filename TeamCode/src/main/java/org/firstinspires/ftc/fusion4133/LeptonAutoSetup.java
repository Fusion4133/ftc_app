package org.firstinspires.ftc.fusion4133;

        import android.hardware.Sensor;

        import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
        import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.DcMotorController;
        import com.qualcomm.robotcore.robot.Robot;
        import com.qualcomm.robotcore.util.ElapsedTime;

        import org.lasarobotics.vision.opmode.LinearVisionOpMode;

/**
 * Created by Fusion on 11/6/2016.
 */
//we do a secondary setup for our auto because it allows us for a eaiser time in the future making autonomus because all basic movements are already defined.
public abstract class LeptonAutoSetup extends LinearVisionOpMode
    //this is where the autonomous options are defined.
    {
    AutonomousTextOption    allianceColor = new AutonomousTextOption("Alliance Color", "blue", new String[] {"blue", "red"});
    AutonomousTextOption    startPos      = new AutonomousTextOption("Start Position", "middle", new String [] {"middle", "line"});
    AutonomousIntOption     waitStart     = new AutonomousIntOption("Wait at Start", 0, 0, 20);
    AutonomousIntOption     numberBalls    = new AutonomousIntOption("Shoot Balls", 2, 0, 2);
    AutonomousBooleanOption parkCenter    = new AutonomousBooleanOption("Park Center", true);
    AutonomousBooleanOption pressBeacons  = new AutonomousBooleanOption("Press Beacons", true);
    AutonomousBooleanOption parkCorner    = new AutonomousBooleanOption("Park Corner", true);
    AutonomousIntOption     waitShoot     = new AutonomousIntOption("Wait after Shoot", 0, 0, 20 );
    //this is how we get the options to show up on the phone.
    AutonomousOption [] autoOptions       = {allianceColor, startPos, waitStart, numberBalls, waitShoot, pressBeacons, parkCenter, parkCorner};
    int currentOption = 0;

    //this is how we tell the program what buttons we are using to chose the program.
    boolean aPressed = false;
    boolean bPressed = false;
    boolean xPressed = false;
    boolean yPressed = false;

    LeptonHardwareSetup robot  = new LeptonHardwareSetup();
    //this is where we defined are ticks so are drives would be as far as we want them to be.
    final double ticksPerInch  = 188;  //tick of the encoder * gear ratio / circumference of the wheel
    final  int   tickOverRun   = 80;   //number of tick robot overruns target after stop
    final double inchesPerDeg  = .142;  //wheel base of robot * pi / 360
    final double tickPerDeg    = ticksPerInch * inchesPerDeg;
    ElapsedTime movementTime   = new ElapsedTime();
    double leftDirAdj;
    double rightDirAdj;
    //time to rotate, 600 @ 1.0 motor power
    //time to rotate,

    //this is so that we we keep the button pushers from smashing into the wall.
    double[]  bplPositions = {robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            robot.BPL_IN,
            0.51,
            0.44,
            0.41,
            0.39,
            0.38,
            0.34,
            0.30,
            0.28,
            0.25,
            0.25,
            0.22,
            0.19,
            0.17,
            0.16};

    final int timeToRotate     = 700; //milliseconds to rotate around one time
    final int timeToLoadBall   = 1000; //milliseconds it takes for the next ball to fall into the popper
    // This is where we define our drives.
    public enum driveDirections{
        FORWARD, BACKWARD
    }
    public enum turnDirections{
        LEFT, RIGHT
    }
    // This is how we get our autonomous options to show up on our phones.
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
    // This is how we select our auto options
    public void selectOptions () throws InterruptedException {

        while (currentOption< autoOptions.length && !opModeIsActive()){
            showOptions();

            if (gamepad1.a && !aPressed) {
                currentOption = currentOption + 1;
            }
            aPressed = gamepad1.a;

            if (gamepad1.y && !yPressed) {
                currentOption = currentOption - 1;
            }
            yPressed = gamepad1.y;

            if (gamepad1.b && !bPressed) {
                autoOptions[currentOption].nextValue();
            }
            bPressed = gamepad1.b;

            if (gamepad1.x && !xPressed) {
                autoOptions[currentOption].previousValue();
            }
            xPressed = gamepad1.x;

            telemetry.update();
            Thread.yield();
        }

        telemetry.addData("Robot","READY!!");
        telemetry.update();
    }

    // This is our main drive that the distance is determaind by the encoders so it will go as far as we tell it to go because of the ticks per inch above.
    public void driveENC (double ispeed, int idist, driveDirections idir) {

        double vSpeed = ispeed;

        int leftTargetFront;
        int leftTargetBack;
        int rightTargetFront;
        int rightTargetBack;

        int leftStartFront = 0;
        int leftStartBack = 0;
        int rightStartFront = 0;
        int rightStartBack = 0;

        int leftFinalFront = 0;
        int leftFinalBack = 0;
        int rightFinalFront = 0;
        int rightFinalBack = 0;

        int leftAdjTargetFront;
        int leftAdjTargetBack;
        int rightAdjTargetFront;
        int rightAdjTargetBack;

        if (idir == driveDirections.FORWARD) {
            leftDirAdj = 1.0;
            rightDirAdj = 1.0;
        } else {
            leftDirAdj = -1.0;
            rightDirAdj = -1.0;
            vSpeed = vSpeed * -1;
        }

        leftStartFront = robot.leftMotorFront.getCurrentPosition();
        leftStartBack = robot.leftMotorBack.getCurrentPosition();
        rightStartFront = robot.rightMotorFront.getCurrentPosition();
        rightStartBack = robot.rightMotorBack.getCurrentPosition();

        leftTargetFront = leftStartFront + (int) (idist * ticksPerInch * leftDirAdj);
        leftTargetBack = leftStartBack + (int) (idist * ticksPerInch * leftDirAdj);
        rightTargetFront = rightStartFront + (int) (idist * ticksPerInch * rightDirAdj);
        rightTargetBack = rightStartBack + (int) (idist * ticksPerInch * rightDirAdj);

        leftAdjTargetFront  = leftTargetFront  - (int)(tickOverRun * leftDirAdj);
        leftAdjTargetBack   = leftTargetBack   - (int)(tickOverRun * leftDirAdj);
        rightAdjTargetFront = rightTargetFront - (int)(tickOverRun * rightDirAdj);
        rightAdjTargetBack  = rightTargetBack  - (int)(tickOverRun * rightDirAdj);

        telemetry.addData("leftFront", Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront) + "; " + Integer.toString(leftFinalFront));
        telemetry.addData("leftBack", Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack) + "; " + Integer.toString(leftFinalBack));
        telemetry.addData("rightFront", Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront) + "; " + Integer.toString(rightFinalFront));
        telemetry.addData("rightBack", Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack) + "; " + Integer.toString(rightFinalBack));

        telemetry.update();

        robot.rightMotorBack.setPower(vSpeed);
        robot.rightMotorFront.setPower(vSpeed);
        robot.leftMotorFront.setPower(vSpeed);
        robot.leftMotorBack.setPower(vSpeed);

        if (idir == driveDirections.FORWARD) {

            while (opModeIsActive() &&
                    robot.leftMotorFront.getCurrentPosition() < leftAdjTargetFront &&
                    robot.leftMotorBack.getCurrentPosition() < leftAdjTargetBack &&
                    robot.rightMotorFront.getCurrentPosition() < rightAdjTargetFront &&
                    robot.rightMotorBack.getCurrentPosition() < rightAdjTargetBack ) {
            }
        }
        else {
            while (opModeIsActive() &&
                    robot.leftMotorFront.getCurrentPosition() > leftAdjTargetFront &&
                    robot.leftMotorBack.getCurrentPosition() > leftAdjTargetBack &&
                    robot.rightMotorFront.getCurrentPosition() > rightAdjTargetFront &&
                    robot.rightMotorBack.getCurrentPosition() > rightAdjTargetBack ) {
            }
        }

        robot.rightMotorBack.setPower(0.0);
        robot.rightMotorFront.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

        leftFinalFront = robot.leftMotorFront.getCurrentPosition();
        leftFinalBack = robot.leftMotorBack.getCurrentPosition();
        rightFinalFront = robot.rightMotorFront.getCurrentPosition();
        rightFinalBack = robot.rightMotorBack.getCurrentPosition();

        telemetry.addData("leftFront",Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront)+ "; " + Integer.toString(leftFinalFront)+ "; " + Integer.toString(leftAdjTargetFront));
        telemetry.addData("leftBack",Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack)+ "; " + Integer.toString(leftFinalBack)+ "; " + Integer.toString(leftAdjTargetBack));
        telemetry.addData("rightFront",Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront)+ "; " + Integer.toString(rightFinalFront) + "; " + Integer.toString(rightAdjTargetFront));
        telemetry.addData("rightBack",Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack)+ "; " + Integer.toString(rightFinalBack) + "; " + Integer.toString(rightAdjTargetBack));

        telemetry.update();
    }

    public void resetEncoders(){
        robot.leftMotorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotorBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotorBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        while (robot.rightMotorBack.getCurrentPosition() != 0) {
            Thread.yield();
        }
    }
    // This is a secondary drive that time detrmains the distance it goes we don't use it often because it is inaccurate.
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
    // This is a secondary spin turn where the amount of time is how far it turns we don't use it often because it is inaccurate.
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
    //this is a spin turn we use that is based on encoders and is fairly accurate.
    public void spinENC (double ispeed, int idist, turnDirections idir, double iAllianceAdj) {

        double vSpeed = ispeed;

        int leftTargetFront;
        int leftTargetBack;
        int rightTargetFront;
        int rightTargetBack;

        int leftStartFront = 0;
        int leftStartBack = 0;
        int rightStartFront = 0;
        int rightStartBack = 0;

        int leftFinalFront = 0;
        int leftFinalBack = 0;
        int rightFinalFront = 0;
        int rightFinalBack = 0;

        int leftAdjTargetFront;
        int leftAdjTargetBack;
        int rightAdjTargetFront;
        int rightAdjTargetBack;

        if (idir == turnDirections.RIGHT) {
            leftDirAdj = 1.0 * iAllianceAdj;
            rightDirAdj = -1.0 * iAllianceAdj;
        } else {
            leftDirAdj = -1.0 * iAllianceAdj;
            rightDirAdj = 1.0 * iAllianceAdj;
        }

        leftStartFront = robot.leftMotorFront.getCurrentPosition();
        leftStartBack = robot.leftMotorBack.getCurrentPosition();
        rightStartFront = robot.rightMotorFront.getCurrentPosition();
        rightStartBack = robot.rightMotorBack.getCurrentPosition();

        leftTargetFront = leftStartFront + (int) (idist * tickPerDeg * leftDirAdj);
        leftTargetBack = leftStartBack + (int) (idist * tickPerDeg * leftDirAdj);
        rightTargetFront = rightStartFront + (int) (idist * tickPerDeg * rightDirAdj);
        rightTargetBack = rightStartBack + (int) (idist * tickPerDeg * rightDirAdj);

        leftAdjTargetFront = leftTargetFront - (int) (tickOverRun * leftDirAdj);
        leftAdjTargetBack = leftTargetBack - (int) (tickOverRun * leftDirAdj);
        rightAdjTargetFront = rightTargetFront - (int) (tickOverRun * rightDirAdj);
        rightAdjTargetBack = rightTargetBack - (int) (tickOverRun * rightDirAdj);

        telemetry.addData("leftFront", Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront) + "; " + Integer.toString(leftFinalFront));
        telemetry.addData("leftBack", Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack) + "; " + Integer.toString(leftFinalBack));
        telemetry.addData("rightFront", Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront) + "; " + Integer.toString(rightFinalFront));
        telemetry.addData("rightBack", Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack) + "; " + Integer.toString(rightFinalBack));

        telemetry.update();

        robot.rightMotorBack.setPower(vSpeed * rightDirAdj);
        robot.rightMotorFront.setPower(vSpeed * rightDirAdj);
        robot.leftMotorFront.setPower(vSpeed * leftDirAdj);
        robot.leftMotorBack.setPower(vSpeed * leftDirAdj);

        if ((idir == turnDirections.RIGHT && iAllianceAdj > 0.0) || (idir == turnDirections.LEFT && iAllianceAdj < 0.0)) {

            while (opModeIsActive() &&
                    robot.leftMotorFront.getCurrentPosition() < leftAdjTargetFront &&
                    robot.leftMotorBack.getCurrentPosition() < leftAdjTargetBack &&
                    robot.rightMotorFront.getCurrentPosition() > rightAdjTargetFront &&
                    robot.rightMotorBack.getCurrentPosition() > rightAdjTargetBack) {
            }
        } else {
            while (opModeIsActive() &&
                    robot.leftMotorFront.getCurrentPosition() > leftAdjTargetFront &&
                    robot.leftMotorBack.getCurrentPosition() > leftAdjTargetBack &&
                    robot.rightMotorFront.getCurrentPosition() < rightAdjTargetFront &&
                    robot.rightMotorBack.getCurrentPosition() < rightAdjTargetBack) {
            }
        }

        robot.rightMotorBack.setPower(0.0);
        robot.rightMotorFront.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

        leftFinalFront = robot.leftMotorFront.getCurrentPosition();
        leftFinalBack = robot.leftMotorBack.getCurrentPosition();
        rightFinalFront = robot.rightMotorFront.getCurrentPosition();
        rightFinalBack = robot.rightMotorBack.getCurrentPosition();

        telemetry.addData("Done leftFront", Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront) + "; " + Integer.toString(leftFinalFront));
        telemetry.addData("leftBack", Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack) + "; " + Integer.toString(leftFinalBack));
        telemetry.addData("rightFront", Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront) + "; " + Integer.toString(rightFinalFront));
        telemetry.addData("rightBack", Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack) + "; " + Integer.toString(rightFinalBack));

        telemetry.update();

    }


    //this is a gyro turn that we use that is based on a gyroscopic sensor and is the most accurate of all the spin turns.
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
            Thread.yield();

        }
        robot.rightMotorFront.setPower(0.0);
        robot.rightMotorBack.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

    }
    //this is our time based point turn we don't use it often because it is inaccurate.
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
    //this is our encodee base point turn.
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
    //this is how we shoot the balls the amoutn of time is based on testing that we did.
    public void shootBalls () throws InterruptedException{
        double popperPower = 0.8;
        if (numberBalls.getValue() > 0){
            for (int i = 0; i < numberBalls.getValue(); i ++) {
                //for a battery at 12.75 volts, 1.0 motor power
                //for a battery at 13.31 volts, 0.9 motor power


                robot.popperMotor.setPower (popperPower);
                Thread.sleep(timeToRotate);
                robot.popperMotor.setPower(0.0);
                Thread.sleep(timeToLoadBall);
                //popperPower = 0.6;
            }
        }
        else {
            Thread.sleep(2000);
        }
    }

    //this is our range based drive so that in the button press program we don't hit the walls.
    public void driveENCRange (double ispeed, int idist, driveDirections idir, double irange) {

        double vSpeed = ispeed;

        int leftTargetFront;
        int leftTargetBack;
        int rightTargetFront;
        int rightTargetBack;

        int leftStartFront = 0;
        int leftStartBack = 0;
        int rightStartFront = 0;
        int rightStartBack = 0;

        int leftFinalFront = 0;
        int leftFinalBack = 0;
        int rightFinalFront = 0;
        int rightFinalBack = 0;

        int leftAdjTargetFront;
        int leftAdjTargetBack;
        int rightAdjTargetFront;
        int rightAdjTargetBack;

        if (idir == driveDirections.FORWARD) {
            leftDirAdj = 1.0;
            rightDirAdj = 1.0;
        } else {
            leftDirAdj = -1.0;
            rightDirAdj = -1.0;
            vSpeed = vSpeed * -1;
        }

        leftStartFront = robot.leftMotorFront.getCurrentPosition();
        leftStartBack = robot.leftMotorBack.getCurrentPosition();
        rightStartFront = robot.rightMotorFront.getCurrentPosition();
        rightStartBack = robot.rightMotorBack.getCurrentPosition();

        leftTargetFront = leftStartFront + (int) (idist * ticksPerInch * leftDirAdj);
        leftTargetBack = leftStartBack + (int) (idist * ticksPerInch * leftDirAdj);
        rightTargetFront = rightStartFront + (int) (idist * ticksPerInch * rightDirAdj);
        rightTargetBack = rightStartBack + (int) (idist * ticksPerInch * rightDirAdj);

        leftAdjTargetFront = leftTargetFront - (int) (tickOverRun * leftDirAdj);
        leftAdjTargetBack = leftTargetBack - (int) (tickOverRun * leftDirAdj);
        rightAdjTargetFront = rightTargetFront - (int) (tickOverRun * rightDirAdj);
        rightAdjTargetBack = rightTargetBack - (int) (tickOverRun * rightDirAdj);

        telemetry.addData("leftFront", Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront) + "; " + Integer.toString(leftFinalFront));
        telemetry.addData("leftBack", Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack) + "; " + Integer.toString(leftFinalBack));
        telemetry.addData("rightFront", Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront) + "; " + Integer.toString(rightFinalFront));
        telemetry.addData("rightBack", Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack) + "; " + Integer.toString(rightFinalBack));

        telemetry.update();

        robot.rightMotorBack.setPower(vSpeed);
        robot.rightMotorFront.setPower(vSpeed);
        robot.leftMotorFront.setPower(vSpeed);
        robot.leftMotorBack.setPower(vSpeed);

        if (idir == driveDirections.FORWARD) {

            while (opModeIsActive() &&
                    robot.leftMotorFront.getCurrentPosition() < leftAdjTargetFront &&
                    robot.leftMotorBack.getCurrentPosition() < leftAdjTargetBack &&
                    robot.rightMotorFront.getCurrentPosition() < rightAdjTargetFront &&
                    robot.rightMotorBack.getCurrentPosition() < rightAdjTargetBack) {
                rangeADJ(ispeed, idir, irange);
                telemetry.update();
            }
        } else {
            while (opModeIsActive() &&
                    robot.leftMotorFront.getCurrentPosition() > leftAdjTargetFront &&
                    robot.leftMotorBack.getCurrentPosition() > leftAdjTargetBack &&
                    robot.rightMotorFront.getCurrentPosition() > rightAdjTargetFront &&
                    robot.rightMotorBack.getCurrentPosition() > rightAdjTargetBack) {
                rangeADJ(ispeed, idir, irange);
                telemetry.update();
            }
        }

        robot.rightMotorBack.setPower(0.0);
        robot.rightMotorFront.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);

        leftFinalFront = robot.leftMotorFront.getCurrentPosition();
        leftFinalBack = robot.leftMotorBack.getCurrentPosition();
        rightFinalFront = robot.rightMotorFront.getCurrentPosition();
        rightFinalBack = robot.rightMotorBack.getCurrentPosition();

        telemetry.addData("leftFront", Integer.toString(leftStartFront) + "; " + Integer.toString(leftTargetFront) + "; " + Integer.toString(leftFinalFront) + "; " + Integer.toString(leftAdjTargetFront));
        telemetry.addData("leftBack", Integer.toString(leftStartBack) + "; " + Integer.toString(leftTargetBack) + "; " + Integer.toString(leftFinalBack) + "; " + Integer.toString(leftAdjTargetBack));
        telemetry.addData("rightFront", Integer.toString(rightStartFront) + "; " + Integer.toString(rightTargetFront) + "; " + Integer.toString(rightFinalFront) + "; " + Integer.toString(rightAdjTargetFront));
        telemetry.addData("rightBack", Integer.toString(rightStartBack) + "; " + Integer.toString(rightTargetBack) + "; " + Integer.toString(rightFinalBack) + "; " + Integer.toString(rightAdjTargetBack));

        telemetry.update();
    }

    //this is how we make the drive range is scurate.
    public void rangeADJ (double ispeed, driveDirections idir, double irange) {
        double dirAdj;
        double slowInc = 0.075;
        double currentRange = robot.range.cmUltrasonic();

        telemetry.addData("cm ultrasonic", "%.2f cm", currentRange);

        if (idir == driveDirections.FORWARD) {
            dirAdj = 1.0;
        } else {
            dirAdj = -1.0;
        }

        if (irange > currentRange){
            telemetry.addData("i am", " too close");
            if (allianceColor.getValue().equals("red")){
                if (Math.abs(robot.leftMotorBack.getPower()) < ispeed){
                    robot.leftMotorBack.setPower(ispeed * dirAdj);
                    robot.leftMotorFront.setPower(ispeed * dirAdj);
                }
                else{
                    robot.rightMotorBack.setPower((ispeed - slowInc) * dirAdj);
                    robot.rightMotorFront.setPower((ispeed - slowInc) * dirAdj);
                }
            }
            else {
                if (Math.abs(robot.rightMotorBack.getPower()) < ispeed){
                    robot.rightMotorFront.setPower(ispeed * dirAdj);
                    robot.rightMotorBack.setPower(ispeed * dirAdj);
                }
                else{
                    robot.leftMotorBack.setPower((ispeed - (slowInc * 2.5)) * dirAdj);
                    robot.leftMotorFront.setPower((ispeed - (slowInc * 2.5)) * dirAdj);
                }
            }
        }
        else if (irange < currentRange){
            telemetry.addData("i am", " too far");
            if (allianceColor.getValue().equals("blue")){
                if (Math.abs(robot.leftMotorBack.getPower()) < ispeed){
                    robot.leftMotorBack.setPower(ispeed * dirAdj);
                    robot.leftMotorFront.setPower(ispeed * dirAdj);
                }
                else{
                    robot.rightMotorBack.setPower((ispeed - slowInc) * dirAdj);
                    robot.rightMotorFront.setPower((ispeed - slowInc) * dirAdj);
                }
            }
            else {
                if (Math.abs(robot.rightMotorBack.getPower()) < ispeed){
                    robot.rightMotorFront.setPower(ispeed * dirAdj);
                    robot.rightMotorBack.setPower(ispeed * dirAdj);
                }
                else{
                    robot.leftMotorBack.setPower((ispeed - slowInc) * dirAdj);
                    robot.leftMotorFront.setPower((ispeed - slowInc) * dirAdj);
                }
            }
        }
        else {
            robot.leftMotorBack.setPower(ispeed * dirAdj);
            robot.leftMotorFront.setPower(ispeed * dirAdj);
            robot.rightMotorBack.setPower(ispeed * dirAdj);
            robot.rightMotorFront.setPower(ispeed * dirAdj);
        }
        try {
            if (allianceColor.getValue().equals("blue")) {
                robot.buttonPushRight.setPosition(bplPositions[(int) currentRange]);
            } else {
                robot.buttonPushLeft.setPosition(bplPositions[(int) currentRange]);
            }
        }
        catch(Exception e){
            if (allianceColor.getValue().equals("blue")) {
                robot.buttonPushRight.setPosition(bplPositions[bplPositions.length-1]);
            } else {
                robot.buttonPushLeft.setPosition(bplPositions[bplPositions.length-1]);
            }

        }
    }
}
