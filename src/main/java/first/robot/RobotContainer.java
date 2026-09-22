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
import org.wpilib.command2.button.CommandXboxController;
import org.wpilib.telemetry.TelemetryLoggable;
import org.wpilib.telemetry.TelemetryTable;

import first.robot.Constants.OperatorConstants;
import first.robot.subsystems.AutoSubsystem;
import first.robot.subsystems.ExampleSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer implements TelemetryLoggable {

    private final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();

    private final AutoSubsystem autoSubsystem = new AutoSubsystem(this.exampleSubsystem);

    private final CommandXboxController driverController = new CommandXboxController(
        OperatorConstants.DRIVER_CONTROLLER_PORT
    );

    /** The container for the robot. */
    public RobotContainer() {
        // Configure the trigger bindings
        this.configureBindings();
    }

    /**
     * Configures Trigger bindings
     */
    private void configureBindings() {
        // Sets the value of the motor when A is pressed down on the driver controller
        this.driverController.a().onTrue(
            this.exampleSubsystem.setExampleMotorGoal(() -> Math.abs(this.driverController.getLeftX()))
        );

        this.driverController.b().onTrue(this.autoSubsystem.exampleCommand1());
    }

    /** 
     * Resets the internal positioning of the robot to the default start position and 
     * redetermines the alliance the robot is on
     */
    public void resetSide() {

    }

    /** Resets the robot PIDs on enable to prevent I value buildup */
    public void resetPIDs() {
        this.exampleSubsystem.resetPIDs();
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An example command will be run in autonomous
        return this.autoSubsystem.getAutoCommand();
    }

    /**
     * Get the command to run when teleop begins, could be used to reset subsystems
     * @return the command that will be run at the start of teleop mode
     */
    public Command getTeleopCommand() {
        return this.autoSubsystem.getTeleopCommand();
    }

    @Override
    public void logTo(TelemetryTable table) {
        table.log("Controller Button", this.driverController.a().getAsBoolean());
    }
}
