package org.firstinspires.ftc.teamcode.pedroPathing.paths;

public class NavtoBallpattern {

    private ObeliskandGoal opMode;

    public NavtoBallpattern(ObeliskandGoal opMode) {
        this.opMode = opMode;
    }

    public void navigateToGPP() {
        opMode.telemetry.addLine("Navigating to GPP line...");
        opMode.telemetry.update();
        // TODO: Add pathing logic here to drive to the GPP line.
        // Example: drive forward 24 inches
        // opMode.setMecanumPower(0.5, 0, 0); 
        // opMode.sleep(1000); 
        // opMode.stopRobot();
        opMode.sleep(1000); // Placeholder for navigation
    }

    public void navigateToPGP() {
        opMode.telemetry.addLine("Navigating to PGP line...");
        opMode.telemetry.update();
        // TODO: Add pathing logic here to drive to the PGP line.
        // Example: drive forward 24 inches, then strafe left 12 inches
        opMode.sleep(1000); // Placeholder for navigation
    }

    public void navigateToPPG() {
        opMode.telemetry.addLine("Navigating to PPG line...");
        opMode.telemetry.update();
        // TODO: Add pathing logic here to drive to the PPG line.
        // Example: drive forward 24 inches, then strafe right 12 inches
        opMode.sleep(1000); // Placeholder for navigation
    }
}
