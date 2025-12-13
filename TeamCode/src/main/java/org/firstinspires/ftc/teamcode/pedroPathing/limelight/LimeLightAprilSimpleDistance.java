// Import statements (simplified for FTC context)
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.IMU;

// Limelight imports (FTC SDK + Limelight library)
import com.qualcomm.hardware.limelightvision.Limelight3A;

@TeleOp(name="AprilTagDistanceDemo", group="Linear Opmode")
public class AprilTagDistanceDemo extends LinearOpMode {

    private double distance;
    private IMU imu;

    @Override
    public void runOpMode() {
        // Initialize hardware
        imu = hardwareMap.get(IMU.class, "imu");

        // Initialize Limelight pipeline (example: pipeline 8 for AprilTag)
        LimelightHelpers.setPipelineIndex("limelight", 8);

        telemetry.addLine("Ready to start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Get latest Limelight result
            LimelightHelpers.LimelightResults results = LimelightHelpers.getLatestResults("limelight");

            if (results != null && results.targetingResults.valid) {
                double targetArea = results.targetingResults.ta;

                // Calculate distance using curve-fit equation
                distance = getDistanceFromTag(targetArea);

                // Display values
                telemetry.addData("Target Area", targetArea);
                telemetry.addData("Distance (mm)", distance);
                telemetry.update();
            }
        }
    }

    /**
     * Curve-fit equation derived from calibration data
     * Equation: distance = scale / targetArea
     * Scale constant comes from curve fitting tool (mycurvefit.com)
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
