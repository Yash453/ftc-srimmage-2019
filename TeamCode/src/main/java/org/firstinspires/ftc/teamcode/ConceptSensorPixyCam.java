//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
//
//
//public class ConceptSensorPixyCam extends LinearOpMode {
//    I2cDeviceSynch pixyCam;
//
//    double x, y, width, height, numObjects;
//
//    byte[] pixyData;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        pixyCam = hardwareMap.i2cDeviceSynch.get("Pixy");
//
//        waitForStart();
//
//        while(opModeIsActive()){
//            pixyCam.engage();
//            pixyData = pixyCam.read(0x51, 5);
//
//            x = pixyData[1];
//            y = pixyData[2];
//            width = pixyData[3];
//            height = pixyData[4];
//            numObjects = pixyData[0];
//
//
//            telemetry.addData("0 - numObjects", 0xff&pixyData[0]);
//            telemetry.addData("1 - x", 0xff&pixyData[1]);
//            telemetry.addData("2 - y", 0xff&pixyData[2]);
//            telemetry.addData("width - 3", 0xff&pixyData[3]);
//            telemetry.addData("height - 4", 0xff&pixyData[4]);
//            telemetry.addData("Length", pixyData.length);
//            telemetry.update();
//            sleep (500);
//        }
//
//    }
//}
