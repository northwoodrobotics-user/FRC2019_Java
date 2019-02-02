package org.usfirst.frc4048.commands.cargo;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc4048.Robot;

public class EjectCargoStart extends Command {
    public EjectCargoStart() {
        requires(Robot.cargoSubsystem);
    }

    // Called just before this Command runs the first time
	@Override
	protected void initialize() {
		setTimeout(0.2);
        Robot.cargoSubsystem.cargoOutput();
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
   
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return isTimedOut();
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
        end();
	}
}