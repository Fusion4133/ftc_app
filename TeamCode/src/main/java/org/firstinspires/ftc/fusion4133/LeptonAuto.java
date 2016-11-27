package org.firstinspires.ftc.fusion4133;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.lang.reflect.Method;

/**
 * Created by Fusion on 11/2/2016.
 */
@Autonomous(name="Lepton: Autonomous", group="Lepton")
public class LeptonAuto extends LeptonAutoSetup {

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
        if (allianceColor.getValue().equals("Red") && startPos.getValue().equals("Center")){

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.LEFT);

            driveENC(0.9, 70, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.LEFT);

            driveENC(0.9, 27, driveDirections.FORWARD);

        } else if (allianceColor.getValue().equals("Red") && startPos.getValue().equals("Corner")){

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.LEFT);

            driveENC(0.9, 15, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.LEFT);

            driveENC(0.9, 15, driveDirections.FORWARD);

        } else if (allianceColor.getValue().equals("Blue") && startPos.getValue().equals("Center")){

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.RIGHT);

            driveENC(0.9, 70, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.RIGHT);

            driveENC(0.9, 27, driveDirections.FORWARD);

        } else if (allianceColor.getValue().equals("Blue") && startPos.getValue().equals("Corner")){

            driveENC(0.9, 2, driveDirections.FORWARD);

            pointENC(0.9, 45, turnDirections.RIGHT);

            driveENC(0.9, 15, driveDirections.FORWARD);

            pointENC(0.9, 90, turnDirections.RIGHT);

            driveENC(0.9, 30, driveDirections.FORWARD);

        }

    }

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        selectOptions();

        waitForStart();

        Thread.sleep((long) (waitStart.getValue() * 1000));

        if (parkCenter.getValue() && !pressBeacons.getValue()) {
            parkCenterFromStart();
        }
        if (!parkCenter.getValue() && !pressBeacons.getValue()){
            parkCornerFromStart();
        }
    }
}