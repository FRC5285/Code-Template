// FRC Team 5285 Code Template
// Copyright (C) 2026 FRC Team 5285, FIRST, and other WPILib contributors

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package first.robot;

import org.wpilib.command2.Command;
import org.wpilib.command2.CommandScheduler;
import org.wpilib.framework.TimedRobot;
import org.wpilib.telemetry.Telemetry;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
    private Command autonomousCommand;

    /** If the robot has been in Auton before disable */
    private boolean usedAuton = false;

    /** If the robot has been in Teleop before disable */
    private boolean usedTeleop = false;

    private final RobotContainer robotContainer;

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    public Robot() {
        // Instantiate our RobotContainer.    This will perform all our button bindings, and put our
        // autonomous chooser on the dashboard.
        this.robotContainer = new RobotContainer();
    }

    /**
     * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
     * that you want ran during disabled, autonomous, teleoperated and utility.
     *
     * <p>This runs after the mode specific periodic functions, but before LiveWindow and
     * SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        // Runs Command Scheduler - runs commands from code
        CommandScheduler.getInstance().run();

        Telemetry.log("Robot", this.robotContainer);
    }

    /** This function is called once each time the robot enters Disabled mode. */
    @Override
    public void disabledInit() {
        // If teleop has been used, start the robot normally when enabled.
        // If teleop has not been used and auton has not been used, nothing happens
        // If teleop hasn't been used yet but auton has, the robot positioning wouldn't be reset
        // when the robot is enabled again (for the three seconds between auton and teleop)
        if (this.usedTeleop) {
            this.usedAuton = false;
            this.usedTeleop = false;
        }
    }

    @Override
    public void disabledPeriodic() {}

    /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
    @Override
    public void autonomousInit() {
        this.robotContainer.resetPIDs();
        this.robotContainer.resetSide();
        this.usedAuton = true;

        this.autonomousCommand = robotContainer.getAutonomousCommand();

        // schedule the autonomous command (example)
        if (this.autonomousCommand != null) {
            CommandScheduler.getInstance().schedule(autonomousCommand);
        }
    }

    /** This function is called periodically during autonomous. */
    @Override
    public void autonomousPeriodic() {}

    @Override
    public void autonomousExit() {
        // This makes sure that the autonomous stops running when
        // autonomous mode ends. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (this.autonomousCommand != null) {
            this.autonomousCommand.cancel();
        }
    }

    /** This function is called once each time the robot enters operator control. */
    @Override
    public void teleopInit() {
        this.usedTeleop = true;

        CommandScheduler.getInstance().schedule(this.robotContainer.getTeleopCommand());

        // Resets robot field orientation only if Auton was not used
        if (!this.usedAuton) {
            this.robotContainer.resetSide();
        }

        this.robotContainer.resetPIDs();
    }

    /** This function is called periodically during operator control. */
    @Override
    public void teleopPeriodic() {}

    @Override
    public void utilityInit() {
        // Cancels all running commands at the start of utility mode.
        CommandScheduler.getInstance().cancelAll();
    }

    /** This function is called periodically during utility mode. */
    @Override
    public void utilityPeriodic() {}

    /** This function is called once when the robot is first started up. */
    @Override
    public void simulationInit() {}

    /** This function is called periodically whilst in simulation. */
    @Override
    public void simulationPeriodic() {}
}
