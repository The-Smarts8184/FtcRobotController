package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.robocol.Command;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.ftc.NextFTCOpMode;



@Autonomous(name = "NextFTC Autonomous Program Java")
public class AutonNi extends NextFTCOpMode {

    public AutonNi() {

    }

    public Command firstRoutine() {
        return new SequentialGroup(

        );
    }

    @Override
    public void onStartButtonPressed() {
        firstRoutine().invoke();
    }


}