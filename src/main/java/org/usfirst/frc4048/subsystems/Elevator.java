/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc4048.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc4048.Robot;
import org.usfirst.frc4048.RobotMap;
import org.usfirst.frc4048.commands.elevator.ElevatorMoveManual;
import org.usfirst.frc4048.utils.ElevatorPosition;
import org.usfirst.frc4048.utils.MechanicalMode;
import org.usfirst.frc4048.utils.MotorUtils;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  private WPI_TalonSRX elevatorMotor;

  private final int TIMEOUT = 100;

  private final double ELEVATOR_POSITION_ERROR = 0;

  private final double ELEVATOR_UP_SCALE_FACTOR = 0.6;
  private final double ELEVATOR_DOWN_SCALE_FACTOR = 0.35;

  private final double ELEVATOR_CARGO_P = 10;
  private final double ELEVATOR_CARGO_I = 0;
  private final double ELEVATOR_CARGO_D = 3;
  private final double ELEVATOR_CARGO_F = 0;

  private final double ELEVATOR_HATCH_P = 100;
  private final double ELEVATOR_HATCH_I = 3;
  private final double ELEVATOR_HATCH_D = 1;
  private final double ELEVATOR_HATCH_F = 0;

  private final int ELEVATOR_ACCEL = 281/* 281 */; // RPM Of motor we can use these values to set max speed during the movement
  private final int ELEVATOR_CRUISE_VELOCITY = 281/* 281 */; // ^
  
  private final int ENCODER_CLOSED_LOOP_ERROR = 12000;

  private double elevatorSetpoint;

  private double elevatorP;
  private double elevatorI;
  private double elevatorD;
  private double elevatorF;

  private final boolean CARGO_MODE = true;
  private final boolean HATCH_MODE = false;

  public Elevator() {

    elevatorMotor = new WPI_TalonSRX(RobotMap.ELEVATOR_MOTOR_ID);
    elevatorMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TIMEOUT);
    elevatorMotor.configNominalOutputForward(0, TIMEOUT);
    elevatorMotor.configNominalOutputReverse(0, TIMEOUT);
    elevatorMotor.configPeakOutputForward(ELEVATOR_UP_SCALE_FACTOR, TIMEOUT);
    elevatorMotor.configPeakOutputReverse(-ELEVATOR_DOWN_SCALE_FACTOR, TIMEOUT);
    elevatorMotor.setNeutralMode(NeutralMode.Brake);
    elevatorMotor.selectProfileSlot(0, 0);
    elevatorMotor.setInverted(true);
    elevatorMotor.configAllowableClosedloopError(0, ENCODER_CLOSED_LOOP_ERROR, TIMEOUT); //This is the margin of error on the encoder value while doing closed loop functions. This will change
    // int elevatorMode = Robot.mechanicalMode.getMode();
    int elevatorMode = RobotMap.HATCH_RETURN_CODE;
    switch(elevatorMode){
      case RobotMap.CARGO_RETURN_CODE:
        setPID(CARGO_MODE);
        break;
      case RobotMap.HATCH_RETURN_CODE:
        setPID(HATCH_MODE);
        break;
    }
    
    // elevatorMotor.configMotionAcceleration(ELEVATOR_ACCEL, TIMEOUT);
    // elevatorMotor.configMotionCruiseVelocity(ELEVATOR_CRUISE_VELOCITY, TIMEOUT);
    elevatorMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
    elevatorMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);

    resetEncoder();
    elevatorSetpoint = getEncoder();
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Elevator setpoint", elevatorSetpoint);
    SmartDashboard.putNumber("Elevator Encoder", getEncoder());
    System.out.println("Elevator Encoder: " + getEncoder());
    SmartDashboard.putNumber("Elevator Current", elevatorMotor.getOutputCurrent());
    moveElevator();
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    setDefaultCommand(new ElevatorMoveManual());
  }

  public void moveElevator() {
    double position = elevatorSetpoint;
    elevatorMotor.set(ControlMode.Position, (int) position);
  }

  public void elevatorToPosition(ElevatorPosition elevatorPosition) {
    elevatorSetpoint = elevatorPosition.getPosition();
  }

  public boolean elevatorAtPos(ElevatorPosition elevatorPosition) {
    return Math.abs(getError()) < ELEVATOR_POSITION_ERROR;
  }

  public int getError() {
    return elevatorMotor.getClosedLoopError(0);
  }

  public void resetEncoder() {
    elevatorMotor.setSelectedSensorPosition(0, 0, TIMEOUT);
  }

  public double getEncoder() {
    return elevatorMotor.getSelectedSensorPosition(0);
  }

  public boolean getTopSwitch() {
    return elevatorMotor.getSensorCollection().isFwdLimitSwitchClosed();
  }

  public boolean getBotSwitch() {
    return elevatorMotor.getSensorCollection().isRevLimitSwitchClosed();
  }

  public void stopElevator() {
    elevatorSetpoint = getEncoder();
  }

  public void fineTune(double speed) {
    elevatorSetpoint += speed;
  }

  public void setPID(boolean isCargo) {
    if (isCargo) {
      elevatorP = ELEVATOR_CARGO_P;
      elevatorI = ELEVATOR_CARGO_I;
      elevatorD = ELEVATOR_CARGO_D;
      elevatorF = ELEVATOR_CARGO_F;
    } else {
      elevatorP = ELEVATOR_HATCH_P;
      elevatorI = ELEVATOR_HATCH_I;
      elevatorD = ELEVATOR_HATCH_D;
      elevatorF = ELEVATOR_HATCH_F;
    }
    
    elevatorMotor.config_kP(0, elevatorP, TIMEOUT);
    elevatorMotor.config_kI(0, elevatorI, TIMEOUT);
    elevatorMotor.config_kD(0, elevatorD, TIMEOUT);
    elevatorMotor.config_kF(0, elevatorF, TIMEOUT);
    
  }
}