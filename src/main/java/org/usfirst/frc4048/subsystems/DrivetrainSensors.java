/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc4048.subsystems;

import org.usfirst.frc4048.utils.CameraAngles;
import org.usfirst.frc4048.utils.CameraDistance;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import org.usfirst.frc4048.utils.LimeLightVision;

/**
 * This subsystem holds the sensors the robot uses for navigation
 */
public class DrivetrainSensors extends Subsystem {

    private Ultrasonic ultrasonic;

    private LimeLightVision limelight;

    private NetworkTableEntry unltrasonicEntry = Shuffleboard.getTab("DrivetrainSensors").add("Ultrasonic Distance", 0.0).getEntry();
    private NetworkTableEntry limelightValidTargetEntry = Shuffleboard.getTab("DrivetrainSensors").add("LimelightValidTarget", false).getEntry();
    private NetworkTableEntry limelightXEntry = Shuffleboard.getTab("DrivetrainSensors").add("LimelightX", 0.0).getEntry();
    private NetworkTableEntry LimelightYEntry = Shuffleboard.getTab("DrivetrainSensors").add("LimelightY", 0.0).getEntry();
    private NetworkTableEntry limelightForwardEntry = Shuffleboard.getTab("DrivetrainSensors").add("LimelightForward", 0.0).getEntry();
    private NetworkTableEntry limelightSidewaysEntry = Shuffleboard.getTab("DrivetrainSensors").add("LimelightSideways", 0.0).getEntry();

    public DrivetrainSensors() {
        ultrasonic = new Ultrasonic(8, 9);
        ultrasonic.setAutomaticMode(true);

        limelight = new LimeLightVision();
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop
        CameraDistance targetDistance = getTargetDistance();
        limelightValidTargetEntry.setBoolean(targetDistance != null);
        if (targetDistance != null) {
            limelightForwardEntry.setDouble(targetDistance.getForward());
            limelightSidewaysEntry.setDouble(targetDistance.getSideways());
        }

        // unltrasonicEntry.setDouble(ultrasonic.getRangeInches());

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void init() {
    }

    public double getUltrasonicDistance() {
        return ultrasonic.getRangeInches();
    }

    public CameraDistance getTargetDistance() {
        return limelight.getTargetDistance();
    }

    public CameraAngles getCameraAngles() {
        return limelight.getCameraAngles();
    }

    public void ledOn() {
        limelight.setLedOn();
    }

    public void ledOff() {
        limelight.setLedOff();
    }
}