package frc.robot.Subsystems;

import javax.xml.xpath.XPathVariableResolver;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Utilities.ToggleButton;

public class Wolviebot {
    private static Wolviebot instance = null;

    private Drive drive;
    private CargoHandler cargo;
    private Climber climber;
    private Limelight lime;

    private ToggleButton emergencyClimbOverride = null;
    private int AutoTicker = 0;
    private boolean autoShooting = false;
    public double TargetShootetPower = 9500;


    public Wolviebot(){
        drive = new Drive();
        cargo = new CargoHandler();
        climber = new Climber();
        lime = new Limelight();

        emergencyClimbOverride = new ToggleButton();
        AutoTicker = 0;
        autoShooting = false;
    }

    public void Driver(XboxController controller){
        double turn = controller.getRightX();
        double throttle = -controller.getLeftY();
        boolean quickTurn = controller.getRightStickButton();
        boolean autoShoot = controller.getRightTriggerAxis() > 0.5 ? true : false;

        boolean speedMode = controller.getLeftBumper();

        //Squareing The Input
        turn = turn * Math.abs(turn);
        throttle = throttle * Math.abs(throttle);

        
        if(autoShoot){
            autoShooting = true;
            double autoTurn = lime.AimX();
            drive.shiftDrive(false);
            drive.curvieDrive(0, autoTurn, true);
            cargo.RevShooter(true);
            if(autoTurn<0.2 && autoTurn > -0.2){
                cargo.RevShooter(false);
                cargo.VelocityShot(true, lime.SmartShot());
                cargo.HoodControl(2);
            }
        } else{
            autoShooting = false;
            drive.shiftDrive(speedMode);
            drive.curvieDrive(throttle, turn, quickTurn);
        }
    }

    public void TeleInit(){
        drive.setBrakeMode(false);
    }

    public void TestInit(){
        TargetShootetPower = 9500;
    }

    public void TestOperator(XboxController controller){
        boolean shooting = (controller.getRightTriggerAxis() > 0.5 ? true : false);
        boolean toShooter = (controller.getLeftTriggerAxis() > 0.5 ? true : false);
        boolean intake = controller.getAButton();
        boolean outtake = controller.getXButton();
        boolean raiseTarget = controller.getRightBumperReleased();
        boolean lowerTarget = controller.getLeftBumperReleased();

        lime.UpdateLimelight();
        lime.SmartShot();

        if(raiseTarget){
            TargetShootetPower += 100;
        }
        if(lowerTarget){
            TargetShootetPower -= 100;
        }

        SmartDashboard.putNumber("Test Target Power", TargetShootetPower);

        if(intake||outtake){
            cargo.DriveIntake(intake, outtake);
        }else{
            cargo.TestToShooter(toShooter);
        }
        cargo.TestVelocity(shooting, lime.SmartShot());
        if(!shooting){
        //    cargo.DriveIntake(false, false);
        }
    }

    public void Operator(XboxController controller){
        boolean intake = controller.getAButton();
        boolean outtake = controller.getXButton();
       
        boolean shooting = (controller.getRightTriggerAxis() > 0.5 ? true : false);
        boolean lowShot = controller.getRightBumper();
        boolean highShot = controller.getLeftBumper();
       
        double climbAxis = -controller.getLeftY();
        boolean climbEmergency = controller.getStartButton();
        int climbPOV = controller.getPOV(0); //0 is up, 180 is Down
        
        //Drive The Climber
        if(emergencyClimbOverride.Input(climbEmergency)){
            climber.DriveClimber(climbAxis);
        }else {
            climber.ClimbTime(climbPOV);
        }
        SmartDashboard.putBoolean("Emergency Climber", emergencyClimbOverride.Output());
        

        //Drive the Intake
        cargo.DriveIntake(intake, outtake);
        
        //Shooting Time
        if(!autoShooting){
            double velocity = 9500;//10000, Max Velocity = 20345
            if(cargo.LowShot){
                velocity = velocity * 0.65;
            }
            cargo.VelocityShot(shooting, velocity);
            //cargo.FullPowerShot(shooting);

            //Hood Control
            int whichShot = 0;
            if(lowShot){
                whichShot = 1;
            }else if(highShot){
                whichShot = 2;
            }
            cargo.HoodControl(whichShot);
        }
    }

    public void LimeUpdate(){
        lime.UpdateLimelight();
    }

    public static Wolviebot getInstance() {

		if (instance == null) {
			instance = new Wolviebot();
		}
		return instance;
    }


    //Auto Modes


    public void AutoInit(){
        AutoTicker = 0;
        cargo.StopShooter();
        drive.setBrakeMode(true);
    }

    public void SingleLowBall(){//Shoots the ball for 150 ticks then drives forward for 50 ticks, then stops
        AutoTicker++;
        int powerShot = 0; //6500
        boolean shooting = false;
        cargo.HoodControl(1);
        if(AutoTicker>= 0 && AutoTicker<=150){
            powerShot = 6500;
            shooting = true;
            cargo.VelocityShot(shooting, powerShot);
        } else{
            powerShot = 0;
            shooting = false;
            cargo.StopShooter();
            if(AutoTicker>=150 && AutoTicker<=200){
                drive.curvieDrive(0.3, 0, false);
            } else{
                drive.curvieDrive(0, 0, false);
            }
        }
    }

    public void TwoHighBall(){
        AutoTicker++;
        if(AutoTicker>=0 && AutoTicker<=90){
            cargo.DriveIntake(true, false);
            drive.curvieDrive(0.4, 0.0, true);
            cargo.RevShooter(true);
        } else if(AutoTicker>=90&&AutoTicker<=100){
            cargo.HoodControl(2);
            cargo.RevShooter(true);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.0, true);
        } else if(AutoTicker>=100&&AutoTicker<=240){
            cargo.HoodControl(2);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, lime.AimX(), true);
            cargo.RevShooter(false);
            cargo.VelocityShot(true, lime.SmartShot());
        } else{
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.0, true);
            cargo.VelocityShot(false, 0);
        }
    }

    public void ThreeHighBall(){
        if(AutoTicker<245){
            TwoHighBall();
        }else{
            AutoTicker++;
            if(AutoTicker>=250 && AutoTicker<=280){ //Turn
                drive.curvieDrive(0.0, 0.5, true);
            } else if(AutoTicker>=280 && AutoTicker<=300){//Stop
                drive.curvieDrive(0.0, 0.0, true);
            } else if(AutoTicker>=300 && AutoTicker<=400){//Drive Forward and Intake
                cargo.DriveIntake(true, false);
                drive.curvieDrive(0.4, 0.0, true);
                cargo.RevShooter(true);
            }else if(AutoTicker>=400&&AutoTicker<=420){ //Stop
                cargo.HoodControl(2);
                cargo.RevShooter(true);
                cargo.DriveIntake(false, false);
                drive.curvieDrive(0.0, 0.0, true);
            } else if(AutoTicker>=420 && AutoTicker<=450){ //Turn
                drive.curvieDrive(0.0, -0.5, true);
            } else if(AutoTicker>=450 && AutoTicker<=490){//Shoot
                drive.curvieDrive(0.0, lime.AimX(), true);
                cargo.RevShooter(false);
                cargo.VelocityShot(true, lime.SmartShot());
            }else{ // STOP ALL
                cargo.DriveIntake(false, false);
                drive.curvieDrive(0.0, 0.0, true);
                cargo.VelocityShot(false, 0);
            }
        }
    }

    public void HighHopes(){
        if(AutoTicker<=500){
            ThreeHighBall();
        }else{
            AutoTicker++;
            if(AutoTicker>=500 && AutoTicker<=520){//TURN
                drive.curvieDrive(0.0, 0.5, true);
            } else if(AutoTicker>=520 && AutoTicker<=530){//Stop
                drive.curvieDrive(0.0, 0.0, true);
            } else if(AutoTicker>=530 && AutoTicker<=620){//Drive Forward and Intake
                cargo.DriveIntake(true, false);
                drive.curvieDrive(0.4, 0.0, true);
                cargo.RevShooter(true);
            }else if(AutoTicker>=630&&AutoTicker<=640){ //Stop
                cargo.HoodControl(2);
                cargo.RevShooter(true);
                cargo.DriveIntake(true, false);
                drive.curvieDrive(0.0, 0.0, true);
            } else if(AutoTicker>=640 && AutoTicker<=690){//Drive Backwards
                cargo.DriveIntake(false, false);
                drive.curvieDrive(-0.4, 0.0, true);
                cargo.RevShooter(true);
            } else if(AutoTicker>=690 && AutoTicker<=750){//Shoot!
                drive.curvieDrive(0.0, lime.AimX(), true);
                cargo.RevShooter(false);
                cargo.VelocityShot(true, lime.SmartShot());
            }else{
                cargo.DriveIntake(false, false);
                drive.curvieDrive(0.0, 0.0, true);
                cargo.VelocityShot(false, 0);
            }
        }
    }

    /*public void ThreeHighBall(){
        AutoTicker++;
        if(AutoTicker>=0 && AutoTicker<=70){ //DRIVE FORWARD
            cargo.DriveIntake(true, false);
            drive.curvieDrive(0.55, 0.0, true);
            cargo.RevShooter(true);
        } else if(AutoTicker>=60&&AutoTicker<=80){ //STOP
            cargo.HoodControl(2);
            cargo.RevShooter(true);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.0, true);
        } else if(AutoTicker>=80&&AutoTicker<=200){//AIM AND SHOOT
            cargo.HoodControl(2);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, lime.AimX(), true);
            cargo.RevShooter(false);
            cargo.VelocityShot(true, lime.SmartShot());
        } else if(AutoTicker>=200&&AutoTicker<=220){//STOP
            cargo.HoodControl(2);
            cargo.RevShooter(true);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.0, true);
        } else if(AutoTicker>=220 && AutoTicker<=250){//TURN
            cargo.VelocityShot(false, 0);
            cargo.RevShooter(true);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.5, true);
        } else if(AutoTicker>=250 && AutoTicker<=270){//STOP
            cargo.HoodControl(2);
            cargo.RevShooter(true);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.0, true);
        } else if(AutoTicker>=270&&AutoTicker<=320){//DRIVE FORWARD
            cargo.DriveIntake(true, false);
            drive.curvieDrive(0.55, 0.0, true);
            cargo.RevShooter(true);
        } else if(AutoTicker>=320 && AutoTicker<=330){//STOP
            cargo.HoodControl(2);
            cargo.RevShooter(true);
            cargo.DriveIntake(true, false);
            drive.curvieDrive(0.0, 0.0, true);
        } else if(AutoTicker>=330 && AutoTicker<=360){//TURN TO SHOOT
            cargo.RevShooter(true);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, -0.55, true);
        } else if(AutoTicker>=360&&AutoTicker<=420){//AIM AND SHOOT
            cargo.HoodControl(2);
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, lime.AimX(), true);
            cargo.RevShooter(false);
            cargo.VelocityShot(true, lime.SmartShot());
        } else{
            cargo.DriveIntake(false, false);
            drive.curvieDrive(0.0, 0.0, true);
            cargo.VelocityShot(false, 0);
        }
    }*/




}
