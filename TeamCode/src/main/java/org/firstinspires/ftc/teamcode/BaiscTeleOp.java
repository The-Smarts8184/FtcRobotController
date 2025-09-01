package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BaiscTeleOp {

    private DcMotor frontLeft = null;
    private DcMotor frontRIght = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    @Override
    public void init() {
        //TODO fix the DcMotor.class
        frontLeft = hardwareMap.dcMotor.get(DcMotor.class, "frontLeft");
        frontRIght = hardwareMap.dcMotor.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.dcMotor.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.dcMotor.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
    }
    @Override
    public void loop() {
        backLeft.setPower(gamepad1.left_stick_y);
        backRight.setPower(gamepad1.right_stick_y);
        frontLeft.setPower(gamepad1.left_stick_y);
        frontRIght.setPower(gamepad1.right_stick_y);
    }
}
