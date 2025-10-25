package org.firstinspires.ftc.teamcode.pedroPathing.paths;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AprilTag Pattern Autonomous")
public class PatternBased extends LinearOpMode {

    // Drive motors
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftRear = null;
    private DcMotor rightRear = null;

    // Intake and outtake motors
    private DcMotor intakeMotor = null;
    private DcMotor outtakeMotor = null;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private BallPattern ballPattern; // Instance of BallPattern

    private static final boolean USE_WEBCAM = true;

    // Variable to store the detected pattern
    private String detectedPattern = ""; // GPP, PGP, PPG

    @Override
    public void runOpMode() {
        // Initialize hardware
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        rightRear = hardwareMap.get(DcMotor.class, "rightRear");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftRear.setDirection(DcMotor.Direction.REVERSE);

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");

        initAprilTag();
        ballPattern = new BallPattern(this); // Initialize BallPattern

        telemetry.addLine("Initialization complete. Waiting for start.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Step 1: Rotate to find the signal AprilTag (21, 22, or 23)
            findSignalPattern();

            if (!detectedPattern.isEmpty()) {
                telemetry.addData("Pattern Detected", detectedPattern);
                telemetry.update();

                // Step 2: Navigate to the ball pattern line
                runPatternNavigation();

                // TODO: Add logic here to interact with the balls (intake/outtake)

                // Step 3: Navigate to AprilTag ID 2
                navigateToTag2();
            } else {
                telemetry.addLine("No signal pattern detected.");
                telemetry.update();
            }
        }

        visionPortal.close();
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            // Use phone camera
        }
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
    }

    private void findSignalPattern() {
        // Start rotating to find the signal tag
        setDrivePower(0.3, -0.3, 0.3, -0.3); // Rotate right

        long startTime = System.currentTimeMillis();
        // Rotate for a maximum of 5 seconds to find the tag
        while (opModeIsActive() && detectedPattern.isEmpty() && (System.currentTimeMillis() - startTime < 5000)) {
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                switch (detection.id) {
                    case 21:
                        detectedPattern = "GPP";
                        break;
                    case 22:
                        detectedPattern = "PGP";
                        break;
                    case 23:
                        detectedPattern = "PPG";
                        break;
                }
                if (!detectedPattern.isEmpty()) break;
            }
            sleep(20);
        }
        stopRobot();
    }

    private void runPatternNavigation() {
        switch (detectedPattern) {
            case "GPP":
                ballPattern.navigateToGPP();
                break;
            case "PGP":
                ballPattern.navigateToPGP();
                break;
            case "PPG":
                ballPattern.navigateToPPG();
                break;
        }
    }

    private void navigateToTag2() {
        telemetry.addLine("Navigating to AprilTag ID 2");
        telemetry.update();

        AprilTagDetection tag2Detection = null;

        // Rotate to find tag 2
        setDrivePower(0.4, -0.4, 0.4, -0.4); // Rotate right
        long searchStartTime = System.currentTimeMillis();
        while (opModeIsActive() && tag2Detection == null && (System.currentTimeMillis() - searchStartTime < 5000)) {
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (detection.id == 2) {
                    tag2Detection = detection;
                    break;
                }
            }
            sleep(20);
        }
        stopRobot();

        if (tag2Detection != null) {
            double targetDistance = 48.0; // inches
            double distanceError = tag2Detection.ftcPose.range - targetDistance;

            // Simple proportional control loop to approach the tag.
            // For better accuracy, a PID controller is recommended.
            double Kp_drive = 0.02;
            double Kp_strafe = 0.03;
            double Kp_turn = 0.025;
            double maxSpeed = 0.5;

            while (opModeIsActive() && Math.abs(distanceError) > 1.0) {
                 List<AprilTagDetection> currentDetections = aprilTag.getDetections();
                 boolean foundTag = false;
                 for(AprilTagDetection detection : currentDetections) {
                     if(detection.id == 2) {
                         tag2Detection = detection;
                         foundTag = true;
                         break;
                     }
                 }

                if(!foundTag) {
                    stopRobot();
                    break;
                }

                distanceError = tag2Detection.ftcPose.range - targetDistance;
                double yawError = tag2Detection.ftcPose.yaw;
                double strafeError = tag2Detection.ftcPose.x;

                double drivePower = Range.clip(distanceError * Kp_drive, -maxSpeed, maxSpeed);
                double turnPower = Range.clip(yawError * Kp_turn, -0.3, 0.3);
                double strafePower = Range.clip(strafeError * Kp_strafe, -0.4, 0.4);

                setMecanumPower(drivePower, strafePower, turnPower);

                telemetry.addData("Distance", tag2Detection.ftcPose.range);
                telemetry.update();
                sleep(20);
            }
            stopRobot();
            telemetry.addLine("Positioned at 48 inches from tag 2.");
            telemetry.update();
        } else {
            telemetry.addLine("AprilTag ID 2 not found.");
            telemetry.update();
        }
    }

    private void stopRobot() {
        setDrivePower(0, 0, 0, 0);
    }

    private void setDrivePower(double lf, double rf, double lr, double rr) {
        leftFront.setPower(lf);
        rightFront.setPower(rf);
        leftRear.setPower(lr);
        rightRear.setPower(rr);
    }

    private void setMecanumPower(double drive, double strafe, double turn) {
        double leftFrontPower = drive + strafe + turn;
        double rightFrontPower = drive - strafe - turn;
        double leftRearPower = drive - strafe + turn;
        double rightRearPower = drive + strafe - turn;

        // Normalize powers if they are greater than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftRearPower));
        max = Math.max(max, Math.abs(rightRearPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftRearPower /= max;
            rightRearPower /= max;
        }

        setDrivePower(leftFrontPower, rightFrontPower, leftRearPower, rightRearPower);
    }
}
