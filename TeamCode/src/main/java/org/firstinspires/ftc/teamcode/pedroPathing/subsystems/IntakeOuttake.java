package org.firstinspires.ftc.teamcode.pedroPathing.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeOuttake {

    private DcMotor intakeMotor;
    private DcMotor outtakeMotor;

    // Constants for motor power. Adjust as needed.
    private static final double INTAKE_POWER = 0.7;
    private static final double OUTTAKE_POWER = 0.9;

    public IntakeOuttake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
    }

    /**
     * Starts the intake motor.
     */
    public void startIntake() {
        intakeMotor.setPower(INTAKE_POWER);
    }

    /**
     * Stops the intake motor.
     */
    public void stopIntake() {
        intakeMotor.setPower(0);
    }

    /**
     * Starts the outtake motor.
     */
    public void startOuttake() {
        outtakeMotor.setPower(OUTTAKE_POWER);
    }

    /**
     * Stops the outtake motor.
     */
    public void stopOuttake() {
        outtakeMotor.setPower(0);
    }
}
