package frc.robot.Subsystems;

import edu.wpi.first.wpilibj.XboxController;

public class Wolviebot {
    private static Wolviebot instance = null;

    private Drive drive;
    private CargoHandler cargo;

    public Wolviebot(){
        drive = new Drive();
        cargo = new CargoHandler();
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

        cargo.ShootingTime(shooting);
        cargo.DriveIntake(intake, outtake);
    }



    public static Wolviebot getInstance() {

		if (instance == null) {
			instance = new Wolviebot();
		}
		return instance;
    }
}
