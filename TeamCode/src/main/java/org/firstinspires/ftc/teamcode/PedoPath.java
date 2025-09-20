package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.robocol.Command;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay;
import com.rowanmcalpin.nextftc.pedro.FollowPath;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import org.firstinspires.ftc.teamcode.OpenCv.AprilTagDetectionPipeline;
import org.opencv.core.Point;

@Autonomous(name = "NextFTC Autonomous Program 2 Java")
public class PedoPath extends PedroOpMode {
    public AutonomousProgram() {

    }

    private final AprilTagDetectionPipeline.Pose startPose = new AprilTagDetectionPipeline.Pose(9.0, 60.0, Math.toRadians(0.0));
    private final AprilTagDetectionPipeline.Pose finishPose = new AprilTagDetectionPipeline.Pose(37.0, 50.0, Math.toRadians(180.0));

    public void buildPaths() {
        move = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(finishPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), finishPose.getHeading())
                .build();
    }

    public Command secondRoutine() {
        return new SequentialGroup(
                new ParallelGroup(
                        new FollowPath(move),
                        Lift.INSTANCE.toHigh()
                ),
                new ParallelGroup(
                        Claw.INSTANCE.open(),
                        Lift.INSTANCE.toMiddle()
                ),
                new Delay(1.0),
                Lift.INSTANCE.toLow()
        );
    }

    @Override
    public void onInit() {
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(startingPose);
        buildPaths();
    }

    @Override
    public void onStartButtonPressed() {
        secondRoutine().invoke();
    }
}