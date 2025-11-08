
package org.firstinspires.ftc.teamcode.pedroPathing.paths;

import org.firstinspires.ftc.teamcode.Constants.Alliance;

public class NavtoBallpattern {

    private ObeliskandGoal opMode;

    private enum NavState {
        IDLE,
        DRIVING,
        TURNING,
        STRAFING,
        DONE
    }

    private NavState currentState = NavState.IDLE;
    private Alliance alliance;
    private double driveInches;
    private double strafeInches;

    public NavtoBallpattern(ObeliskandGoal opMode) {
        this.opMode = opMode;
    }

    public void startGPPNavigation(Alliance alliance) {
        this.alliance = alliance;
        this.driveInches = 3 * 12;
        this.strafeInches = 5 * 12;
        opMode.getIntakeOuttake().startIntake();
        newState(NavState.DRIVING);
    }

    public void startPGPNavigation(Alliance alliance) {
        this.alliance = alliance;
        this.driveInches = 1 * 12;
        this.strafeInches = 5 * 12;
        opMode.getIntakeOuttake().startIntake();
        newState(NavState.DRIVING);
    }

    public void startPPGNavigation(Alliance alliance) {
        this.alliance = alliance;
        this.driveInches = -1 * 12;
        this.strafeInches = 5 * 12;
        opMode.getIntakeOuttake().startIntake();
        newState(NavState.DRIVING);
    }

    public boolean isDone() {
        return currentState == NavState.DONE;
    }

    public void loop() {
        if (isDone() || currentState == NavState.IDLE) {
            return;
        }

        switch (currentState) {
            case DRIVING:
                opMode.driveToPosition(driveInches, 0.5, 0);
                if (!opMode.isRobotBusy()) {
                    newState(NavState.TURNING);
                }
                break;

            case TURNING:
                double turnAngle = (alliance == Alliance.BLUE) ? -90.0 : 90.0;
                opMode.rotate(turnAngle, 0.4);
                if (!opMode.isRobotBusy()) {
                    newState(NavState.STRAFING);
                }
                break;

            case STRAFING:
                // Since we've turned 90 degrees, what was a strafe is now driving forward.
                opMode.driveToPosition(strafeInches, 0.5, 0);
                if (!opMode.isRobotBusy()) {
                    opMode.getIntakeOuttake().stopIntake();
                    newState(NavState.DONE);
                }
                break;
        }
    }

    private void newState(NavState newState) {
        opMode.resetAndPrepMotors();
        currentState = newState;
    }
}
