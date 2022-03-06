package frc.robot.Subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.Utilities.Map;

public class Climber {
    private TalonFX leftMotor;
    private TalonFX rightMotor;

    private int climbLevel = 0;
    private int oldPOV = -1;


    public Climber(){
        leftMotor = new TalonFX(Map.Climber.leftMotor);
        rightMotor = new TalonFX(Map.Climber.rightMotor);

        initMotor(leftMotor);
        initMotor(rightMotor);

        rightMotor.follow(leftMotor);
        rightMotor.setInverted(TalonFXInvertType.OpposeMaster);

        leftMotor.setNeutralMode(NeutralMode.Brake);
        rightMotor.setNeutralMode(NeutralMode.Brake);
    
        climbLevel = 0;
        oldPOV = -1;
    }

    public void DriveClimber(double climbPower){
        leftMotor.set(ControlMode.PercentOutput, climbPower);
        //rightMotor.set(ControlMode.PercentOutput, climbPower);
    }

    public void ClimbTime(int pov){
        //Uses the PoV Directions to Switch the level of the Climber, 0 for Down, 1 for Low Wrung, 2 for Mid Wrung
        if(pov!= -1){
            if(pov == 180){
                climbLevel = 0;
            }else if(oldPOV == -1 && pov == 0){
                if(climbLevel == 0){
                    climbLevel = 1;
                }else if(climbLevel == 1){
                    climbLevel = 2;
                }
            }
        }
        oldPOV = pov;

        
        //Sets the motor to the level it's suppose to be depending on the Climb Level
        double climbPosition = 0;
        if(climbLevel==0){
            climbPosition = 0;
        }else if(climbLevel == 1){
            climbPosition = 1000;
        } else if(climbLevel == 2){
            climbPosition = 3000;
        }
        leftMotor.set(TalonFXControlMode.MotionMagic, climbPosition);
    }
    

    private void initMotor(TalonFX motor){
        int kSlotIdx = 0;
        int kPIDLoopIdx = 0;
        int kTimeoutMs = 30;

        motor.configFactoryDefault();
        motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, kPIDLoopIdx, kTimeoutMs);
        motor.configNeutralDeadband(0.001, kTimeoutMs);

        /* Set relevant frame periods to be at least as fast as periodic rate */
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, kTimeoutMs);
		motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, kTimeoutMs);

        motor.configNominalOutputForward(0, kTimeoutMs);
		motor.configNominalOutputReverse(0, kTimeoutMs);
		motor.configPeakOutputForward(1, kTimeoutMs);
		motor.configPeakOutputReverse(-1, kTimeoutMs);

        /* Set Motion Magic gains in slot0 - see documentation */
		motor.selectProfileSlot(kSlotIdx, kPIDLoopIdx);
		motor.config_kF(kSlotIdx, 0.2, kTimeoutMs);
		motor.config_kP(kSlotIdx, 0.2, kTimeoutMs);
		motor.config_kI(kSlotIdx, 0.0, kTimeoutMs);
		motor.config_kD(kSlotIdx, 0.0, kTimeoutMs);

		/* Set acceleration and vcruise velocity - see documentation */
		motor.configMotionCruiseVelocity(15000, kTimeoutMs);
		motor.configMotionAcceleration(6000, kTimeoutMs);

		/* Zero the sensor once on robot boot up */
		motor.setSelectedSensorPosition(0, kPIDLoopIdx, kTimeoutMs);

    }
}
