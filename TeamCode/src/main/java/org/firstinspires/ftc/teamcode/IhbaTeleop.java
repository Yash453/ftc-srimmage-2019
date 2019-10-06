///* Copyright (c) 2017 FIRST. All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without modification,
// * are permitted (subject to the limitations in the disclaimer below) provided that
// * the following conditions are met:
// *
// * Redistributions of source code must retain the above copyright notice, this list
// * of conditions and the following disclaimer.
// *
// * Redistributions in binary form must reproduce the above copyright notice, this
// * list of conditions and the following disclaimer in the documentation and/or
// * other materials provided with the distribution.
// *
// * Neither the name of FIRST nor the names of its contributors may be used to endorse or
// * promote products derived from this software without specific prior written permission.
// *
// * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
// * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
// * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.Gamepad;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//
///**
// * Created by Neha Deshpande, FTC 12116 on 9/1/2018.
// */
//
//@TeleOp(name="Ihba Main", group="Neha")
//// @Disabled
//public class IhbaTeleop extends LinearOpMode {
//
//    /* Declare OpMode members. */
//    HardwareIhba robot = new HardwareIhba();   // Use a Redbot's hardware
//    private ElapsedTime runtime = new ElapsedTime();
//    double target = 0;
//    private int chosen = 1; // default is double controller
//
//    @Override
//    public void runOpMode() {
//
//        /* Initialize the hardware variables.
//         * The init() method of the hardware class does all the work here
//         */
//        robot.init(hardwareMap);
//        //robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_LAVA_PALETTE);
//        robot.samplingDetector.disable();
//
//
//        // Wait for the game to start (driver presses PLAY)
//        Gamepad[] controller = new Gamepad[2]; // this is cool, it should go in the engineering notebook -- efficiency of using arrays as opposed to two if/else statements per gamepad
//        controller[0] = gamepad1;
//        controller[1] = gamepad2;
//
//        telemetry.addData("Say", "Press A for single controller\nPress B for double controller");
//        telemetry.update();
//
//        while (!isStopRequested() && !isStarted())  {   // default is single controller
//            if (gamepad1.a || gamepad2.a) {
//                telemetry.addData("Say", "you chose single controller!");
//                telemetry.update();
//                chosen = 0; // controller at index 0 is gamepad1
//            }
//            else if (gamepad1.b || gamepad2.b) {
//                telemetry.addData("Say", "you chose double controller!");
//                telemetry.update();
//                chosen = 1; // controller at index 1 is gamepad2
//            }
//        }
//
//        // Send telemetry message to signify robot waiting;
//        telemetry.addData("Say", "\"I'm lit, get it?\" - Ihba");
//        telemetry.update();
//        // Hello 2018-19 Redbots!\nHow many robots does it take to screw " + "in a lightbulb?\nThree - one to hold the lightbulb and two to turn the ladder! 7/1/18
//        // Can we remove the plexiglass panels again? 11/10/18
//        // Guys we won our first competition! 11/17/18
//        // "I'm lit, get it?" - Ihba 11/28/18
//        // Which idiot wrote this code? I have no idea what I'm doing - Ihba
//
//        runtime.reset();
//        // run until the end of the match (driver presses STOP)
//        while (opModeIsActive()) {
//
//            // Convert joysticks to desired motion
//            Mecanum.Motion motion = Mecanum.joystickToMotion(
//                    gamepad1.left_stick_x, gamepad1.left_stick_y,
//                    gamepad1.right_stick_x, gamepad1.right_stick_y);
//            // Convert desired motion to wheel powers, with power clamping
//            Mecanum.Wheels wheels = Mecanum.motionToWheels(motion);
//            robot.frontLeft.setPower(wheels.frontLeft);
//            robot.frontRight.setPower(wheels.frontRight);
//            robot.backLeft.setPower(wheels.backLeft);
//            robot.backRight.setPower(wheels.backRight);
//
//
////            if (gamepad1.left_stick_x == 0 && gamepad1.left_stick_y == 0 && gamepad1.right_stick_y == 0 &&
////                    gamepad1.right_stick_x == 0 &&
////                    !gamepad1.right_bumper && !gamepad1.left_bumper) {  // test these values
////                robot.frontLeft.setPower(0);
////                robot.frontRight.setPower(0);
////                robot.backLeft.setPower(0);
////                robot.backRight.setPower(0);
////            } else if (gamepad1.right_bumper){ // strafe right
////                robot.frontLeft.setPower(1);
////                robot.frontRight.setPower(-1);
////                robot.backLeft.setPower(-1);
////                robot.backRight.setPower(1);
////            } else if (gamepad1.left_bumper) { // strafe left
////                robot.frontLeft.setPower(-1);
////                robot.frontRight.setPower(1);
////                robot.backLeft.setPower(1);
////                robot.backRight.setPower(-1);
////            }
//
//            if (!controller[chosen].dpad_up && !controller[chosen].dpad_down)
//                robot.lift.setPower(0);
//            else if (controller[chosen].dpad_down)
//                robot.lift.setPower(-1);
//            else if (controller[chosen].dpad_up)
//                robot.lift.setPower(1);
//
//
//            if (controller[chosen].y){ // press one button to move the lift up completely
//                moveLatch(1);
//            }
//            if (controller[chosen].a){ // press one button to move the lift down completely
//                moveLatch(-1);
//            }
//
////            if (!controller[chosen].b && !controller[chosen].x)
////                robot.marker.setPower(0);
////            else if (controller[chosen].b)
////                robot.marker.setPower(1);
////            else if (controller[chosen].x)
////                robot.marker.setPower(-1);
//
//
//            if (!controller[chosen].dpad_left && !controller[chosen].dpad_right)    // marker extention
//                robot.marker.setPower(0);
//            else if(controller[chosen].dpad_left)
//                robot.marker.setPower(2);
//            else if(controller[chosen].dpad_right)
//                robot.marker.setPower(-2);
//
//
//            if (!controller[chosen].left_bumper && !controller[chosen].right_bumper)    // marker dumper
//                robot.dump.setPower(0);
//            else if (controller[chosen].left_bumper)
//                robot.dump.setPower(1);
//            else if (controller[chosen].right_bumper)
//                robot.dump.setPower(-1);
//
////            if (runtime.seconds() > 90)    // end game
////                robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED); // this should be a different red than the shot_red
//        }
//    }
//
//    public void moveLatch(double speed){
//        /*
//        if latch going up
//        move up until it is false
//        keep moving up until it is true
//         */
//        //robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_LAVA_PALETTE);
//        while (opModeIsActive() && robot.magneticLimitSwitch.getState() && !gamepad2.x) // move until false or until x is pressed
//            robot.lift.setPower(speed);
//
//        while (opModeIsActive() && !robot.magneticLimitSwitch.getState() && !gamepad2.x) // move until true or until x is pressed
//            robot.lift.setPower(speed);
//        //if (speed > 0)
//          //  robot.blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
//
//        robot.lift.setPower(0);
//    }
//}
