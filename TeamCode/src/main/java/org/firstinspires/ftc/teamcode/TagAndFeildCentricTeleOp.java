package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.util.HeadingPID;

@TeleOp(name = "Tag and Field Centric TeleOp")
public class TagAndFeildCentricTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        // Reverse the right side motors. This may be wrong for your setup.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);

        // Initialize Limelight
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(8); // Use your AprilTag pipeline

        // PID controller for turning. Tune these P, I, and D values for your robot.
        HeadingPID turnController = new HeadingPID(0.03, 0.0, 0.001);
        turnController.setGoal(0); // We want to turn until the target is centered (tx = 0)

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;

            // --- AprilTag Lock-on Logic ---
            double rx;
            double manualRx = gamepad1.right_stick_x;

            // Check if the user is trying to rotate manually. Use a deadzone to avoid drift.
            if (Math.abs(manualRx) > 0.1) {
                // MANUAL CONTROL
                // User is turning. Right stick X is positive to the right.
                // For this robot's kinematics, a positive 'rx' causes a clockwise (right) turn.
                // So, we can directly use the joystick value for intuitive control.
                rx = manualRx;
                turnController.reset(); // Reset PID controller when using manual override
            } else {
                // AUTOMATIC CONTROL
                // User is not turning, so try to lock on to an AprilTag.
                LLResult llResult = limelight.getLatestResult();

                // Check for a valid result with at least one fiducial tag
                if (llResult.isValid() && !llResult.getFiducialResults().isEmpty()) {
                    double tx = llResult.getTx();

                    // Use the PID controller to calculate the rotational power.
                    // If the target is on the right (tx > 0), we need to turn right (rx > 0).
                    // The HeadingPID class expects radians, so we convert tx from degrees.
                    // We negate the output because a positive tx needs a positive rx, and the controller
                    // output is proportional to (goal - measurement) = (0 - tx) = -tx.
                    rx = -turnController.calculate(Math.toRadians(tx));
                } else {
                    // No tag is seen, and user is not turning, so don't rotate.
                    rx = 0;
                    turnController.reset(); // Reset PID if no tag is seen
                }
            }
            // --- End AprilTag Lock-on ---

            // This button choice was arbitrary. Any button would work.
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
        }
    }
}
