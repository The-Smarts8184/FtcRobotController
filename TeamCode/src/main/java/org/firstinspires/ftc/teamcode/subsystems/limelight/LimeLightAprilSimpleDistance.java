package org.firstinspires.ftc.teamcode.subsystems.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="LimeLightAprilSimpleDistance", group="Linear Opmode")
public class LimeLightAprilSimpleDistance extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() {
        // Initialize hardware
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Initialize Limelight pipeline (example: pipeline 8 for AprilTag)
        limelight.pipelineSwitch(8);

        telemetry.addLine("Ready to start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Get latest Limelight result
            LLResult results = limelight.getLatestResult();

            if (results != null && results.isValid()) {
                double targetArea = results.getTa();
                double angleToTag = results.getTx();

                // Calculate distance using curve-fit equation
                double distance = getDistanceFromTag(targetArea);

                // Display values
                telemetry.addData("Target Area", targetArea);
                telemetry.addData("Distance (mm)", distance);
                telemetry.addData("Angle to Tag", angleToTag);
                telemetry.update();
            }
        }
    }

    /**
     * Curve-fit equation derived from calibration data
     * Equation: distance = scale / targetArea
     */
    public double getDistanceFromTag(double targetArea) {
        if (targetArea <= 0) return Double.POSITIVE_INFINITY; // invalid detection

        // Example calibration constants from video
        final double SCALE = 30665.95;   // y-value from curve fit
        final double EXPONENT = 1.99612; // ~2, inverse square law

        // Apply power law curve
        return Math.pow(SCALE / targetArea, 1.0 / EXPONENT);
    }
}
