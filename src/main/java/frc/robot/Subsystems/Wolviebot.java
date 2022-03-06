package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.XboxController;

public class Wolviebot {
    private static Wolviebot instance = null;

    private Drive drive;
    private CargoHandler cargo;
    private Climber climber;

    public Wolviebot(){
        drive = new Drive();
        cargo = new CargoHandler();
        climber = new Climber();
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
       
        double climbAxis = controller.getLeftY();
        boolean emergencyClimbOverride = controller.getStartButton();
        int climbPOV = controller.getPOV(0); //0 is up, 180 is Down
        
        //Drive The Climber
        if(emergencyClimbOverride){
            climber.DriveClimber(climbAxis);
        }else {
            climber.ClimbTime(climbPOV);
        }
        

        //Drive the Intake
        cargo.DriveIntake(intake, outtake);
        
        //Shooting Time
        double velocity = 10000;
        if(cargo.LowShot){
            velocity = velocity * 0.8;
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



    public static Wolviebot getInstance() {

		if (instance == null) {
			instance = new Wolviebot();
		}
		return instance;
    }
}
