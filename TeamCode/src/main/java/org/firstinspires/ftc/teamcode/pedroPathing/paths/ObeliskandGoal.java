package org.firstinspires.ftc.teamcode.pedroPathing.paths;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.pedroPathing.subsystems.IntakeOuttake;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Command-Based AprilTag Autonomous")
public class ObeliskandGoal extends OpMode {

    // States for the autonomous routine
    private enum AutoState {
        NAV_TO_W2,
        FIND_SIGNAL_PATTERN,
        NAV_TO_BALLS,
        INTAKE,
        NAV_TO_GOAL,
        OUTTAKE,
        DONE
    }

    private AutoState currentState = AutoState.NAV_TO_W2;

    // Drive motors
    private DcMotor leftFront, rightFront, leftRear, rightRear;

    // Subsystems
    private IntakeOuttake intakeOuttake;
    private NavtoBallpattern ballPattern;

    // Vision
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    // State-related variables
    private ElapsedTime stateTimer = new ElapsedTime();
    private String detectedPattern = "";

    @Override
    public void init() {
        // Initialize hardware
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        rightRear = hardwareMap.get(DcMotor.class, "rightRear");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftRear.setDirection(DcMotor.Direction.REVERSE);

        // Initialize subsystems
        intakeOuttake = new IntakeOuttake(hardwareMap);
        ballPattern = new NavtoBallpattern(hardwareMap);

        // Initialize AprilTag
        initAprilTag();

        telemetry.addLine("Initialization complete. Ready for start.");
    }

    @Override
    public void loop() {
        switch (currentState) {
            case NAV_TO_W2:
                // TODO: Replace with your actual pathing logic.
                // This is a placeholder for navigating to W2.
                telemetry.addLine("Navigating to W2...");
                if (stateTimer.seconds() > 2.0) { // Placeholder for path completion
                    stopRobot();
                    newState(AutoState.FIND_SIGNAL_PATTERN);
                }
                break;

            case FIND_SIGNAL_PATTERN:
                telemetry.addLine("Searching for signal pattern...");
                setDrivePower(0.3, -0.3, 0.3, -0.3); // Rotate right
                detectSignalPattern();
                if (!detectedPattern.isEmpty() || stateTimer.seconds() > 5.0) {
                    stopRobot();
                    newState(AutoState.NAV_TO_BALLS);
                }
                break;

            case NAV_TO_BALLS:
                // This is a placeholder for navigating to the ball line.
                telemetry.addData("Navigating to ball pattern for", detectedPattern);
                // TODO: Replace with actual pathing and completion check.
                if (stateTimer.seconds() > 2.0) {
                    stopRobot();
                    newState(AutoState.INTAKE);
                }
                break;

            case INTAKE:
                telemetry.addLine("Intaking balls...");
                intakeOuttake.startIntake();
                if (stateTimer.seconds() > 1.2) { // Corresponds to INTAKE_TIME
                    intakeOuttake.stopIntake();
                    newState(AutoState.NAV_TO_GOAL);
                }
                break;

            case NAV_TO_GOAL:
                telemetry.addLine("Navigating to goal...");
                // TODO: Replace with actual navigation to tag 2.
                if (stateTimer.seconds() > 3.0) { // Placeholder for navigation
                     newState(AutoState.OUTTAKE);
                }
                break;

            case OUTTAKE:
                telemetry.addLine("Outtaking balls...");
                intakeOuttake.startOuttake();
                if (stateTimer.seconds() > 1.2) { // Corresponds to OUTTAKE_TIME
                    intakeOuttake.stopOuttake();
                    newState(AutoState.DONE);
                }
                break;

            case DONE:
                telemetry.addLine("Autonomous complete.");
                stopRobot();
                break;
        }
        telemetry.update();
    }

    // Helper method to transition to a new state
    private void newState(AutoState newState) {
        stateTimer.reset();
        currentState = newState;
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }

    private void detectSignalPattern() {
        List<AprilTagDetection> detections = aprilTag.getDetections();
        for (AprilTagDetection detection : detections) {
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
}
