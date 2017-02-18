package org.firstinspires.ftc.fusion4133;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.opmode.VisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Size;

import java.lang.reflect.Method;

import static org.lasarobotics.vision.opmode.VisionOpMode.beacon;
import static org.lasarobotics.vision.opmode.VisionOpMode.cameraControl;

/**
 * Created by Fusion on 11/2/2016.
 */
@Autonomous(name="Lepton: Autonomous", group="Lepton")
public class LeptonAuto extends LeptonAutoSetup {
    //Frame counter
    int frameCount = 0;

    double allianceColorAdj = 1.0;
    double distFromWall     = 13;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.log().setCapacity(1);

        robot.init(hardwareMap);

        telemetry.log().add("Robot Initalized");
        telemetry.update();
        //we have this on or off for the led so that we make sure it is on.
        robot.color.enableLed(false);
        robot.color.enableLed(true);

        waitForVisionStart();

        telemetry.log().add("Vision Started");
        telemetry.update();
        //we set the camera to the front facing one so that we can heve the screen accessible to the refs.
        this.setCamera(Cameras.SECONDARY);
        this.setFrameSize(new Size(900, 900));

        enableExtension(Extensions.BEACON);
        enableExtension(Extensions.ROTATION);
        enableExtension(VisionOpMode.Extensions.CAMERA_CONTROL);

        beacon.setColorToleranceRed(0);
        beacon.setColorToleranceBlue(0);

        rotation.setIsUsingSecondaryCamera(true);
        rotation.disableAutoRotate();
        rotation.setActivityOrientationFixed(ScreenOrientation.PORTRAIT);

        cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
        cameraControl.setAutoExposureCompensation();

        telemetry.log().add("Select Options");
        telemetry.update();

        selectOptions();

        robot.range = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeLeft");

        if (allianceColor.getValue().equals("red")){
            allianceColorAdj = 1.0;
        }
        else {
            allianceColorAdj = -1.0;
//            robot.range = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeRight");
        }

        telemetry.log().add("Waiting For Start");
        telemetry.update();

        waitForStart();

        telemetry.log().add("Program Started");
        telemetry.update();

        Thread.sleep((long) (waitStart.getValue() * 1000));

        if (startPos.getValue().equals("line")) {
            driveFromLine();
        }

        telemetry.log().add("Shoot Balls");
        telemetry.update();

        robot.color.enableLed(false);

        shootBalls();

        robot.color.enableLed(true);

        Thread.sleep((long) (waitShoot.getValue() * 1000));

        if (pressBeacons.getValue()) {
            doBeacons();
        }
        else if (parkCenter.getValue()){
            //Need a small turn when on blue side to accommodate differences in shooting
            if (allianceColor.getValue().equals("blue")) {
                spinENC(0.7, 5, turnDirections.LEFT, allianceColorAdj);
            }

            if (allianceColor.getValue().equals("red")) {

                driveENC(0.9, 56, driveDirections.FORWARD);
            }
            else {
                driveENC(0.9, 54, driveDirections.FORWARD);
            }
        }
    }

    public void driveFromLine() throws InterruptedException {
        if (allianceColor.getValue().equals("red")) {
            driveENC(0.7, 12, driveDirections.FORWARD);
            spinENC(0.7, 45, turnDirections.LEFT, allianceColorAdj);
            driveENC(0.4, 2, driveDirections.FORWARD);
            Thread.sleep(250);
        } else {
            driveENC(0.7, 12, driveDirections.FORWARD);
            spinENC(0.7, 40, turnDirections.LEFT, allianceColorAdj);
            driveENC(0.4, 4, driveDirections.FORWARD);
            Thread.sleep(250);
        }
    }

    public void doBeacons() throws InterruptedException {
        if (startPos.getValue().equals("middle") && allianceColor.getValue().equals("red")){
            telemetry.log().add("Move To Beacons From Mid");
            telemetry.update();

            driveENC(.7, 8, driveDirections.FORWARD);
            spinENC(.4, 40, turnDirections.LEFT, allianceColorAdj);
            driveENC(.9, 60, driveDirections.FORWARD);

                robot.buttonPushLeft.setPosition(robot.BPL_MID);

            spinENC(.4, 34, turnDirections.RIGHT, allianceColorAdj);
        }
        else if (startPos.getValue().equals("middle") && allianceColor.getValue().equals("blue")){
            telemetry.log().add("Move To Beacons From Mid");
            telemetry.update();

            driveENC(.7, 8, driveDirections.FORWARD);
            spinENC(.4, 40, turnDirections.LEFT, allianceColorAdj);
            driveENC(.9, 64, driveDirections.FORWARD);

            robot.buttonPushRight.setPosition(robot.BPR_MID);


            spinENC(.4, 47, turnDirections.RIGHT, allianceColorAdj);
        }
        else if (startPos.getValue().equals("line")){
            telemetry.log().add("Move to Beacons From Line");
            telemetry.update();

            spinENC(.4, 17, turnDirections.LEFT, allianceColorAdj);
            driveENC(.7, 90, driveDirections.FORWARD);

            if (allianceColor.getValue().equals("red")){
                robot.buttonPushLeft.setPosition(robot.BPL_MID);
            }
            else {
                robot.buttonPushRight.setPosition(robot.BPR_MID);
            }

            if (allianceColor.getValue().equals("red")) {

                spinENC(.4, 62, turnDirections.RIGHT, allianceColorAdj);
            }

            else if (allianceColor.getValue().equals("blue")){

                spinENC(.4, 62, turnDirections.RIGHT, allianceColorAdj);

            }

        }

        telemetry.log().add("Press First Beacon");
        telemetry.update();

        beacon(.45, driveDirections.BACKWARD, distFromWall);

        if (allianceColor.getValue().equals("blue")){
            spinENC(0.5, 3, turnDirections.LEFT, 1.0);
        }

        telemetry.log().add("Drive to Next Beacon");
        telemetry.update();

        driveENCRange(.9, 45, driveDirections.FORWARD, distFromWall);

        telemetry.log().add("Press Second Beacon");
        telemetry.update();

        beacon(.45, driveDirections.BACKWARD, distFromWall);

        if (allianceColor.getValue().equals("red")){
            robot.buttonPushLeft.setPosition(robot.BPL_IN);
        }
        else {
            robot.buttonPushRight.setPosition(robot.BPR_IN);
        }

        if (parkCorner.getValue()) {
            if (allianceColor.getValue().equals("red")) {
                spinENC(0.9, 10, turnDirections.LEFT, allianceColorAdj);
            }
            else {
                spinENC(0.9, 5, turnDirections.LEFT, allianceColorAdj);
            }

            driveENC(0.9, 80, driveDirections.BACKWARD);
        }
    }

    public void liftTusks() throws InterruptedException {
        robot.tuskServo.setPosition(robot.TUSK_UP);
        robot.liftMotor.setPower(0.5);
        Thread.sleep(250);
        robot.liftMotor.setPower(0.0);
    }

    public void beacon(double ispeed, driveDirections idir, double irange) throws InterruptedException {
        String dummyBeaconVal;

        //drive to white line
        telemetry.log().add("Drive to White Line");
        telemetry.addData("white_line", robot.color.red());
        telemetry.update();

        if (idir == driveDirections.FORWARD) {
            robot.rightMotorBack.setPower(ispeed);
            robot.rightMotorFront.setPower(ispeed);
            robot.leftMotorFront.setPower(ispeed);
            robot.leftMotorBack.setPower(ispeed);
        }
        else {
            robot.rightMotorBack.setPower(-ispeed);
            robot.rightMotorFront.setPower(-ispeed);
            robot.leftMotorFront.setPower(-ispeed);
            robot.leftMotorBack.setPower(-ispeed);
        }

        while (opModeIsActive() &&
               robot.color.red() < 6) {
            //rangeADJ(ispeed, idir, irange);
            dummyBeaconVal = beacon.getAnalysis().getColorString();
            //telemetry.addData("white_line", robot.color.red());
            //telemetry.update();
        }

        robot.rightMotorBack.setPower(0.0);
        robot.rightMotorFront.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);

        telemetry.log().add("Read Beacon Colors");
        telemetry.update();

        String beaconVal;
        String beaconValNext;
        String[] beaconVals;

        beaconValNext = "???,???";
        beaconVal = "???,???";
        beaconVals = beaconVal.split(",");

        while (!(beaconVals[0].equals("red") || beaconVals[0].equals("blue")) || !beaconValNext.equals(beaconVal)) {
            Thread.sleep(250);
            beaconVal = beaconValNext;
            beaconValNext = beacon.getAnalysis().getColorString();
            beaconVals = beaconVal.split(",");
//            telemetry.addData("Left", beaconVals[0]);
//            telemetry.update();
        }

        telemetry.log().add("Beacon Reading: " + beaconVal);
//        telemetry.log().add("Alliance color: " + allianceColor.getValue());
        telemetry.update();

        if (beaconVal.indexOf(allianceColor.getValue().charAt(0)) <4) {
            if (allianceColor.getValue().equals("red")){

                driveENC(.3, 4, driveDirections.FORWARD);
            }
            else {
                driveENC(0.3, 10, driveDirections.FORWARD);
            }
        }
        else {
            if (allianceColor.getValue().equals("red")){
                driveENC(.3, 10, driveDirections.FORWARD);
            }
            else {
                driveENC(0.3, 5, driveDirections.FORWARD);
            }
        }

        if (allianceColor.getValue().equals("red")){
            robot.buttonPushLeft.setPosition(robot.BPL_OUT);
            Thread.sleep(1200);
            robot.buttonPushLeft.setPosition(robot.BPL_MID);
            Thread.sleep(1000);
        }
        else {
            robot.buttonPushRight.setPosition(robot.BPR_OUT);
            Thread.sleep(1200);
            robot.buttonPushRight.setPosition(robot.BPR_MID);
            Thread.sleep(1000);
        }
    }
}