package frc.robot.Utilities;

public class Utility {
    public static double limit(double limited) {
        if (limited > 1) {
            limited = 1;
        } else if (limited < -1) {
            limited = -1;
        }
        return limited;
    }

    public static double applyDeadband(double input, double deadZone) {
        if (input <= deadZone && input >= -deadZone) {
            input = 0;
        }
        return input;
    } 
}
