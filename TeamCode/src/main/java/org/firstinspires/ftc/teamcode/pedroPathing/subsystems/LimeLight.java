package org.firstinspires.ftc.teamcode.pedroPathing.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@Autonomous
public class LimeLight extends OpMode {

    private Limelight3A limelight3A;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(9); //make pipelines TODO MAKE THE PIPELINES

    }

    @Override
    public void start() {
        limelight3A.start();
    }


    @Override
    public void loop() {
        LLResult llresult = limelight3A.getLatestResult();
        if (llresult != null && llresult.isValid()) {
            telemetry.addData("Target X", llresult.getTx());
            telemetry.addData("Target Y", llresult.getTy());
            telemetry.addData("Target Area", llresult.getTa());
        }
    }
}
