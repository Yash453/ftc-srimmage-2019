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

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;

/**
 * Created by Neha Deshpande, FTC 12116 on 12/9/2018.
 */
@Autonomous(name="3.0 Depot", group="Neha")
@Disabled
public class AutoDepot3 extends LinearOpMode {

    /* Declare OpMode members. */
    HardwareIhba robot = new HardwareIhba();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();


    static final double COUNTS_PER_MOTOR_REV = 560;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;         // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    // These constants define the desired driving/control characteristics
    // The can/should be tweaked to suite the specific robot drive train.
    static final double     DRIVE_SPEED             = 0.07;
    static final double     TURN_SPEED              = 0.07;
    static final double     SLOW_STRAFE_SPEED       = 0.3;
    static final double     STRAFE_SPEED            = 0.2;


    static final double HEADING_THRESHOLD = 1;      // As tight as we can make it with an integer gyro
    static final double P_TURN_COEFF = 0.1;     // Larger is more responsive, but also less stable
    static final double P_DRIVE_COEFF = 0.15;     // Larger is more responsive, but also less stable

    static final double MARKER_RETRACTED = 0.45;
    static final double MARKER_EXTENDED = 0.9;

    double color_num = 3; // 3 = blue, 10 = red
    double current_distance_from_wall = -1;
    double currentAngle = 0;

    String goldlocation = "UNKNOWN";
    double holdTime = 0.5;
    int mineralAngle = 45;


    @Override
    public void runOpMode() throws InterruptedException{

        /*
         * Initialize the standard drive system variables.
         * The init() method of the hardware class does most of the work here
         */
        robot.init(hardwareMap);
        //robot.detector.enable();

        // Ensure the robot is stationary, then reset the encoders and calibrate the gyro.
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.latch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Send telemetry message to alert driver that we are calibrating;
        telemetry.addData(">", "Calibrating Gyro");    //
        telemetry.update();

        robot.modernRoboticsI2cGyro1.calibrate();
        robot.modernRoboticsI2cGyro2.calibrate();

        // make sure the gyro is calibrated before continuing
        while (!isStopRequested() && robot.modernRoboticsI2cGyro1.isCalibrating()) {
            sleep(50);
            idle();
        }
        while (!isStopRequested() && robot.modernRoboticsI2cGyro2.isCalibrating()) {
            sleep(50);
            idle();
        }

        telemetry.addData(">", "Robot Ready.");
        telemetry.update();

        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (Display Gyro value), and reset gyro before we move.
        while (!isStarted() && !isStopRequested()) {
            telemetry.addData(">", "Robot Heading = %d", robot.modernRoboticsI2cGyro1.getIntegratedZValue());
            telemetry.update();
            telemetry.addData(">", "Robot Heading 2 = %d", robot.modernRoboticsI2cGyro2.getIntegratedZValue());
            telemetry.update();
            //goldlocation = robot.detector.getCurrentOrder().toString();    // scan minerals
            //goldlocation = robot.samplingDetector.getLastOrder().toString();
            telemetry.addData("Mineral is on the robot's ", goldlocation);
            telemetry.update();

        }
        //telemetry.addData("Chosen: ", goldlocation);
        //telemetry.update();

        robot.modernRoboticsI2cGyro1.resetZAxisIntegrator();
        robot.modernRoboticsI2cGyro2.resetZAxisIntegrator();


        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        // Put a hold after each turn


        // LEFT MEANS THE ROBOT'S LEFT
        //robot.samplingDetector.disable();

        unlatch();

        sleep(500);

        encoderDrive(DRIVE_SPEED,10,10,1);
        gyroHold(DRIVE_SPEED,currentAngle,holdTime);

        sleep(500);

        currentAngle = 35;
        gyroTurn(DRIVE_SPEED,currentAngle);
        gyroHold(DRIVE_SPEED,currentAngle,holdTime);

        sleep(500);

        while(opModeIsActive() && !robot.alignDetector.getAligned()){
                robot.frontRight.setPower(-0.03);
                robot.backRight.setPower(-0.03);
                robot.frontLeft.setPower(0.03);
                robot.backLeft.setPower(0.03);
        }
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
        //at this point, robot is facing mineral
        sleep(500);

        currentAngle = robot.modernRoboticsI2cGyro1.getHeading();

        for(int i = 0; i < 3;i++) {
            runtime.reset();
            while (runtime.seconds() < 0.7) {
                robot.frontRight.setPower(0.2);
                robot.backRight.setPower(0.2);
                robot.frontLeft.setPower(0.1);
                robot.backLeft.setPower(0.1);
            }
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.backRight.setPower(0);
            sleep(100);
        }
        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
        for(int i = 0; i < 2;i++) {
            runtime.reset();
            while (runtime.seconds() < 0.7) {
                robot.frontRight.setPower(0.3);
                robot.backRight.setPower(0.3);
                robot.frontLeft.setPower(0.2);
                robot.backLeft.setPower(0.2);
            }
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.backRight.setPower(0);
            sleep(100);
        }
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);


        if (currentAngle > 15){
            gyroTurn(TURN_SPEED,-45);
            gyroHold(TURN_SPEED,-45,holdTime);
        }
        else if (currentAngle < -15){
            gyroTurn(TURN_SPEED,45);
            gyroHold(TURN_SPEED,45,holdTime);
        }
        else{
            gyroTurn(TURN_SPEED,0);
            gyroHold(TURN_SPEED,0,holdTime);
        }

        runtime.reset();
        robot.marker.setPower(1);
        while (opModeIsActive() && runtime.seconds() < 1.5) { }
        runtime.reset();
        robot.marker.setPower(-1);
        while (opModeIsActive() && runtime.seconds() < 0.5) { }
        runtime.reset();
        robot.marker.setPower(1);
        while (opModeIsActive() && runtime.seconds() < 1.5) {
        }

        for(int i = 0; i < 1;i++) {
            runtime.reset();
            while (runtime.seconds() < 0.5) {
                robot.frontRight.setPower(-0.3);
                robot.backRight.setPower(-0.3);
                robot.frontLeft.setPower(-0.2);
                robot.backLeft.setPower(-0.2);
            }
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.backRight.setPower(0);
            sleep(100);
        }

        //remove marker

        //knock off mineral
//        encoderDrive(DRIVE_SPEED,100,100,1.5);
//        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
//
//        sleep(250);



//        //come back
//        encoderDrive(DRIVE_SPEED,33,33,1.5);
//        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
//
//        // turn facing depot
//        currentAngle = 0;
//        gyroTurn(TURN_SPEED,currentAngle);
//        gyroHold(TURN_SPEED,currentAngle,holdTime);
//
//        //move forwards
//        encoderDrive(DRIVE_SPEED,15,15,1.5);
//        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
//        //remove marker
//        removeMarker();
//
//        //move back
//        encoderDrive(DRIVE_SPEED,-15,-15,1.5);
//        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
//
//        //turn towards crater
//        currentAngle = 45;
//        gyroTurn(TURN_SPEED,currentAngle);
//        gyroHold(TURN_SPEED,currentAngle,holdTime);
//
//        // move forwards
//        encoderDrive(DRIVE_SPEED,-33,-33,1.5);
//        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
//
//        //turn again
//        currentAngle = 135;
//        gyroTurn(TURN_SPEED,currentAngle);
//        gyroHold(TURN_SPEED,currentAngle,holdTime);
//
//        //move into crater
//        encoderDrive(DRIVE_SPEED,99,99,1.5);
//        gyroHold(DRIVE_SPEED,currentAngle,holdTime);
//

        telemetry.addData("Path", "Complete");
        telemetry.update();

    }

    public void unlatch() throws InterruptedException{
        //  robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.CONFETTI);
        int newLatchTarget;
        int rotations = 36;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLatchTarget = robot.latch.getCurrentPosition() - (int)(rotations * COUNTS_PER_MOTOR_REV);
            robot.latch.setTargetPosition(newLatchTarget);

            // Turn On RUN_TO_POSITION
            robot.latch.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.latch.setPower(-1);

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < 15) &&
                    (robot.latch.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d", robot.latch.getTargetPosition());
                telemetry.addData("Path2",  "Running at %7d",
                        robot.latch.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.latch.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move

            encoderStrafeLeft(0.2,1,1);
            gyroHold(0.2,currentAngle, holdTime);
            encoderStrafeLeft(0.2,1,1);
            gyroHold(0.2,currentAngle, holdTime);

//            gyroDrive(DRIVE_SPEED,5,currentAngle);
//            gyroHold(DRIVE_SPEED,currentAngle,0.25);
        }
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller

            newFrontLeftTarget = robot.frontLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newFrontRightTarget = robot.frontRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newBackLeftTarget = robot.backLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newBackRightTarget = robot.backRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

            robot.frontRight.setTargetPosition(newFrontRightTarget);
            robot.frontLeft.setTargetPosition(newFrontLeftTarget);
            robot.backRight.setTargetPosition(newBackRightTarget);
            robot.backLeft.setTargetPosition(newBackLeftTarget);


            // Turn On RUN_TO_POSITION
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            // reset the timeout time and start motion.
            runtime.reset();
            robot.frontRight.setPower(Math.abs(speed));
            robot.frontLeft.setPower(Math.abs(speed));
            robot.backRight.setPower(Math.abs(speed));
            robot.backLeft.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.frontLeft.isBusy() && robot.frontRight.isBusy() && robot.backLeft.isBusy() && robot.backRight.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newFrontLeftTarget,  newFrontRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        robot.frontLeft.getCurrentPosition(),
                        robot.frontRight.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backRight.setPower(0);
            robot.backLeft.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //  sleep(250);   // optional pause after each move
        }
    }
    public void leftMineral() throws InterruptedException{
        // robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET);

        //face mineral
        currentAngle = 45;
        gyroTurn(TURN_SPEED,currentAngle);
        gyroHold(TURN_SPEED,currentAngle,0.5);

        //knock off mineral
        gyroDrive(DRIVE_SPEED,33, currentAngle);

    }

    public void centerMineral() throws InterruptedException{

        // robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.GRAY);

        // knock off mineral
        gyroDrive(DRIVE_SPEED,33,currentAngle);

        removeMarker();

    }

    public void rightMineral() throws InterruptedException{
        //  robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED_ORANGE);

        // face mineral
        currentAngle = -45;
        gyroTurn(TURN_SPEED,currentAngle);
        gyroHold(TURN_SPEED,currentAngle,0.5);


        // knock off mineral
        gyroDrive(DRIVE_SPEED,33,currentAngle);

        // face depot
        currentAngle = 0;
        gyroTurn(TURN_SPEED,currentAngle);
        gyroHold(TURN_SPEED,currentAngle,0.5);

        removeMarker();


    }

    public void removeMarker() throws InterruptedException{
        //  robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);

//        for (int i = 0; i < 2; i++) {
//            while (opModeIsActive() && robot.magneticLimitSwitch2.getState()) // move until false
//                robot.marker.setPower(1);
//            while (opModeIsActive() && !robot.magneticLimitSwitch2.getState()) // move until true
//                robot.marker.setPower(1);
//        }
//        robot.marker.setPower(0);

        sleep(1000);
        robot.marker.setPower(-1);
        runtime.reset();
        while(opModeIsActive() && runtime.seconds() < 2) { }
        robot.marker.setPower(0);

        sleep(1000);
        robot.marker.setPower(-1);

//        for (int i = 0; i < 2; i++) {
//            while (opModeIsActive() && robot.magneticLimitSwitch2.getState()) // move until false
//                robot.marker.setPower(-1);
//            while (opModeIsActive() && !robot.magneticLimitSwitch2.getState()) // move until true
//                robot.marker.setPower(-1);
//        }
//        robot.marker.setPower(0);

//        for (int i = 0; i < 2; i++) {
//            sleep(1000);
//            robot.dump.setPower(-1);
//            runtime.reset();
//            while(opModeIsActive() && runtime.seconds() < 2) { }
//            robot.dump.setPower(0);
//        }
        sleep(1000);

        //   robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);

        //sleep(1000);

//        robot.dump.setPower(1);
//        runtime.reset();
//        while(opModeIsActive() && runtime.seconds() < 3) { }
//        robot.dump.setPower(0);

//        for (int i = 0; i < 2; i++) {
//            while (opModeIsActive() && robot.magneticLimitSwitch2.getState()) // move until false
//                robot.marker.setPower(-1);
//            while (opModeIsActive() && !robot.magneticLimitSwitch2.getState()) // move until true
//                robot.marker.setPower(-1);
//        }
//        robot.marker.setPower(0);

    }


    public void driveTime(double seconds, double power) throws InterruptedException{
        robot.frontRight.setPower(power+0.12);
        robot.frontLeft.setPower(power);
        robot.backLeft.setPower(power);
        robot.backRight.setPower(power +0.12);
        runtime.reset();
        while(opModeIsActive() && runtime.seconds() < seconds){}
        robot.frontRight.setPower(0);
        robot.frontLeft.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
    }

    public void encoderStrafeLeft(double speed, double revolutions, double timeoutS) throws InterruptedException{

        int newFrontLeftTarget = robot.frontLeft.getCurrentPosition() - (int)(revolutions * COUNTS_PER_MOTOR_REV);
        int newBackLeftTarget = robot.backLeft.getCurrentPosition() + (int)(revolutions * COUNTS_PER_MOTOR_REV);

        robot.frontLeft.setTargetPosition(newFrontLeftTarget);
        robot.frontRight.setTargetPosition(newBackLeftTarget);
        robot.backLeft.setTargetPosition(newBackLeftTarget);
        robot.backRight.setTargetPosition(newFrontLeftTarget);

        robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.frontLeft.setPower(-speed);
        robot.frontRight.setPower(speed);
        robot.backLeft.setPower(speed);
        robot.backRight.setPower(-speed);

        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < timeoutS) && robot.frontLeft.isBusy() && robot.frontRight.isBusy()
                && robot.backLeft.isBusy() && robot.backRight.isBusy()) {
            // Display it for the driver.
            telemetry.addData("Path1",  "Running to %7d", newFrontLeftTarget);
            telemetry.addData("Path2",  "Running at %7d", robot.frontLeft.getCurrentPosition());
            telemetry.update();
        }

        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);

        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }



    /**
     * Method to drive on a fixed compass bearing (angle), based on encoder counts.
     * Move will stop if either of these conditions occur:
     * 1) Move gets to the desired position
     * 2) Driver stops the opmode running.
     *
     * @param speed    Target speed for forward motion.  Should allow for _/- variance for adjusting heading
     * @param distance Distance (in inches) to move from current position.  Negative distance means move backwards.
     * @param angle    Absolute Angle (in Degrees) relative to last gyro reset.
     *                 0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                 If a relative angle is required, add/subtract from current heading.
     */
    public void gyroDrive(double speed,
                          double distance,
                          double angle) {

        int newLeftTarget;
        int newRightTarget;
        int moveCounts;
        double max;
        double error;
        double steer;
        double leftSpeed;
        double rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int) (distance * COUNTS_PER_INCH);
            newLeftTarget = robot.frontLeft.getCurrentPosition() + moveCounts;
            newRightTarget = robot.frontRight.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            robot.frontLeft.setTargetPosition(newLeftTarget);
            robot.frontRight.setTargetPosition(newRightTarget);
            robot.backLeft.setTargetPosition(newLeftTarget);
            robot.backRight.setTargetPosition(newRightTarget);


            robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            robot.frontLeft.setPower(speed);
            robot.frontRight.setPower(speed);
            robot.backLeft.setPower(speed);
            robot.backRight.setPower(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (robot.frontLeft.isBusy() && robot.frontRight.isBusy() && robot.backLeft.isBusy() && robot.backRight.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0) {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                robot.frontLeft.setPower(leftSpeed);
                robot.frontRight.setPower(rightSpeed);
                robot.backLeft.setPower(leftSpeed);
                robot.backRight.setPower(rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St", "%5.1f/%5.1f", error, steer);
                telemetry.addData("Target", "%7d:%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Actual", "%7d:%7d", robot.frontLeft.getCurrentPosition(),
                        robot.frontRight.getCurrentPosition());
                telemetry.addData("Speed", "%5.2f:%5.2f", leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            robot.frontLeft.setPower(0);
            robot.frontRight.setPower(0);
            robot.backLeft.setPower(0);
            robot.backRight.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        }
    }

    /**
     * Method to spin on central axis to point in a new direction.
     * Move will stop if either of these conditions occur:
     * 1) Move gets to the heading (angle)
     * 2) Driver stops the opmode running.
     *
     * @param speed Desired speed of turn.
     * @param angle Absolute Angle (in Degrees) relative to last gyro reset.
     *              0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *              If a relative angle is required, add/subtract from current heading.
     */
    public void gyroTurn(double speed, double angle) {

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Update telemetry & Allow time for other processes to run.
            telemetry.update();
        }
    }

    /**
     * Method to obtain & hold a heading for a finite amount of time
     * Move will stop once the requested time has elapsed
     *
     * @param speed    Desired speed of turn.
     * @param angle    Absolute Angle (in Degrees) relative to last gyro reset.
     *                 0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                 If a relative angle is required, add/subtract from current heading.
     * @param holdTime Length of time (in seconds) to hold the specified heading.
     */
    public void gyroHold(double speed, double angle, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();

        // keep looping while we have time remaining.
        holdTimer.reset();
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Update telemetry & Allow time for other processes to run.
            onHeading(speed, angle, P_TURN_COEFF);
            telemetry.update();
        }

        // Stop all motion;
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);
    }

    /**
     * Perform one cycle of closed loop heading control.
     *
     * @param speed  Desired speed of turn.
     * @param angle  Absolute Angle (in Degrees) relative to last gyro reset.
     *               0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *               If a relative angle is required, add/subtract from current heading.
     * @param PCoeff Proportional Gain coefficient
     * @return
     */
    boolean onHeading(double speed, double angle, double PCoeff) {
        double error;
        double steer;
        boolean onTarget = false;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        } else {
            steer = getSteer(error, PCoeff);
            rightSpeed = speed * steer;
            leftSpeed = -rightSpeed;
        }

        // Send desired speeds to motors.
        robot.frontLeft.setPower(leftSpeed);
        robot.frontRight.setPower(rightSpeed);
        robot.backLeft.setPower(leftSpeed);
        robot.backRight.setPower(rightSpeed);

        // Display it for the driver.
        telemetry.addData("Target", "%5.2f", angle);
        telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);

        return onTarget;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     *
     * @param targetAngle Desired angle (relative to global reference established at last Gyro Reset).
     * @return error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     * +ve error means the robot should turn LEFT (CCW) to reduce error.
     */
    public double getError(double targetAngle) {

        double robotError;

        AngularVelocity rates = robot.gyro1.getAngularVelocity(AngleUnit.DEGREES);

        if (!(formatRate(rates.xRotationRate).equals("0.000") && formatRate(rates.yRotationRate).equals("0.000") && formatRate(rates.zRotationRate).equals("0.000"))){
            robotError = targetAngle - robot.modernRoboticsI2cGyro1.getIntegratedZValue();
            telemetry.addData(">","first gyro");
            telemetry.update();
        }
        else{
            robotError = targetAngle - robot.modernRoboticsI2cGyro2.getIntegratedZValue();
            telemetry.addData(">","backup");
            telemetry.update();
        }
        while (robotError > 180) robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     *
     * @param error  Error angle in robot relative degrees
     * @param PCoeff Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

    String formatRate(float rate) {
        return String.format("%.3f", rate);
    }
}
