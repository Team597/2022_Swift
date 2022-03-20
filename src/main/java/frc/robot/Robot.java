// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Subsystems.Wolviebot;
import frc.robot.Utilities.Map;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */ 

  private static XboxController mainJoystick = null;
  private static XboxController coJoystick = null;
  private Wolviebot robot = null;
  UsbCamera frontCamera, insideCamera;


  private enum StartingPosition{
    ONE_BALL_LOW,TWO_BALL_HIGH,THREE_BALL_HIGH//FOUR_BALL_HIGH,ONE_BALL_HIGH
  };
  private StartingPosition selectedAuto;
  private final SendableChooser<StartingPosition> autoChooser = new SendableChooser<>();

  @Override
  public void robotInit() {
    mainJoystick = new XboxController(Map.Main.mainPad);
    coJoystick = new XboxController(Map.Main.coPad);
    robot = new Wolviebot();
    frontCamera = CameraServer.startAutomaticCapture(0);
    insideCamera = CameraServer.startAutomaticCapture(1);

    autoChooser.setDefaultOption("One Ball Low (Default)", StartingPosition.ONE_BALL_LOW);
    autoChooser.addOption("Two Balls High", StartingPosition.TWO_BALL_HIGH);
    autoChooser.addOption("Three Balls High", StartingPosition.THREE_BALL_HIGH);
    SmartDashboard.putData("Auto Choices", autoChooser);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
    robot.AutoInit();
    selectedAuto = autoChooser.getSelected();
  }

  @Override
  public void autonomousPeriodic() {
    robot.LimeUpdate();
    switch(selectedAuto){
      case ONE_BALL_LOW:
        robot.SingleLowBall();
        break;
      case TWO_BALL_HIGH:
        robot.TwoHighBall();
        break;
      case THREE_BALL_HIGH:
        robot.ThreeHighBall();
        break;
    }
    
  }

  @Override
  public void teleopInit() {
    robot.TeleInit();
  }

  @Override
  public void teleopPeriodic() {
    robot.LimeUpdate();
    robot.Driver(mainJoystick);
    robot.Operator(coJoystick);
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
