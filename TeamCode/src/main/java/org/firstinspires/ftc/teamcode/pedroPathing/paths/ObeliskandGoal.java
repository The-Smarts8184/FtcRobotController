package org.firstinspires.ftc.teamcode.pedroPathing.paths;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Constants.Alliance;
import org.firstinspires.ftc.teamcode.pedroPathing.subsystems.IntakeOuttake;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Command-Based AprilTag Autonomous")
public class ObeliskandGoal extends OpMode {

    private enum AutoState {
        NAV_TO_W2,
        FIND_SIGNAL_PATTERN,
        NAV_TO_BALL_PATTERN,
        NAV_TO_GOAL,
        OUTTAKE,
        DONE
    }

    private AutoState currentState = AutoState.NAV_TO_W2;
    private Alliance alliance = Alliance.BLUE;

    private DcMotor leftFront, rightFront, leftRear, rightRear;
    private IMU imu;

    private IntakeOuttake intakeOuttake;
    private NavtoBallpattern ballPattern;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    private ElapsedTime stateTimer = new ElapsedTime();
    private String detectedPattern = "";

    private static final double COUNTS_PER_MOTOR_REV = 537.7;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_DIAMETER_INCHES = 3.78;
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    private static final double DEGREES_PER_ENCODER_TICK = 360.0 / COUNTS_PER_MOTOR_REV;

    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        rightRear = hardwareMap.get(DcMotor.class, "rightRear");
        imu = hardwareMap.get(IMU.class, "imu");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftRear.setDirection(DcMotor.Direction.REVERSE);

        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT)));

        intakeOuttake = new IntakeOuttake(hardwareMap);
        ballPattern = new NavtoBallpattern(this);

        initAprilTag();
        telemetry.addLine("Initialization complete.");
    }

    @Override
    public void loop() {
        switch (currentState) {
            case NAV_TO_W2:
                if (navigateToW2ByTags()) {
                    stopRobot();
                    newState(AutoState.FIND_SIGNAL_PATTERN);
                }
                break;

            case FIND_SIGNAL_PATTERN:
                setDrivePower(0.3, -0.3, 0.3, -0.3);
                detectSignalPattern();
                if (!detectedPattern.isEmpty() || stateTimer.seconds() > 5.0) {
                    stopRobot();
                    startBallPatternNavigation();
                    newState(AutoState.NAV_TO_BALL_PATTERN);
                }
                break;

            case NAV_TO_BALL_PATTERN:
                ballPattern.loop();
                if(ballPattern.isDone()){
                    newState(AutoState.NAV_TO_GOAL);
                }
                break;

            case NAV_TO_GOAL:
                if (navigateToGoalByTag()) {
                    stopRobot();
                    newState(AutoState.OUTTAKE);
                }
                break;

            case OUTTAKE:
                intakeOuttake.startOuttake();
                if (stateTimer.seconds() > 1.2) {
                    intakeOuttake.stopOuttake();
                    newState(AutoState.DONE);
                }
                break;

            case DONE:
                stopRobot();
                break;
        }
        telemetry.update();
    }

    private void newState(AutoState newState) {
        stateTimer.reset();
        resetAndPrepMotors();
        currentState = newState;
    }
    
    public IntakeOuttake getIntakeOuttake(){
        return intakeOuttake;
    }

    private void startBallPatternNavigation() {
        switch (detectedPattern) {
            case "GPP":
                ballPattern.startGPPNavigation(alliance);
                break;
            case "PGP":
                ballPattern.startPGPNavigation(alliance);
                break;
            case "PPG":
                ballPattern.startPPGNavigation(alliance);
                break;
            default: // Default to DONE if no pattern is detected
                newState(AutoState.DONE);
                break;
        }
    }

    public void resetAndPrepMotors(){
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }

    private boolean navigateToW2ByTags() {
        double targetDistanceInches = 9.22 * 12.0;
        double distanceTolerance = 2.0;
        List<AprilTagDetection> detections = aprilTag.getDetections();
        AprilTagDetection tag20 = null, tag24 = null;
        for (AprilTagDetection detection : detections) {
            if (detection.id == 20) tag20 = detection;
            if (detection.id == 24) tag24 = detection;
        }

        if (tag20 != null && tag24 != null) {
            double error20 = tag20.ftcPose.range - targetDistanceInches;
            double error24 = tag24.ftcPose.range - targetDistanceInches;

            if (Math.abs(error20) < distanceTolerance && Math.abs(error24) < distanceTolerance) {
                return true;
            }

            double driveError = (error20 + error24) / 2.0;
            double turnError = (tag20.ftcPose.range - tag24.ftcPose.range) / 2.0;
            double strafeError = (tag20.ftcPose.x + tag24.ftcPose.x) / 2.0;

            setMecanumPower(Range.clip(driveError * 0.02, -0.5, 0.5),
                    Range.clip(strafeError * 0.015, -0.4, 0.4),
                    Range.clip(turnError * 0.04, -0.3, 0.3));
        } else {
            setDrivePower(0.3, -0.3, 0.3, -0.3);
        }
        return false;
    }

    private boolean navigateToGoalByTag() {
        int targetTagId = (alliance == Alliance.BLUE) ? 20 : 24;
        double targetDistanceInches = 2.8 * 12.0; // 33.6 inches

        double distanceTolerance = 1.0; // inches
        double headingTolerance = 2.0;  // degrees
        double strafeTolerance = 1.0;   // inches

        AprilTagDetection goalTag = null;
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.id == targetTagId) {
                goalTag = detection;
                break;
            }
        }

        if (goalTag != null) {
            double distanceError = goalTag.ftcPose.range - targetDistanceInches;
            double headingError = goalTag.ftcPose.yaw;
            double strafeError = goalTag.ftcPose.x;

            if (Math.abs(distanceError) < distanceTolerance &&
                Math.abs(headingError) < headingTolerance &&
                Math.abs(strafeError) < strafeTolerance) {
                telemetry.addLine("At Goal Position.");
                return true;
            }

            double drivePower = Range.clip(distanceError * 0.02, -0.5, 0.5);
            double turnPower = Range.clip(headingError * 0.025, -0.3, 0.3);
            double strafePower = Range.clip(strafeError * 0.03, -0.4, 0.4);

            setMecanumPower(drivePower, strafePower, turnPower);

        } else {
            telemetry.addData("Searching for Goal Tag", "ID %d", targetTagId);
            setDrivePower(0.4, -0.4, 0.4, -0.4); // Rotate right
        }

        return false;
    }

    private void detectSignalPattern() {
        List<AprilTagDetection> detections = aprilTag.getDetections();
        for (AprilTagDetection detection : detections) {
            if (detection.metadata != null) {
                switch (detection.id) {
                    case 21: detectedPattern = "GPP"; break;
                    case 22: detectedPattern = "PGP"; break;
                    case 23: detectedPattern = "PPG"; break;
                }
                if (!detectedPattern.isEmpty()) break;
            }
        }
    }

    public void stopRobot() {
        setDrivePower(0, 0, 0, 0);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void setDrivePower(double lf, double rf, double lr, double rr) {
        leftFront.setPower(lf);
        rightFront.setPower(rf);
        leftRear.setPower(lr);
        rightRear.setPower(rr);
    }

    private void setMecanumPower(double drive, double strafe, double turn) {
        double lf = drive + strafe + turn;
        double rf = drive - strafe - turn;
        double lr = drive - strafe + turn;
        double rr = drive + strafe - turn;
        double max = Math.max(1.0, Math.max(Math.abs(lf), Math.max(Math.abs(rf), Math.max(Math.abs(lr), Math.abs(rr)))));
        setDrivePower(lf / max, rf / max, lr / max, rr / max);
    }

    private void setMotorMode(DcMotor.RunMode mode) {
        leftFront.setMode(mode);
        rightFront.setMode(mode);
        leftRear.setMode(mode);
        rightRear.setMode(mode);
    }

    public void driveToPosition(double inches, double speed, double strafeInches) {
        int driveTarget = (int) (inches * COUNTS_PER_INCH);
        int strafeTarget = (int) (strafeInches * COUNTS_PER_INCH);

        leftFront.setTargetPosition(leftFront.getCurrentPosition() + driveTarget + strafeTarget);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + driveTarget - strafeTarget);
        leftRear.setTargetPosition(leftRear.getCurrentPosition() + driveTarget - strafeTarget);
        rightRear.setTargetPosition(rightRear.getCurrentPosition() + driveTarget + strafeTarget);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        setDrivePower(speed, speed, speed, speed);
    }
    
    public void rotate(double degrees, double power) {
        double targetAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) + degrees;
        double error = degrees;
        while(Math.abs(error) > 2) {
            error = targetAngle - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double turnPower = Range.clip(error * 0.01, -power, power);
            setMecanumPower(0,0,turnPower);
        }
        stopRobot();
    }

    public boolean isRobotBusy(){
        return leftFront.isBusy() || rightFront.isBusy() || leftRear.isBusy() || rightRear.isBusy();
    }
}
