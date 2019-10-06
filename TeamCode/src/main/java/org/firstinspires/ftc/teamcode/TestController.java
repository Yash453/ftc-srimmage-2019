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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 * The code is structured as a LinearOpMode
 *
 * This particular OpMode executes a POV Game style Teleop for a PushBot
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Test Controller", group="Neha")
@Disabled
public class TestController extends LinearOpMode {

    final double    CLAW_SPEED      = 0.02 ;                   // sets rate to move servo

    /* Declare OpMode members. */
    HardwareIhba robot = new HardwareIhba();   // Use a Redbot's hardware
    private ElapsedTime runtime = new ElapsedTime();

    double target = 0;
    double SERVO_SPEED = .1;

    @Override
    public void runOpMode() {

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        //robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_LAVA_PALETTE);
        robot.alignDetector.disable();
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "\"Which idiot wrote the documentation for this code?\nI have no idea what I'm doing\"\n- Ihba");
        telemetry.update();
        // How many robots does it take in to screw in a lightbulb? 7/1/18
        // Can we remove the plexiglass panels again? 11/10/18
        // Guys we won our first competition! 11/17/18
        // "I'm lit, get it?" - Ihba 11/28/18
        // Which idiot wrote the documentation for this code? I have no idea what I'm doing - Ihba 12/27/18

        // Wait for the game to start (driver presses PLAY)
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Run wheels in POV mode (note: The joystick goes negative when pushed forwards, so negate it)

            // Convert joysticks to desired motion
            if (!gamepad1.x && !gamepad1.y && !gamepad1.a && !gamepad1.b){
                robot.frontLeft.setPower(0);
                robot.frontRight.setPower(0);
                robot.backLeft.setPower(0);
                robot.backRight.setPower(0);

            }
            if (gamepad1.x) //back left
                robot.frontLeft.setPower(1);
            if (gamepad1.y) //front right
                robot.frontRight.setPower(1);
            if (gamepad1.a) // front left
                robot.backLeft.setPower(1);
            if (gamepad1.b)//backright
                robot.backRight.setPower(1);
//            if (gamepad1.left_stick_x == 0 && gamepad1.left_stick_y == 0 && gamepad1.right_stick_y == 0 &&
//                    gamepad1.right_stick_x == 0 &&
//                    !gamepad1.right_bumper && !gamepad1.left_bumper) {  // test these values
//                robot.frontLeft.setPower(0);
//                robot.frontRight.setPower(0);
//                robot.backLeft.setPower(0);
//                robot.backRight.setPower(0);
//            } else if (gamepad1.right_bumper){ // strafe right
//                robot.frontLeft.setPower(1);
//                robot.frontRight.setPower(-1);
//                robot.backLeft.setPower(-1);
//                robot.backRight.setPower(1);
//            } else if (gamepad1.left_bumper) { // strafe left
//                robot.frontLeft.setPower(-1);
//                robot.frontRight.setPower(1);
//                robot.backLeft.setPower(1);
//                robot.backRight.setPower(-1);
//            }

//            if (!gamepad2.dpad_up && !gamepad2.dpad_down)
//                robot.lift.setPower(0);
//            else if (gamepad2.dpad_down)
//                robot.lift.setPower(-1);
//            else if (gamepad2.dpad_up)
//                robot.lift.setPower(1);


//            if (gamepad2.y){ // press one button to move the lift up completely
//                moveLatch(1);
//            }
//            if (gamepad2.a){ // press one button to move the lift down completely
//                moveLatch(-1);
//            }

//            if (!gamepad2.b && !gamepad2.x)
//                robot.marker.setPower(0);
//            else if (gamepad2.b)
//                robot.marker.setPower(1);
//            else if (gamepad2.x)
//                robot.marker.setPower(-1);
//
//            if (runtime.seconds() > 90)    // end game
//                robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED); // this should be a different red than the shot_red
        }
    }

//    public void moveLatch(double speed){
//        /*
//        if latch going up
//        move up until it is false
//        keep moving up until it is true
//         */
//        robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_LAVA_PALETTE);
//        while (opModeIsActive() && robot.magneticLimitSwitch.getState() && !gamepad2.x) // move until false or until x is pressed
//            robot.lift.setPower(speed);
//
//        while (opModeIsActive() && !robot.magneticLimitSwitch.getState() && !gamepad2.x) // move until true or until x is pressed
//            robot.lift.setPower(speed);
//        robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
//
//        robot.lift.setPower(0);
//    }
}
