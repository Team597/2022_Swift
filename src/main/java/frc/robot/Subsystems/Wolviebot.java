package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Utilities.ToggleButton;

public class Wolviebot {
    private static Wolviebot instance = null;

    private Drive drive;
    private CargoHandler cargo;
    private Climber climber;

    private ToggleButton emergencyClimbOverride = null;
    private int AutoTicker = 0;


    public Wolviebot(){
        drive = new Drive();
        cargo = new CargoHandler();
        climber = new Climber();

        emergencyClimbOverride = new ToggleButton();
        AutoTicker = 0;
    }

    public void AutoInit(){
        AutoTicker = 0;
        cargo.StopShooter();
    }

    public void Driver(XboxController controller){
        double turn = controller.getRightX();
        double throttle = -controller.getLeftY();
        boolean quickTurn = (controller.getLeftTriggerAxis() > 0.5) ? true : false;

        boolean speedMode = controller.getLeftBumper();

        //Squareing The Input
        turn = turn * Math.abs(turn);
        throttle = throttle * Math.abs(throttle);

        drive.shiftDrive(speedMode);
        drive.curvieDrive(throttle, turn, quickTurn);
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
        double velocity = 9500;//10000
        if(cargo.LowShot){
            velocity = velocity * 0.65;
        }
        cargo.VelocityShot(shooting, velocity);

        //Hood Control
        int whichShot = 0;
        if(lowShot){
            whichShot = 1;
        }else if(highShot){
            whichShot = 2;
        }
        cargo.HoodControl(whichShot);
    }

    public void AutoTime(){
        AutoTicker++;
        int powerShot = 0; //6500
        boolean shooting = false;
        cargo.HoodControl(1);
        if(AutoTicker>= 30 && AutoTicker<=200){
            powerShot = 6500;
            shooting = true;
            cargo.VelocityShot(shooting, powerShot);
        } else{
            powerShot = 0;
            shooting = false;
            cargo.StopShooter();
            if(AutoTicker>=200 && AutoTicker<=350){
                drive.curvieDrive(0.3, 0, false);
            } else{
                drive.curvieDrive(0, 0, false);
            }
        }
        //cargo.VelocityShot(shooting, powerShot);
        
    }



    public static Wolviebot getInstance() {

		if (instance == null) {
			instance = new Wolviebot();
		}
		return instance;
    }
}
