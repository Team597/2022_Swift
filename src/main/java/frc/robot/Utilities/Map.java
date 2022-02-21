package frc.robot.Utilities;

public class Map{
    
    public static class Main{
        public static final int
            mainPad = 0, coPad = 1;
    }

    public static class Drive{
        public static final int //Motor IDs
            leftMain = 0, leftFollow = 1,
            rightMain = 2, rightFollow = 3;

        public static final int //Pneumatics Shifters
            shiftIn = 0, shiftOut = 1;
    }

    public static class Intake{
        public static final int //ID for Motors
            intakeMotor = 4, indexMain = 5, indexShooter = 6; 
        
        public static final int //ID for Pneumatics
            indexerIn = 2, indexerOut = 3;
    }
    
    public static class Shooter{
        public static final int 
            topShooterMotor = 8, btmShooterMotor = 7;
        public static final int
            hoodIn = 4, hoodOut = 5;
    }

}