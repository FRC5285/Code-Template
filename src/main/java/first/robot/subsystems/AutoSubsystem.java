// FRC Team 5285 Code Template
// Copyright (C) 2026 FRC Team 5285

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

package first.robot.subsystems;

import java.util.function.Supplier;

import org.wpilib.command2.Command;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.telemetry.TelemetryTable;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;

import first.robot.Constants.AutoConstants;

public class AutoSubsystem {

    private final RobotState robotState = new RobotState();

    // Robot subsystems

    private final ExampleSubsystem exampleSubsystem;

    // Auton selectors

    private final Selectable<Supplier<Command>> autonChooser = new Selectable<>();

    /**
     * Initializes the AutonSubsystem, handles autons and sequences of controls
     * 
     * @param exampleSubsystem instance of exampleSubsystem from RobotContainer
     */
    public AutoSubsystem(ExampleSubsystem exampleSubsystem) {

        this.exampleSubsystem = exampleSubsystem;

        this.autonChooser.add("Auton 1", () -> this.goToPosition1());
        this.autonChooser.add("Auton 2", () -> this.goToPosition2());

        this.autonChooser.setDefault("Auton 1");

        Tunables.publish("Auton Picker", this.autonChooser);
    }

    public Command getAutoCommand() {
        return this.robotState.startAuto()
        .andThen(this.autonChooser.getSelected().get())
        ;
    }

    public Command getTeleopCommand() {
        return this.robotState.endAuto()
        .andThen(this.goToPosition1())
        ;
    }

    public Command goToPosition1() {
        return this.exampleSubsystem.setExampleMotorGoal(() -> AutoConstants.POS_1);
    }

    public Command goToPosition2() {
        return this.exampleSubsystem.setExampleMotorGoal(() -> AutoConstants.POS_2);
    }

    public Command exampleCommand1() {
        return this.goToPosition1()
        .onlyIf(() -> !this.robotState.isInAuto())
        ;
    }


    /**
     * Contains all commands that hold logic/variables for AutoSubsystem
     */
    private class RobotState extends SubsystemBase {

        private boolean inAuto = false;

        public RobotState() {

        }

        @Override
        public void periodic() {
            Telemetry.log("RobotState", this);
        }

        public boolean isInAuto() {
            return this.inAuto;
        }

        public Command startAuto() {
            return this.runOnce(() -> this.inAuto = true);
        }

        public Command endAuto() {
            return this.runOnce(() -> this.inAuto = false);
        }

        @Override
        public void logTo(TelemetryTable table) {
            table.log("Auton command active?", this.isInAuto());
        }
    }
}
