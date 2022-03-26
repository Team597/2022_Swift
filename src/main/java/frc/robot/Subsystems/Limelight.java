package frc.robot.Subsystems;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Limelight {
    private double lX, lY, lV, lHor;
    private double turnPower = 0.035;
    private double turnLimit = 0.2;

    public Limelight(){
        lX = 0.0;
        lY = 0.0;
        lV = 0.0;
        lHor = 0.0;
    }

    public void UpdateLimelight(){
        lX = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        lY = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
        lV = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        lHor = NetworkTableInstance.getDefault().getTable("limelight").getEntry("thor").getDouble(0);
        SmartDashboard.putNumber("Lime X", lX);
        SmartDashboard.putNumber("Lime Y", lY);
        SmartDashboard.putNumber("Lime Hor", lHor);
    }

    public void GetHor(){
        SmartDashboard.putNumber("Lime Hor", lHor);
    }

    public double AimY(){
        double rpm = 0;
        double defaultRPM = 7000;
        if(lV >= 1.0){
            rpm = defaultRPM + (3500 * Math.abs(1.0 - ScaleY(lY)));
        }
        return rpm;
    }

    public double SmartShot(){
        double powerOutput = 9500;
        if(lV >= 1.0){
            powerOutput = (-6.3127*Math.pow(lY, 3)) + (251.17*Math.pow(lY,2)) - 3272.9*lY + 23679; 
        }
        powerOutput = Math.min(Math.max(powerOutput, 9400),20000);
        SmartDashboard.putNumber("Smart Power", powerOutput);
        return powerOutput;
    }
    public double AimX(){
        double targetX = 0.0;
        if(lV >= 1.0){
            targetX = lX * turnPower;
            if(targetX > turnLimit){
                targetX = turnLimit;
            } else if(targetX < -turnLimit){
                targetX = -turnLimit;
            }
        }

        return targetX;
    }


    private double ScaleY(double unscale){
        double newY = 0.0;
        newY = 1 * (unscale + 24) / (-4+24); // change the numbers to -
        //newY = Math.round(newY*100);
        //newY = newY/100;
        return newY;
    }
}
