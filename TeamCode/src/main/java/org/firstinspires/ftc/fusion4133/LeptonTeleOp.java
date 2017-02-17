package org.firstinspires.ftc.fusion4133;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Fusion on 10/26/2016.
 */
@TeleOp(name="Lepton: TeleOp", group="Lepton")
public class LeptonTeleOp extends OpMode{

    LeptonHardwareSetup robot       = new LeptonHardwareSetup(); // use the class created to define a Pushbot's hardware

    double leftPower;
    double rightPower;
    double bpInc = 0.001;
    double tuskINC = 0.002;
    double popperPower = 0.8;
    String collectionState;
    boolean triggerPressed;
    boolean reversedTriggerPressed;
    DcMotor leftDriveFront;
    DcMotor leftDriveBack;
    DcMotor rightDriveFront;
    DcMotor rightDriveBack;
    double dirADJ;

    @Override
    public void init() {
        //telemetry.addData("Step", "Initializing");
        //telemetry.update();

        collectionState = "off";

        triggerPressed = false;

        reversedTriggerPressed = false;

        dirADJ = -1.0;

        robot.init(hardwareMap);
        leftDriveFront = robot.leftMotorFront;
        leftDriveBack = robot.leftMotorBack;
        rightDriveFront = robot.rightMotorFront;
        rightDriveBack = robot.rightMotorBack;
        robot.leftMotorBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftMotorFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightMotorBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightMotorFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

       // telemetry.addData("Step", "Init Complete");
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void loop() {
       // telemetry.addData("Step", "Running");

        //JOYSTICK 1
        // This made sure that are two motors are going the same speed
        leftPower  = gamepad1.left_stick_y * dirADJ;
        rightPower = gamepad1.right_stick_y * dirADJ;
        leftDriveFront.setPower(leftPower);
        leftDriveBack.setPower(leftPower);
        rightDriveFront.setPower(rightPower);
        rightDriveBack.setPower(rightPower);

        if (gamepad1.a){
            leftDriveFront = robot.rightMotorBack;
            leftDriveBack = robot.rightMotorFront;
            rightDriveFront = robot.leftMotorBack;
            rightDriveBack = robot.leftMotorFront;

             dirADJ = 1.0;

        }
        else if (gamepad1.b){
            leftDriveFront = robot.leftMotorFront;
            leftDriveBack = robot.leftMotorBack;
            rightDriveFront = robot.rightMotorFront;
            rightDriveBack = robot.rightMotorBack;

            dirADJ = -1.0;

        }

        //Run collection system with triggers
        if(gamepad1.right_trigger > 0.3 && !triggerPressed){
            if (collectionState.equals("forward")){
                collectionState = "off";
                robot.collectionMotor.setPower(0.0);
            }
            else {
                collectionState = "forward";
                robot.collectionMotor.setPower(1.0);
            }
        }
        triggerPressed = gamepad1.right_trigger > 0.3;

        if(gamepad1.left_trigger > 0.3 && !reversedTriggerPressed){
            if (collectionState.equals("backward")){
                collectionState = "off";
                robot.collectionMotor.setPower(0.0);
            }
            else {
                collectionState = "backward";
                robot.collectionMotor.setPower(-1.0);
            }
        }
        reversedTriggerPressed = gamepad1.left_trigger > 0.3;

       //JOYSTICK 2
        //Run lift with joy stick
        robot.liftMotor.setPower(gamepad2.right_stick_y);

        //button pusher section
        if (gamepad2.y) {
            robot.buttonPushLeft.setPosition(Math.min(robot.buttonPushLeft.getPosition() + bpInc, robot.BPL_IN));
        }
        else if (gamepad2.x) {
            robot.buttonPushLeft.setPosition(Math.max(robot.buttonPushLeft.getPosition() - bpInc, robot.BPL_OUT));
        }

        if (gamepad2.b) {
            robot.buttonPushRight.setPosition(Math.max(robot.buttonPushRight.getPosition() - bpInc, robot.BPR_OUT));
        }
        else if (gamepad2.a){
            robot.buttonPushRight.setPosition(Math.min(robot.buttonPushRight.getPosition() + bpInc, robot.BPR_IN));
        }


        //tusks go up when up on d-pad is pressed.
        if (gamepad2.dpad_up) {
            robot.tuskServo.setPosition(robot.TUSK_UP);
        }
        //tusk go down when in down on d-pad is pressed.
        else if (gamepad2.dpad_down) {
            robot.tuskServo.setPosition(robot.TUSK_DOWN);
        }
        //tusk goes to the grabing position for the ball.
        else if (gamepad2.dpad_left) {
            robot.tuskServo.setPosition(robot.TUSK_GRAB);
        }
        //this is the initial tusk position.
        else if (gamepad2.dpad_right) {
            robot.tuskServo.setPosition(robot.TUSK_READY);
        }
        //telemetry.addData("tuskServo", robot.tuskServo.getPosition());

        //horn servo is not in use.
        if (gamepad2.left_bumper) {
            robot.hornServo.setPosition(robot.HORN_RELEASED);
        }

        //popper
        if (gamepad2.right_trigger > 0.3) {
            robot.popperMotor.setPower(popperPower);
        }
        else {
            robot.popperMotor.setPower(0.0);
        }
       // if (gamepad1.a) {
       //     popperPower = popperPower + .05;
       //  }
       // else if (gamepad1.b) {
       //     popperPower = popperPower - .05;
       // }

        // Send telemetry message to signify robot running;
     /*   telemetry.addData("rightBack", Integer.toString(robot.rightMotorBack.getCurrentPosition()));
        telemetry.addData("leftFront", Integer.toString(robot.leftMotorFront.getCurrentPosition()));
        telemetry.addData("leftBack", Integer.toString(robot.leftMotorBack.getCurrentPosition()));
        telemetry.addData("rightFront", Integer.toString(robot.rightMotorFront.getCurrentPosition()));
        */
       /* telemetry.addData("left",  "%.2f", leftPower);
        telemetry.addData("right", "%.2f", rightPower);
        telemetry.addData("Servo position left","%.2f", robot.buttonPushLeft.getPosition());
        telemetry.addData("Servo position right","%.2f", robot.buttonPushRight.getPosition());
        telemetry.addData("trigger pos", "%.2f", gamepad2.right_trigger);
        */
    }
}
