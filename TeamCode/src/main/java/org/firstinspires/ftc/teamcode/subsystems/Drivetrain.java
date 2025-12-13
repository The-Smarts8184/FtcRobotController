package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Drivetrain {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;
    public void init(HardwareMap hwMap){
        frontLeft = hwMap.get(DcMotor.class, "front_left_motor");
        frontRight = hwMap.get(DcMotor.class, "front_right_motor");
        backLeft = hwMap.get(DcMotor.class, "back_left_motor");
        backRight = hwMap.get(DcMotor.class, "back_right_motor");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );
        imu.initialize(new IMU.Parameters(RevOrientation));
    }
    public void drive(double forward, double strafe, double rotate){
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;

        maxPower = Math.max(Math.abs(frontLeftPower), maxPower);
        maxPower = Math.max(Math.abs(frontRightPower), maxPower);
        maxPower = Math.max(Math.abs(backLeftPower), maxPower);
        maxPower = Math.max(Math.abs(backRightPower), maxPower);

        frontLeft.setPower(maxSpeed * frontLeftPower / maxPower);
        backLeft.setPower(maxSpeed * backLeftPower / maxPower);
        frontRight.setPower(maxSpeed * frontRightPower / maxPower);
        backRight.setPower(maxSpeed * backRightPower / maxPower);
    }

    public void driveFeildRelative(double forward, double strafe, double rotate){
        double theta = Math.atan2(strafe, forward);
        double r = Math.hypot(strafe, forward);

        theta  = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate);
    }
}
