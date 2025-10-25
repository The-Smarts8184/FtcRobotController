package org.firstinspires.ftc.teamcode.pedroPathing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeOuttake {

    private DcMotor intakeMotor;
    private DcMotor outtakeMotor;
    private LinearOpMode opMode; // For telemetry and sleep

    // Constants for motor power. Adjust as needed.
    private static final double INTAKE_POWER = 0.8;
    private static final double OUTTAKE_POWER = 0.6;
    private static final long INTAKE_TIME = 1200; // milliseconds
    private static final long OUTTAKE_TIME = 1200; // milliseconds


    public IntakeOuttake(LinearOpMode opMode) {
        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
    }

    /**
     * Runs the intake motors for a specified duration.
     */
    public void runIntake() {
        opMode.telemetry.addLine("Intaking...");
        opMode.telemetry.update();
        intakeMotor.setPower(INTAKE_POWER);
        opMode.sleep(INTAKE_TIME);
        intakeMotor.setPower(0);
    }

    /**
     * Runs the outtake motors for a specified duration.
     */
    public void runOuttake() {
        opMode.telemetry.addLine("Outtaking...");
        opMode.telemetry.update();
        outtakeMotor.setPower(OUTTAKE_POWER);
        opMode.sleep(OUTTAKE_TIME);
        outtakeMotor.setPower(0);
    }
}
