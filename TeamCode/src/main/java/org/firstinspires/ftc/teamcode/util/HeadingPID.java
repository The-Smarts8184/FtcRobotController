package org.firstinspires.ftc.teamcode.util;

import com.arcrobotics.ftclib.controller.PIDController;

public class HeadingPID {
    private PIDController pid;

    public HeadingPID(double p, double i, double d) {
        pid = new PIDController(p, i, d);
    }

    public PIDController getPid() {
        return pid;
    }

    public double getSetPoint() {
        return pid.getSetPoint();
    }

    public void setSetPoint(double setPointRadians) {
        setPointRadians = angleWrap(setPointRadians);
        pid.setSetPoint(setPointRadians);
    }

    public boolean atSetPoint() {
        return pid.atSetPoint();
    }

    public double calculate(double currentRadians) {
        // The ftclib PIDController does not do continuous input wrapping
        // so we must do it here.
        return pid.calculate(currentRadians);
    }

    public double calculate(double currentRadians, double setPointRadians) {
        currentRadians = angleWrap(currentRadians);
        setPointRadians = angleWrap(setPointRadians);
        return pid.calculate(currentRadians, setPointRadians);
    }

    public void reset() {
        pid.reset();
    }

    public double angleWrap(double radians) {
        while (radians > Math.PI) {
            radians -= 2 * Math.PI;
        }
        while (radians < -Math.PI) {
            radians += 2 * Math.PI;
        }
        return radians;
    }
}
