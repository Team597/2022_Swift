package frc.robot.Subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.Utilities.Map;
import frc.robot.Utilities.Utility;

public class Drive {
    //Motors being declared
    private TalonFX leftMain, leftFollow, rightMain, rightFollow;

    //Pneumatic Shifters
    private DoubleSolenoid shiftGear;

    //Values for Curvie Drive
    private double m_quickStopThreshold = 0.2;
	private double m_quickStopAlpha = 0.1;
	private double m_quickStopAccumulator = 0.0;
	protected double m_maxOutput = 1.0;

    //Initialize all Variables
    public Drive(){
        //Set Up Motors
        leftMain = new TalonFX(Map.Drive.leftMain);
        leftFollow = new TalonFX(Map.Drive.leftFollow);
        rightMain = new TalonFX(Map.Drive.rightMain);
        rightFollow = new TalonFX(Map.Drive.rightFollow);

        rightMain.setInverted(true);
        rightFollow.setInverted(true);

        //Have Follows follow the Main
        leftFollow.follow(leftMain);
        rightFollow.follow(rightMain);

        //Set Motors to Coast
        setBrakeMode(false);

        //Set up Pneumatics
        shiftGear = new DoubleSolenoid(PneumaticsModuleType.REVPH, Map.Drive.shiftIn,Map.Drive.shiftOut);
        shiftGear.set(Value.kReverse);


    }

    //Brake Mode Function - FALSE for Coast, TRUE for break
    public void setBrakeMode(boolean breaking){
        NeutralMode Mode;
        if(breaking){
            Mode = NeutralMode.Brake;
        }else {
            Mode = NeutralMode.Coast;
        }
        leftMain.setNeutralMode(Mode);
        rightMain.setNeutralMode(Mode);
        leftFollow.setNeutralMode(Mode);
        rightFollow.setNeutralMode(Mode);
    }

    //Shift the Shifting Shifters - TRUE for SPEED MODE, FALSE for NORMAL MODE
    public void shiftDrive(boolean isShifting){
        if(isShifting){
            shiftGear.set(Value.kForward);
        } else{
            shiftGear.set(Value.kReverse);
        }
    }

    //Drive the Motors
    public void driveMotors(double leftPower, double rightPower){
        leftPower = Utility.applyDeadband(leftPower, 0.09);
        rightPower = Utility.applyDeadband(rightPower, 0.09);

        leftMain.set(ControlMode.PercentOutput, leftPower);
        rightMain.set(ControlMode.PercentOutput, rightPower);
    }

    public void stopMotors(){
        driveMotors(0, 0);
    }

    //Tank Drive
    public void tankDrive(double leftInput, double rightInput){
        
    }


    //The Infamous Curvie Drive
	public double[] curvieDrive(double xSpeed, double zRotation, boolean isQuickTurn) {

		double deadZone = 0.10;
		xSpeed = Utility.limit(xSpeed);
		xSpeed = Utility.applyDeadband(xSpeed, deadZone);

		zRotation = Utility.limit(zRotation);
		zRotation = Utility.applyDeadband(zRotation, deadZone);

		double angularPower;
		boolean overPower;

		if (isQuickTurn || xSpeed == 0) {
			if (Math.abs(xSpeed) < m_quickStopThreshold) {
				m_quickStopAccumulator = (1 - m_quickStopAlpha) * m_quickStopAccumulator
						+ m_quickStopAlpha * Utility.limit(zRotation) * 2;
			}
			overPower = true;
			angularPower = zRotation;// * 0.75;
		} else {
			overPower = false;
			angularPower = Math.abs(xSpeed) * zRotation - m_quickStopAccumulator;

			if (m_quickStopAccumulator > 1) {
				m_quickStopAccumulator -= 1;
			} else if (m_quickStopAccumulator < -1) {
				m_quickStopAccumulator += 1;
			} else {
				m_quickStopAccumulator = 0.0;
			}
		}
		//SmartDashboard.putNumber("turn", angularPower);
		double leftMotorOutput = xSpeed + angularPower;
		double rightMotorOutput = xSpeed - angularPower;

		if (overPower) {
			if (leftMotorOutput > 1.0) {
				rightMotorOutput -= leftMotorOutput - 1.0;
				leftMotorOutput = 1.0;
			} else if (rightMotorOutput > 1.0) {
				leftMotorOutput -= rightMotorOutput - 1.0;
				rightMotorOutput = 1.0;
			} else if (leftMotorOutput < -1.0) {
				rightMotorOutput -= leftMotorOutput + 1.0;
				leftMotorOutput = -1.0;
			} else if (rightMotorOutput < -1.0) {
				leftMotorOutput -= rightMotorOutput + 1.0;
				rightMotorOutput = -1.0;
			}
		}

		driveMotors(leftMotorOutput, rightMotorOutput);
		return new double []{leftMotorOutput,rightMotorOutput};
	}

}
