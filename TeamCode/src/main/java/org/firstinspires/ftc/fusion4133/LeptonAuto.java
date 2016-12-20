package org.firstinspires.ftc.fusion4133;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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


    public void parkCenterFromStart() {
        if (allianceColor.getValue().equals("Red") && startPos.getValue().equals("Center")) {

            driveENC(0.9, 4, driveDirections.FORWARD);

            spinENC(0.9, 40, turnDirections.LEFT);
            //pointENC(0.9, 36, turnDirections.LEFT);

            driveENC(0.9, 58, driveDirections.FORWARD);

            spinENC(0.9, 110, turnDirections.LEFT);


            //   driveENC(0.9, 4, driveDirections.BACKWARD);
        } else if (allianceColor.getValue().equals("Red") && startPos.getValue().equals("Corner")) {

            pointENC(0.9, 5, turnDirections.LEFT);

            driveENC(0.9, 50, driveDirections.FORWARD);

            spinENC(0.9, 15, turnDirections.LEFT);

            driveENC(0.9, 12, driveDirections.BACKWARD);

            spinENC(0.9, 90, turnDirections.LEFT);
        } else if (allianceColor.getValue().equals("Blue") && startPos.getValue().equals("Center")) {

            driveENC(0.9, 4, driveDirections.FORWARD);

            spinENC(0.9, 38, turnDirections.RIGHT);
            //pointENC(0.9, 45, turnDirections.RIGHT);

            driveENC(0.9, 54, driveDirections.FORWARD);

            spinENC(0.9, 100, turnDirections.RIGHT);

            driveENC(0.9, 15, driveDirections.BACKWARD);

            //  driveENC(0.9, 4, driveDirections.BACKWARD);
        } else if (allianceColor.getValue().equals("Blue") && startPos.getValue().equals("Corner")) {

            pointENC(0.9, 5, turnDirections.RIGHT);

            driveENC(0.9, 50, driveDirections.FORWARD);

            spinENC(0.9, 15, turnDirections.RIGHT);

            driveENC(0.9, 12, driveDirections.BACKWARD);

            spinENC(0.9, 90, turnDirections.RIGHT);
        }
    }

    public void parkCornerFromStart() {
        if (allianceColor.getValue().equals("Red") && startPos.getValue().equals("Center")) {

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.LEFT);

            driveENC(0.9, 70, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.LEFT);

            driveENC(0.9, 27, driveDirections.FORWARD);

        } else if (allianceColor.getValue().equals("Red") && startPos.getValue().equals("Corner")) {

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.LEFT);

            driveENC(0.9, 15, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.LEFT);

            driveENC(0.9, 15, driveDirections.FORWARD);

        } else if (allianceColor.getValue().equals("Blue") && startPos.getValue().equals("Center")) {

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.RIGHT);

            driveENC(0.9, 70, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.RIGHT);

            driveENC(0.9, 27, driveDirections.FORWARD);

        } else if (allianceColor.getValue().equals("Blue") && startPos.getValue().equals("Corner")) {

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.RIGHT);

            driveENC(0.9, 15, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.RIGHT);

            driveENC(0.9, 30, driveDirections.FORWARD);

        }

    }

    /* @Override
     public void runOpMode() throws InterruptedException {
         robot.init(hardwareMap);

         selectOptions();

         waitForStart();
         driveENC(0.9, 36, driveDirections.FORWARD);
         sleep(5000);
     }
     */
    @Override
    public void runOpMode() throws InterruptedException {

        String beaconVal;
        String beaconValNext;
        String[] beaconVals;
        robot.init(hardwareMap);

        waitForVisionStart();

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
        waitForStart();
/*
        beaconValNext = "???,???";
        beaconVal = "???,???";
        beaconVals = beaconVal.split(",");

        while (!(beaconVals[0].equals("red") || beaconVals[0].equals("blue")) || !beaconValNext.equals(beaconVal)) {
            Thread.sleep(100);
            beaconVal = beaconValNext;
            beaconValNext = beacon.getAnalysis().getColorString();
            beaconVals = beaconVal.split(",");
            telemetry.addData("Left", beaconVals[0]);
            telemetry.update();
        }

        telemetry.addData("Beacon Reading", beaconVal);
        telemetry.update();

        if (beaconVal.indexOf("r") < 4) {
            robot.buttonPushRight.setPosition(robot.BPR_OUT);
            Thread.sleep(5000);
        }
*/
        spinENC(0.9, 90, turnDirections.RIGHT);
        /*
       Thread.sleep((long) (waitStart.getValue() * 1000));

        if (parkCenter.getValue() && !pressBeacons.getValue()) {
            parkCenterFromStart();
        }
        if (!parkCenter.getValue() && !pressBeacons.getValue()){
            parkCornerFromStart();
        }
        */

    }
}