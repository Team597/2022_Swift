package frc.robot.Subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.Utilities.Map;

public class CargoHandler {
    private TalonSRX intakeMotor, indexerMain, indexerShooter;
    private TalonFX Shooter_top,Shooter_bottom;
    //private DoubleSolenoid intakeSolenoid;
    private boolean isShooting;
    int BangBang = 0;


    public CargoHandler(){
        intakeMotor = new TalonSRX(Map.Intake.intakeMotor);
        indexerMain = new TalonSRX(Map.Intake.indexMain);
        indexerShooter = new TalonSRX(Map.Intake.indexShooter);
        Shooter_top = new TalonFX(Map.Intake.Shooter_top);
        Shooter_bottom = new TalonFX(Map.Intake.Shooter_bottom);
        Shooter_bottom.setNeutralMode(NeutralMode.Coast);
        Shooter_top.setNeutralMode(NeutralMode.Coast);

        //intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Map.Intake.indexerIn, Map.Intake.indexerOut);

        isShooting = false;
        BangBang = 0;
    }

    public void DriveIntake(boolean intaking, boolean outaking){
        double indexIntakePower = 0.0;
        double intakePower = 0.0;
        double indexShooterPower = 0.0;
        if(intaking){
            //intakeSolenoid.set(Value.kForward);
            indexIntakePower = 0.75;
            intakePower = 0.6;
            indexShooterPower = 1.0;
        } else if(outaking){
           // intakeSolenoid.set(Value.kForward);
            indexIntakePower = -0.75;
            intakePower = -0.75;
            indexShooterPower = 1.0;
        } else{
            //intakeSolenoid.set(Value.kReverse);
            indexIntakePower = 0.0;
            intakePower = 0.0;
            indexShooterPower = 0.0;
        }
        
        intakeMotor.set(ControlMode.PercentOutput, intakePower);
        if(!isShooting){
            indexerMain.set(ControlMode.PercentOutput, indexIntakePower);
            indexerShooter.set(ControlMode.PercentOutput, indexShooterPower);
        }
    }

    public void ShootingTime(boolean shoot){
        double indexerToShooterPower = 0.0;
        double indexIntakePower = 0.0;
        double shooting_Power = 0.0;
        if(shoot){
            isShooting = true;
            
            shooting_Power = 0.6;
            BangBang++;
            if(BangBang >= 100){
                indexerToShooterPower = -1.0;
                indexIntakePower = 0.75;
            }

        } else{
            isShooting = false;
            indexerToShooterPower = 0.0;
            indexIntakePower = 0.0;
            shooting_Power = 0;
            BangBang = 0;
        }
        indexerShooter.set(ControlMode.PercentOutput, indexerToShooterPower);
        Shooter_top.set(ControlMode.PercentOutput, -shooting_Power);
        Shooter_bottom.set(ControlMode.PercentOutput, shooting_Power);
        if(isShooting){
            indexerMain.set(ControlMode.PercentOutput, indexIntakePower);
        }
    }

}
