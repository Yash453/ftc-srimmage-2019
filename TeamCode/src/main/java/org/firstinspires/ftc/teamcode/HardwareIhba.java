/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.GoldAlignDetector;
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.GyroSensor;
import org.opencv.core.Size;
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.qualcomm.robotcore.hardware.DigitalChannel;


/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class HardwareIhba
{

    public int twentyMotorClicks = 560;
    public int fortyMotorClicks = 1120;
    public int sixtyMotorClicks = 2240;

//    public SamplingOrderDetector samplingDetector;
    public GoldAlignDetector alignDetector;

    /* Public OpMode members. */
    public DcMotor  frontLeft   = null;
    public DcMotor  frontRight  = null;
    public DcMotor  backLeft    = null;
    public DcMotor  backRight   = null;
    public DcMotor  latch       = null;
    
    public DcMotor  arm         = null;
    public DcMotor  extension   = null;
    public DcMotor  intake      = null;

    //ModernRoboticsI2cRangeSensor range;
    IntegratingGyroscope gyro1;
    ModernRoboticsI2cGyro modernRoboticsI2cGyro1;

    IntegratingGyroscope gyro2;
    ModernRoboticsI2cGyro modernRoboticsI2cGyro2;

    //public DigitalChannel digitalTouch;
    DigitalChannel magneticLimitSwitch;  // Hardware Device Object
    //DigitalChannel magneticLimitSwitch2;

    public CRServo marker = null;
   // public CRServo intake = null;
////      rjberry@rjcontrol.com
//
    RevBlinkinLedDriver blinkinLedDriver;


    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareIhba(){ }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        frontLeft  = hwMap.get(DcMotor.class, "FrontLeft");
        frontRight = hwMap.get(DcMotor.class, "FrontRight");
        backLeft  = hwMap.get(DcMotor.class, "BackLeft");
        backRight = hwMap.get(DcMotor.class, "BackRight");
        latch = hwMap.get(DcMotor.class, "Latch");
        arm = hwMap.get(DcMotor.class, "Arm");
        extension = hwMap.get(DcMotor.class,"Extension");
        intake = hwMap.get(DcMotor.class, "Intake");

        modernRoboticsI2cGyro1 = hwMap.get(ModernRoboticsI2cGyro.class, "Gyro");
        gyro1 = (IntegratingGyroscope)modernRoboticsI2cGyro1;
        modernRoboticsI2cGyro2 = hwMap.get(ModernRoboticsI2cGyro.class, "Gyro2");
        gyro2 = (IntegratingGyroscope)modernRoboticsI2cGyro2;

        magneticLimitSwitch = hwMap.get(DigitalChannel.class, "sensor_digital");
        magneticLimitSwitch.setMode(DigitalChannel.Mode.INPUT);
//
//        magneticLimitSwitch2 = hwMap.get(DigitalChannel.class, "sensor_digital2");
//        magneticLimitSwitch2.setMode(DigitalChannel.Mode.INPUT);


        frontLeft.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors/
        frontRight.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        backLeft.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        backRight.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors/
        latch.setDirection(DcMotor.Direction.REVERSE);
        arm.setDirection(DcMotor.Direction.FORWARD);
        extension.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);

        // Set all motors to zero power
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        latch.setPower(0);
        arm.setPower(0);
        extension.setPower(0);
        intake.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);   // is this going to use an encoder?
        extension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define and initialize ALL installed servos.


//        marker = hwMap.get(CRServo.class ,"Marker");
//        marker.setPower(0);
        marker = hwMap.get(CRServo.class,"Marker");
        marker.setPower(0);
//        intake = hwMap.get(CRServo.class, "Intake");
//        intake.setPower(0);

//a
        blinkinLedDriver = hwMap.get(RevBlinkinLedDriver.class, "blinkin");
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.LARSON_SCANNER_GRAY);    // change color if necessary
//

        // Set up detector
//        samplingDetector = new SamplingOrderDetector(); // Create the detector
//        samplingDetector.init(hwMap.appContext, CameraViewDisplay.getInstance()); // Initialize detector with app context and camera
//        samplingDetector.useDefaults(); // Set detector to use default settings
//
//        samplingDetector.downscale = 0.4; // How much to downscale the input frames
//
//        // Optional tuning
//        samplingDetector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
//        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
//        samplingDetector.maxAreaScorer.weight = 0.001;
//
//        samplingDetector.ratioScorer.weight = 15;
//        samplingDetector.ratioScorer.perfectRatio = 1.0;
//
//        samplingDetector.enable(); // Start detector



        // Set up detector
        alignDetector = new GoldAlignDetector(); // Create detector
        alignDetector.init(hwMap.appContext, CameraViewDisplay.getInstance()); // Initialize it with the app context and camera
        alignDetector.useDefaults(); // Set detector to use default settings
        // Optional tuning
        alignDetector.alignSize = 100; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        alignDetector.alignPosOffset = 0; // How far from center frame to offset this alignment zone.
        alignDetector.downscale = 0.4; // How much to downscale the input frames

        alignDetector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        alignDetector.maxAreaScorer.weight = 0.005; //

        alignDetector.ratioScorer.weight = 5; //
        alignDetector.ratioScorer.perfectRatio = 1.0; // Ratio adjustment

        alignDetector.enable(); // Start the detector!
    }

 }

 /* stuff to do in order of importance

 - only TEST gyro strafe
 - PixyCam
 - fix gyro straight/turn coefficients (make it smoother) (depending on time)
 - autonomous for both sides

 - gyro strafe
 - talking robot
 - ConceptRampMotorSpeed
 - lerp / accelerate motors when starting motion


 - Odometry wheels
*/