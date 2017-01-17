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
    double distFromWall = 15;


    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        telemetry.addData("step", "robot initalized");
        telemetry.update();

        robot.color.enableLed(false);
        robot.color.enableLed(true);

        waitForVisionStart();

        telemetry.addData("step", "vision start");
        telemetry.update();

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

        selectOptions();

        if (allianceColor.getValue().equals("red")){
            allianceColorAdj = 1.0;
            robot.range = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeLeft");
        }
        else {
            allianceColorAdj = -1.0;
            robot.range = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeRight");
        }

        waitForStart();

        telemetry.addData("step", "program start");

        Thread.sleep((long) (waitStart.getValue() * 1000));

        if (startPos.getValue().equals("line")) {
            driveENC(0.7, 12, driveDirections.FORWARD);
            spinENC(0.2, 42, turnDirections.LEFT, allianceColorAdj);
            driveENC(0.4, 2, driveDirections.FORWARD);
            Thread.sleep(250);
        }

        telemetry.addData("step", "shoot balls");
        telemetry.update();

        shootBalls();

        Thread.sleep((long) (waitShoot.getValue() * 1000));

        if (pressBeacons.getValue() && startPos.getValue().equals("middle")){
            driveENC(.7, 8, driveDirections.FORWARD);
            spinENC(.4, 40, turnDirections.LEFT, allianceColorAdj);
            driveENC(.9, 60, driveDirections.FORWARD);
            if (allianceColor.getValue().equals("red")){
                robot.buttonPushLeft.setPosition(robot.BPL_MID);
            }
            else {
                robot.buttonPushRight.setPosition(robot.BPR_MID);
            }
            spinENC(.4, 34, turnDirections.RIGHT, allianceColorAdj);
        }
        if (pressBeacons.getValue() && startPos.getValue().equals("line")){
            spinENC(.4, 17, turnDirections.LEFT, allianceColorAdj);
            driveENC(.7, 90, driveDirections.FORWARD);
            if (allianceColor.getValue().equals("red")){
                robot.buttonPushLeft.setPosition(robot.BPL_MID);
            }
            else {
                robot.buttonPushRight.setPosition(robot.BPR_MID);
            }
            spinENC(.4, 62, turnDirections.RIGHT, allianceColorAdj);
        }

        telemetry.addData("step", "beacon1 backward");
        telemetry.update();

        beacon(.45, driveDirections.BACKWARD, distFromWall);
        if (allianceColor.getValue().equals("red")){
            robot.buttonPushLeft.setPosition(robot.BPL_MID);
        }
        else {
            robot.buttonPushRight.setPosition(robot.BPR_MID);
        }
        driveENCRange(.9, 45, driveDirections.FORWARD, distFromWall);

        telemetry.addData("step", "beacon2 forward");
        telemetry.update();

        beacon(.45, driveDirections.BACKWARD, distFromWall);

        if (allianceColor.getValue().equals("red")){
            robot.buttonPushLeft.setPosition(robot.BPL_IN);
        }
        else {
            robot.buttonPushRight.setPosition(robot.BPR_IN);
        }
        driveENC(0.9, 80, driveDirections.BACKWARD);

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
        telemetry.log().add("step: beacon");
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
                robot.color.red() < 6
                ) {
            //rangeADJ(ispeed, idir, irange);
            dummyBeaconVal = beacon.getAnalysis().getColorString();
            telemetry.addData("white_line", robot.color.red());
            telemetry.update();
        }

        robot.rightMotorBack.setPower(0.0);
        robot.rightMotorFront.setPower(0.0);
        robot.leftMotorFront.setPower(0.0);
        robot.leftMotorBack.setPower(0.0);

        String beaconVal;
        String beaconValNext;
        String[] beaconVals;

        beaconValNext = "???,???";
        beaconVal = "???,???";
        beaconVals = beaconVal.split(",");

        while (!(beaconVals[0].equals("red") || beaconVals[0].equals("blue")) || !beaconValNext.equals(beaconVal)) {
            Thread.sleep(200);
            beaconVal = beaconValNext;
            beaconValNext = beacon.getAnalysis().getColorString();
            beaconVals = beaconVal.split(",");
            telemetry.addData("Left", beaconVals[0]);
            telemetry.update();
        }

        telemetry.log().add("Beacon Reading: " + beaconVal);
        telemetry.log().add("Alliance color: " + allianceColor.getValue());
        telemetry.log().add("alliance char: " + allianceColor.getValue().charAt(0));
        telemetry.log().add("color found at: " + Integer.toString(beaconVal.indexOf(allianceColor.getValue().charAt(0))));
        telemetry.update();

        if (beaconVal.indexOf(allianceColor.getValue().charAt(0)) <4) {
            if (idir == driveDirections.FORWARD) {
                driveENC(.3, 1, driveDirections.BACKWARD);
            }
            else {
                driveENC(.3, 4, driveDirections.FORWARD);
            }
        }
        else {
            if (idir == driveDirections.FORWARD) {
                driveENC(.3, 6, driveDirections.FORWARD);
            }
            else {
                driveENC(.3, 10, driveDirections.FORWARD);
            }
        }


        if (allianceColor.getValue().equals("red")){
            robot.buttonPushLeft.setPosition(robot.BPL_OUT);
            Thread.sleep(1000);
            robot.buttonPushLeft.setPosition(robot.BPL_MID);
            Thread.sleep(1000);
        }
        else {
            robot.buttonPushRight.setPosition(robot.BPR_OUT);
            Thread.sleep(1000);
            robot.buttonPushRight.setPosition(robot.BPR_MID);
            Thread.sleep(1000);
        }
    }
}