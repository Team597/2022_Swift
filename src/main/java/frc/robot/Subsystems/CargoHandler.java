package frc.robot.Subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.Utilities.Map;

public class CargoHandler {
    private TalonSRX intakeMotor, indexerMain, indexerShooter;
    private TalonFX shooterTop,shooterBottom;
    private DoubleSolenoid intakeSolenoid;
    private DoubleSolenoid hoodSolenoid;
    private boolean isShooting;
    public boolean LowShot = true;
    private int BangBang = 0;


    public CargoHandler(){
        intakeMotor = new TalonSRX(Map.Intake.intakeMotor);
        indexerMain = new TalonSRX(Map.Intake.indexMain);
        indexerShooter = new TalonSRX(Map.Intake.indexShooter);
        indexerShooter.setInverted(true);
        shooterTop = new TalonFX(Map.Shooter.topShooterMotor);
        shooterBottom = new TalonFX(Map.Shooter.btmShooterMotor);
        shooterBottom.setNeutralMode(NeutralMode.Coast);
        shooterTop.setNeutralMode(NeutralMode.Coast);

        initMotor(shooterTop);
        initMotor(shooterBottom);


        intakeSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Map.Intake.indexerIn, Map.Intake.indexerOut);
        hoodSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Map.Shooter.hoodIn, Map.Shooter.hoodOut);

        isShooting = false;
        LowShot = true;
        BangBang = 0;
    }

    public void DriveIntake(boolean intaking, boolean outaking){
        double indexIntakePower = 0.0;
        double intakePower = 0.0;
        double indexShooterPower = 0.0;
        if(intaking){
            intakeSolenoid.set(Value.kForward);
            indexIntakePower = 0.75;
            intakePower = 0.9;
            indexShooterPower = 1.0;
        } else if(outaking){
            intakeSolenoid.set(Value.kForward);
            indexIntakePower = -0.75;
            intakePower = -0.9;
            indexShooterPower = 1.0;
        } else{
            intakeSolenoid.set(Value.kReverse);
            indexIntakePower = 0.0;
            intakePower = 0.0;
            indexShooterPower = 0.0;
        }
        
        if(!isShooting){
            intakeMotor.set(ControlMode.PercentOutput, intakePower);
            indexerMain.set(ControlMode.PercentOutput, indexIntakePower);
            indexerShooter.set(ControlMode.PercentOutput, indexShooterPower);
        }
    }


    public void StopShooter(){
        shooterTop.set(ControlMode.PercentOutput, 0.0);
        shooterBottom.set(ControlMode.PercentOutput, 0.0);
        indexerShooter.set(ControlMode.PercentOutput, 0.0);
        indexerMain.set(ControlMode.PercentOutput, 0.0);
    }
    public void VelocityShot(boolean shooting, double velocity){
        //Variables Section
        double indexPower = 0.0; //Indexer
        double toshooterPower = 0.0; //Indexer to Shooter
        double shooterPower = 0.0; //Shooter


        if(shooting){ // If the Shooter is suppose to run
            isShooting = true; //Important to turn off other buttons
            if(shooterBottom.getSelectedSensorVelocity() >= velocity-100){ //Only Checks the Bottom Motor's Velocity 
                BangBang++;
            }
            if(BangBang>=10){ //Activates the Belts to feed to shooter
                indexPower = 0.75;
                toshooterPower = -0.9;
            }
        } else{
            isShooting = false; //Renable other buttons
            BangBang = 0; //Reset BangBang
        }

        //Motors Set Area
        if(shooting){ //Sets the motors to velocity mode to shoot
            shooterTop.set(ControlMode.Velocity,-velocity);
            shooterBottom.set(ControlMode.Velocity,velocity);
            indexerMain.set(ControlMode.PercentOutput, indexPower); //Sets the Indexer Motor
            indexerShooter.set(ControlMode.PercentOutput, toshooterPower); //Sets the To Shooter Motor
        } else{//Stops Shooter Motors
           shooterTop.set(ControlMode.PercentOutput, -shooterPower);
            shooterBottom.set(ControlMode.PercentOutput,shooterPower);
        }
    }



    //Hood Control, int mode is either 0 for nothing, 1 for Hood Down, 2 for Hood Up
    public void HoodControl(int mode){
        switch(mode){
            case 1: LowShot = true;
            break;
            case 2: LowShot = false;
            break;
        }
        if(LowShot){
            //Have the Hood Down with Pneumatics
            hoodSolenoid.set(Value.kForward);
        }else{
            //Have the Hood Up with Pneumatics
            hoodSolenoid.set(Value.kReverse);
        }
    }

    //Function to Initialize the Motors
    private void initMotor(TalonFX motor){
        motor.configFactoryDefault();//Reset to factory
        motor.configNeutralDeadband(0.001);//Kill some of the smaller numbers
        motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,0,10);//Configuring the Sensor
        
        motor.config_kF(0, 1023.0/20660.0); //Feed-Forward
        motor.config_kP(0, 0.1);//Proportional
        motor.config_kI(0, 0.001);//Iterative
        motor.config_kD(0, 2.0);//Derivative
        motor.config_IntegralZone(0, 500);//Intergral Zone
        motor.configClosedLoopPeakOutput(0, 1.00);// Closed-Look Peak Output

        motor.getSensorCollection().setIntegratedSensorPosition(0, 10); //Resets the Position to 0
    }

}
