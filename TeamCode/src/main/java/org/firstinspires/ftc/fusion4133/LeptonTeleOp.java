package org.firstinspires.ftc.fusion4133;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by Fusion on 10/26/2016.
 */
@TeleOp(name="Lepton: TeleOp", group="Lepton")
public class LeptonTeleOp extends OpMode{

    LeptonHardwareSetup robot       = new LeptonHardwareSetup(); // use the class created to define a Pushbot's hardware

    @Override
    public void init() {
        telemetry.addData("Step", "Initializing");
        telemetry.update();

        robot.init(hardwareMap);

        telemetry.addData("Step", "Init Complete");
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void loop() {
        telemetry.addData("Step", "Running");

        double leftPower;
        double rightPower;
        double bpInc = 0.001;
        double tuskINC = 0.002;

        // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)
        leftPower  = -gamepad1.left_stick_y;
        rightPower = -gamepad1.right_stick_y;
        robot.leftMotorFront.setPower(leftPower);
        robot.leftMotorBack.setPower(leftPower);
        robot.rightMotorFront.setPower(rightPower);
        robot.rightMotorBack.setPower(rightPower);

        if(gamepad1.right_trigger > 0.3){
            robot.collectionMotor.setPower(1.0);
        }
        else if (gamepad1.left_trigger > 0.3){
            robot.collectionMotor.setPower(-1.0);
        }
        else {
            robot.collectionMotor.setPower(0.0);
        }

        //Run lift with analog stick
        robot.liftMotor.setPower(gamepad2.left_stick_y);

        //button pusher section
        if (gamepad1.y) {
            robot.buttonPushLeft.setPosition(Math.min(robot.buttonPushLeft.getPosition() + bpInc, robot.BPL_IN));
        }
        else if (gamepad1.x) {
            robot.buttonPushLeft.setPosition(Math.max(robot.buttonPushLeft.getPosition() - bpInc, robot.BPL_OUT));
        }

        if (gamepad1.b) {
            robot.buttonPushRight.setPosition(Math.max(robot.buttonPushRight.getPosition() - bpInc, robot.BPR_OUT));
        }
        else if (gamepad1.a){
            robot.buttonPushRight.setPosition(Math.min(robot.buttonPushRight.getPosition() + bpInc, robot.BPR_IN));
        }



        if (gamepad2.a) {
            robot.tuskServo.setPosition(Math.min(robot.tuskServo.getPosition() + tuskINC, robot.TUSK_DOWN));
        }
        else if (gamepad2.y) {
            robot.tuskServo.setPosition(Math.max(robot.tuskServo.getPosition() - tuskINC, robot.TUSK_UP));
        }

        // Send telemetry message to signify robot running;
        telemetry.addData("left",  "%.2f", leftPower);
        telemetry.addData("right", "%.2f", rightPower);
    }
}
