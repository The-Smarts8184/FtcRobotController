
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.TimerTask;

@Autonomous(name = "First Write", group = "Examples")
public class useless extends OpMode {

    private Follower follower;
    private Timer pathTimer, opmodeTimer,actionTimer;
    private double initX = RobotConstants.Auto.initX;
    private double initY = RobotConstants.Auto.initY;
    private final RobotHardware robot = RobotHardware.getInstance();
    TimerTask slidePreload = new TimerTask() {
        public void run() {
            robot.outtake.PIDLoop(RobotConstants.Outtake.slideSample);
        }
    };
