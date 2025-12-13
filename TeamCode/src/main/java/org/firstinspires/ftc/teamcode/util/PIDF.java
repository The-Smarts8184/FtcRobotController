package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.util.RobotConstants;

@Config
@TeleOp(name = "PIDF")
public class PIDF extends OpMode {

    public static PIDFController controller;
    public static double p =0.03, i = 0.00005, d = 0.00001;
    public static double f = 0;

    public static int target = 0;

    public final double ticks_in_degree = 384.5/360;

    DcMotorEx frontMotor, backMotor;


    @Override
    public void init() {
        controller = new PIDFController(p, i,d, f);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        frontMotor = hardwareMap.get(DcMotorEx.class,RobotConstants.Outtake.outtakeFront);
        backMotor = hardwareMap.get(DcMotorEx.class, RobotConstants.Outtake.outtakeRear);

        //backMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        controller.setPIDF(p,i,d,f);
        int pos = backMotor.getCurrentPosition();
        int backMotorPos = pos;
        int frontMotorPos = frontMotor.getCurrentPosition();

        double power = controller.calculate(pos, target);

        frontMotor.setPower(power);
        backMotor.setPower(power);

        telemetry.addData("Front Pos", frontMotorPos);
        telemetry.addData("Back Pos", backMotorPos);
        telemetry.addData("target", target);


    }
}
