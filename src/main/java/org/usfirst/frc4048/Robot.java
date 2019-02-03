/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc4048;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc4048.commands.drive.DriveAlignPhase2;
import org.usfirst.frc4048.commands.drive.DriveAlignPhase3;
import org.usfirst.frc4048.commands.drive.DriveDistance;
import org.usfirst.frc4048.commands.pneumatics.ExampleSolenoidCommand;

import org.usfirst.frc4048.subsystems.CompressorSubsystem;
import org.usfirst.frc4048.subsystems.DriveTrain;
import org.usfirst.frc4048.subsystems.ExampleSolenoidSubsystem;

import org.usfirst.frc4048.commands.drive.DriveDistanceMaintainAngle;
import org.usfirst.frc4048.commands.drive.CentricModeToggle;
import org.usfirst.frc4048.commands.drive.DriveAlignGroup;
import org.usfirst.frc4048.commands.limelight.LimelightToggle;
import org.usfirst.frc4048.commands.drive.RotateAngle;
import org.usfirst.frc4048.commands.drive.RotateAngleForAlignment;

import org.usfirst.frc4048.subsystems.DriveTrain;
import org.usfirst.frc4048.utils.Logging;
import org.usfirst.frc4048.utils.MechanicalMode;
import org.usfirst.frc4048.subsystems.PowerDistPanel;
import org.usfirst.frc4048.utils.WorkQueue;
import org.usfirst.frc4048.subsystems.DrivetrainSensors;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {  
  public static OI oi;
  public static DriveTrain drivetrain;
  public static Logging logging;
  public static PowerDistPanel pdp;
  public static WorkQueue wq;
  public static double timeOfStart = 0;
  public static CompressorSubsystem compressorSubsystem;
  public static ExampleSolenoidSubsystem solenoidSubsystem;
  public static DrivetrainSensors drivetrainSensors;
  public static MechanicalMode mechanicalMode;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  

  

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    mechanicalMode = new MechanicalMode();
    int mode = mechanicalMode.getMode();
    if(mode == RobotMap.CARGO_RETURN_CODE) {

    } else if (mode == RobotMap.HATCH_RETURN_CODE) {

    } else {
      
    }
    if(RobotMap.ENABLE_DRIVETRAIN) {
      drivetrain = new DriveTrain();
    }
    pdp = new PowerDistPanel();
    compressorSubsystem = new CompressorSubsystem();
    solenoidSubsystem = new ExampleSolenoidSubsystem();
    drivetrainSensors = new DrivetrainSensors();
    
    // OI must be initilized last
    oi = new OI();
    // Robot.drivetrainSensors.ledOn();
    SmartDashboard.putData("Auto mode", m_chooser);

    WorkQueue wq = new WorkQueue(512);
		logging = new Logging(100, wq);
		logging.startThread(); // Starts the logger
  }
  
  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

    SmartDashboard.putData("Extend Piston", new ExampleSolenoidCommand(true));
    SmartDashboard.putData("Retract Piston", new ExampleSolenoidCommand(false));
    SmartDashboard.putNumber("Current", Robot.compressorSubsystem.getCurrent());
    SmartDashboard.putBoolean("Pressure", Robot.compressorSubsystem.getPressure());
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
    // Robot.drivetrainSensors.ledOff();
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    
    // SmartDashboard.putData(new LimelightAlign());
    SmartDashboard.putData("Limelight On", new LimelightToggle(true));
    SmartDashboard.putData("Limelight Off", new LimelightToggle(false));
    
    if(RobotMap.ENABLE_DRIVETRAIN) {
      Robot.drivetrain.swerveDrivetrain.setModeField();
    
      // Shuffleboard.getTab("Approach").add("90", new RotateAngle(90));
      // Shuffleboard.getTab("Approach").add("-45", new RotateAngle(-45));
      // Shuffleboard.getTab("Approach").add("0", new RotateAngle(0));
      // Shuffleboard.getTab("Approach").add("10", new RotateAngle(10));
      // Shuffleboard.getTab("Approach").add("-30", new RotateAngle(-30));
  
      // Shuffleboard.getTab("Approach").add("TargetAlign", new DriveTargetCenter(10.0, -0.25));
    
      // SmartDashboard.putData(new DriveDistance(80, 0.1, 0.05, 0.0));

      // SmartDashboard.putData(new DriveDistanceMaintainAngle(40, 20, -0.45, -0.3));
      SmartDashboard.putData(new DriveAlignGroup());
      SmartDashboard.putData(new RotateAngle(0)); 
      // SmartDashboard.putData(new RotateAngleForAlignment());
      SmartDashboard.putData("Toggle Centric Mode", new CentricModeToggle());
      SmartDashboard.putData(new DriveAlignPhase2(0.3, 0.4, false));
      SmartDashboard.putData(new DriveAlignPhase3(0.25, false));
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    if(RobotMap.ENABLE_DRIVETRAIN) {
      SmartDashboard.putData(new DriveDistance(10, 0.3, 0.0, 0.0));
      SmartDashboard.putData(new RotateAngle(90));
      SmartDashboard.putNumber("Gyro", Robot.drivetrain.getGyro());
    }
    Scheduler.getInstance().run();
  
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {

    Scheduler.getInstance().run();
  }



}
