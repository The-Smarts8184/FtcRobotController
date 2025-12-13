package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Constants;

public class IntakeOuttake {

    private final DcMotorEx intakeMotor;
    private final DcMotorEx outtakeMotor;

    public IntakeOuttake(HardwareMap hardwareMap) {
        // TODO: Rename this to the actual intake motor name in the robot configuration
        intakeMotor = hardwareMap.get(DcMotorEx.class, Constants.INTAKE_MOTOR_NAME);
        // TODO: Rename this to the actual outtake motor name in the robot configuration
        outtakeMotor = hardwareMap.get(DcMotorEx.class, Constants.OUTTAKE_MOTOR_NAME);

        intakeMotor.setDirection(DcMotorEx.Direction.FORWARD);
        intakeMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        outtakeMotor.setDirection(DcMotorEx.Direction.FORWARD);
        outtakeMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    public void intake() {
        intakeMotor.setPower(1.0);
    }

    public void stopIntake() {
        intakeMotor.setPower(0);
    }

    public void launch() {
        // This is a rough calculation and may need to be tuned for your specific motor.
        double ticksPerRevolution = outtakeMotor.getMotorType().getTicksPerRev();
        double ticksPerSecond = (Constants.LAUNCH_RPM * ticksPerRevolution) / 60.0;
        outtakeMotor.setVelocity(ticksPerSecond);
    }

    public void stopLaunch() {
        outtakeMotor.setVelocity(0);
    }

    public void stopAll() {
        stopIntake();
        stopLaunch();
    }
}
