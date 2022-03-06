package frc.robot.Utilities;

public class Map{
    
    public static class Main{
        public static final int
            mainPad = 0, coPad = 1;
    }

    public static class Drive{
        public static final int //Motor IDs
            leftMain = 10, leftFollow = 11,
            rightMain = 12, rightFollow = 13;

        public static final int //Pneumatics Shifters
            shiftIn = 14, shiftOut = 11;
    }

    public static class Intake{
        public static final int //ID for Motors
            intakeMotor = 8, indexMain = 9, indexShooter = 7; 
        
        public static final int //ID for Pneumatics
            indexerIn = 10, indexerOut = 13;
    }
    
    public static class Shooter{
        public static final int 
            topShooterMotor = 31, btmShooterMotor = 32;
        public static final int
            hoodIn = 15, hoodOut = 12;
    }

    public static class Climber{
        public static final int
            leftMotor = 21, rightMotor = 22;
    }
}