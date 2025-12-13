package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class teleOpVision {

    private Limelight3A limelight;

    // TODO: Tune these values for your robot
    private final double CAMERA_HEIGHT_INCHES = 6.0; // Height of the camera from the ground
    private final double CAMERA_PITCH_RADIANS = 0.0; // Pitch of the camera in radians

    private final double TARGET_DISTANCE_INCHES = 48.0; // 2 FTC game tiles

    // PID coefficients for alignment
    // TODO: Tune these PID coefficients
    private final double X_KP = 0.03;
    private final double DISTANCE_KP = 0.02;

    public teleOpVision(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(9); // TODO: Set the correct pipeline for AprilTags
    }

    public LLResult getLatestResult() {
        return limelight.getLatestResult();
    }

    public boolean hasTarget() {
        LLResult result = getLatestResult();
        return result != null && result.isValid();
    }

    public double getTargetX() {
        if (hasTarget()) {
            return getLatestResult().getTx();
        }
        return 0.0;
    }

    public double getTargetY() {
        if (hasTarget()) {
            return getLatestResult().getTy();
        }
        return 0.0;
    }

    /**
     * Estimates the distance to the AprilTag target.
     * @return Distance in inches.
     */
    public double getDistanceToTarget() {
        if (hasTarget()) {
            double targetY = getTargetY();
            // Using trigonometry to calculate distance
            // tan(cameraPitch + targetY) = (targetHeight - cameraHeight) / distance
            // Assuming target is at the same height as the camera for simplicity to start.
            // A more accurate approach would require knowing the AprilTag's height.
            // For now, this is a placeholder calculation.
            // A better formula for this would be:
            // distance = (TARGET_HEIGHT - CAMERA_HEIGHT_INCHES) / Math.tan(Math.toRadians(CAMERA_PITCH_DEGREES + targetY));
            // Since we don't have TARGET_HEIGHT, we will use a simplified formula for now, assuming target is on the floor.
            double distance = CAMERA_HEIGHT_INCHES / Math.tan(Math.toRadians(CAMERA_PITCH_RADIANS / (Math.PI/180) + getTargetY()));
            return distance;
        }
        return 0.0;
    }

    public double getDistanceError() {
        if (!hasTarget()) {
            return 0.0;
        }
        return getDistanceToTarget() - TARGET_DISTANCE_INCHES;
    }

    public double getStrafeCorrection() {
        if (!hasTarget()) {
            return 0.0;
        }
        return getTargetX() * X_KP;
    }

    public double getForwardCorrection() {
        if (!hasTarget()) {
            return 0.0;
        }
        return -getDistanceError() * DISTANCE_KP;
    }
}
