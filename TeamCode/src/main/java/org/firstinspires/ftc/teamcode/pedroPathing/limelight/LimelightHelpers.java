package org.firstinspires.ftc.teamcode.pedroPathing.limelight;

public class LimelightHelpers {
    public static class LimelightResults {
        public TargetingResults targetingResults;

        public LimelightResults() {
            targetingResults = new TargetingResults();
        }
    }

    public static class TargetingResults {
        public boolean valid;
        public double ta;
        public double tx;
    }

    public static void setPipelineIndex(String limelightName, int pipelineIndex) {
        // Implementation not included
    }

    public static LimelightResults getLatestResults(String limelightName) {
        LimelightResults results = new LimelightResults();
        results.targetingResults.valid = true;
        results.targetingResults.ta = 0.5; // dummy value
        results.targetingResults.tx = 15.0; // dummy value
        return results;
    }
}
