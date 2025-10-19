
package org.firstinspires.ftc.teamcode.pedroPathing.paths;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AprilTag Pattern Autonomous")
public class PatternBased extends LinearOpMode {

    private DcMotor intakeMotor = null;
    private DcMotor outtakeMotor = null;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    // TODO: Adjust these constants according to your robot's hardware configuration
    private static final boolean USE_WEBCAM = true;
    private static final double INTAKE_POWER = 0.5;
    private static final double OUTTAKE_POWER = 0.5;

    // Variable to store the detected pattern
    private int detectedPattern = 0; // 0 for no pattern, 1, 2, or 3 for detected patterns

    @Override
    public void runOpMode() {
        // Initialize hardware
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");

        // Initialize AprilTag
        initAprilTag();

        // Wait for the driver to press start
        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("Waiting for start...");
            detectAprilTag();
            telemetry.addData("Detected Pattern", detectedPattern);
            telemetry.update();
            sleep(20);
        }

        if (opModeIsActive()) {
            // Execute the pattern based on detection
            switch (detectedPattern) {
                case 1:
                    runPattern1();
                    break;
                case 2:
                    runPattern2();
                    break;
                case 3:
                    runPattern3();
                    break;
                default:
                    telemetry.addLine("No pattern detected or unknown pattern.");
                    telemetry.update();
                    // Optionally, run a default autonomous path
                    break;
            }
        }

        // Release resources
        visionPortal.close();
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            // For phone camera, specify the camera direction.
            // builder.setCamera(BuiltinCameraDirection.BACK);
        }
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
    }

    private void detectAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();

        if (currentDetections.size() > 0) {
            for (AprilTagDetection detection : currentDetections) {
                if (detection.id == 1 || detection.id == 2 || detection.id == 3) {
                    detectedPattern = detection.id;
                    break; // Found a pattern, no need to check others
                }
            }
        }
    }

    private void runPattern1() {
        telemetry.addLine("Running Pattern 1");
        telemetry.update();
        // TODO: Add your robot's movement, intake, and outtake logic for Pattern 1
        // Example:
        // intakeMotor.setPower(INTAKE_POWER);
        // sleep(1000); // run intake for 1 second
        // intakeMotor.setPower(0);
        // outtakeMotor.setPower(OUTTAKE_POWER);
        // sleep(1000); // run outtake for 1 second
        // outtakeMotor.setPower(0);
    }

    private void runPattern2() {
        telemetry.addLine("Running Pattern 2");
        telemetry.update();
        // TODO: Add your robot's movement, intake, and outtake logic for Pattern 2
    }

    private void runPattern3() {
        telemetry.addLine("Running Pattern 3");
        telemetry.update();
        // TODO: Add your robot's movement, intake, and outtake logic for Pattern 3
    }
}
