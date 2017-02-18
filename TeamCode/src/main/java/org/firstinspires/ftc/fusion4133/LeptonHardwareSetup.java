package org.firstinspires.ftc.fusion4133;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbServoController;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoEx;
import com.qualcomm.robotcore.hardware.configuration.ServoControllerConfiguration;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorMRRangeSensor;

/**
 * Created by Fusion on 10/26/2016.
 */
//we have a hardware setup so that when making a program for are robot we don't have have to redefine everything every time.
public class LeptonHardwareSetup {

    /* Public OpMode members. */
    //these are the null statements to make sure nothing is stored in the variables.
    //we do this so the program dose not get confused.
    public DcMotor leftMotorBack   = null;
    public DcMotor leftMotorFront  = null;
    public DcMotor rightMotorBack  = null;
    public DcMotor rightMotorFront = null;
    public DcMotor liftMotor       = null;
    public DcMotor collectionMotor = null;
    public DcMotor popperMotor     = null;

//    public Servo   buttonPushLeft  = null;
    public Servo   buttonPushLeft  = null;
    public Servo   buttonPushRight = null;
    public Servo   tuskServo       = null;
    public Servo   hornServo       = null;

    //this is where our sensors are defined.
    public GyroSensor                     gyro     = null;
    public ColorSensor                    color    = null;
    public ModernRoboticsI2cRangeSensor   range    = null;


    //these are som of the servo positions that we define so that it is eaiser to wright the program later on.
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    final static double MOTOR_STOP        = 0.0;  //this is how we make sure in int we are completely stopped.
    final static double BPR_IN            = 0.58; //this is where the right sides button pusher is fully in.
    final static double BPR_OUT           = 0.11; //this is how we make sure that we are getting the full range of the servo.
    final static double BPR_MID           = 0.33;
    final static double BPL_IN            = 0.55; //this is where the left sides button pusher is fully in.
    final static double BPL_OUT           = 0.11; //this is how we make sure that we are getting the full range of the servo.
    final static double BPL_MID           = 0.33;
    final static double TUSK_UP           = 0.0; //this is how we get the tusk all the way up.
    final static double TUSK_DOWN         = 1.0; //this is how we make sure that we are getting the full range of the servo.
    final static double TUSK_GRAB         = 0.7; //this is the position the tusks need to be in to grab the cap ball
    final static double TUSK_READY        = 0.5; //this is the position we go to right before we grab the ball
    final static double HORN_UP           = 1.0;
    final static double HORN_RELEASED     = 0.0;

    /* Constructor */
    //this is where all other hard ware mapping is done.
    public LeptonHardwareSetup(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        /************************************************************
         * MOTOR SECTION
         ************************************************************/
        // Define Motors
        //this is to put in the variable that we will put in the config file.
        leftMotorFront   = hwMap.dcMotor.get("lmf");
        leftMotorBack    = hwMap.dcMotor.get("lmb");
        rightMotorFront  = hwMap.dcMotor.get("rmf");
        rightMotorBack   = hwMap.dcMotor.get("rmb");
        liftMotor        = hwMap.dcMotor.get("lm");
        collectionMotor  = hwMap.dcMotor.get("cm");
        popperMotor      = hwMap.dcMotor.get("pm");

        //Set the direction of motors
        leftMotorFront.setDirection(DcMotor.Direction.REVERSE); //we have to set two motors on the same side in revers so that the robot goes forward on both sides and dose not spin
        leftMotorBack.setDirection(DcMotor.Direction.REVERSE);
        rightMotorFront.setDirection(DcMotor.Direction.FORWARD);
        rightMotorBack.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        collectionMotor.setDirection(DcMotor.Direction.REVERSE);//this reversed so that the collection motor goes forward.
        popperMotor.setDirection(DcMotor.Direction.FORWARD);//this reversed so that the collection motor goes forward.

        //We do this to make sure that are robot dosent move in inizalize.
        leftMotorFront.setPower(MOTOR_STOP);
        leftMotorBack.setPower(MOTOR_STOP);
        rightMotorFront.setPower(MOTOR_STOP);
        rightMotorBack.setPower(MOTOR_STOP);
        liftMotor.setPower(MOTOR_STOP);
        collectionMotor.setPower(MOTOR_STOP);
        popperMotor.setPower(MOTOR_STOP);


        //the drive motors are preset to  run with encoders so we don't have to add this in autonomus.
        leftMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotorBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        collectionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        popperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        /************************************************************
         * SERVO SECTION
         ************************************************************/
        //Define servos
        //this is to put in the variable that we will put in the config file.
        buttonPushLeft   = hwMap.servo.get("bpl");
        buttonPushRight  = hwMap.servo.get("bpr");
        tuskServo        = hwMap.servo.get("ts");
        hornServo        = hwMap.servo.get("hs");

        //Initialize servo positions so they are completely in
        buttonPushRight.setPosition(BPR_IN);
        buttonPushLeft.setPosition(BPL_IN);
        tuskServo.setPosition(TUSK_DOWN);
        hornServo.setPosition(HORN_UP);

        /************************************************************
         * SENSOR SECTION
         ************************************************************/
        //Define sensors
        //this are what they ar identifide as in the config file.
        gyro   = hwMap.gyroSensor.get("gyro");
        color  = hwMap.colorSensor.get("color");
        //range  = hwMap.get(ModernRoboticsI2cRangeSensor.class, "range");
    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }

}

