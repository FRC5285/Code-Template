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

package first.robot.subsystems;

import java.util.function.Supplier;

import org.wpilib.command2.Command;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.hardware.rotation.DutyCycleEncoder;
import org.wpilib.math.controller.ProfiledPIDController;
import org.wpilib.math.controller.SimpleMotorFeedforward;
import org.wpilib.math.trajectory.TrapezoidProfile;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.telemetry.TelemetryTable;
import org.wpilib.tunable.ComplexTunable;
import org.wpilib.tunable.TunableTable;
import org.wpilib.tunable.Tunables;

import com.ctre.phoenix6.hardware.TalonFX;

import first.robot.Constants.CANBusConstants;
import first.robot.Constants.CANIDConstants;
import first.robot.Constants.DIOConstants;
import first.robot.Constants.ExampleConstants;

public class ExampleSubsystem extends SubsystemBase implements ComplexTunable {

    // Hardware first

    private final TalonFX exampleMotor = new TalonFX(
        CANIDConstants.ID_EXAMPLE_MOTOR,
        CANBusConstants.BUS_EXAMPLE_MOTOR
    );

    private final DutyCycleEncoder exampleEncoder = new DutyCycleEncoder(
        DIOConstants.DIO_EXAMPLE_ENCODER
    );

    // PIDs, Feedforwards

    private final SimpleMotorFeedforward exampleMotorFeedforward = new SimpleMotorFeedforward(
        ExampleConstants.S_EXAMPLE_MOTOR,
        ExampleConstants.V_EXAMPLE_MOTOR
    );

    private final ProfiledPIDController exampleMotorPID = new ProfiledPIDController(
        ExampleConstants.P_EXAMPLE_MOTOR,
        ExampleConstants.I_EXAMPLE_MOTOR,
        ExampleConstants.D_EXAMPLE_MOTOR,
        new TrapezoidProfile.Constraints(
            ExampleConstants.MAX_V_EXAMPLE_MOTOR,
            ExampleConstants.MAX_A_EXAMPLE_MOTOR
        )
    );

    // Other variables

    private double exampleEncoderPos = 0.0;

    private double exampleMotorGoal = ExampleConstants.EXAMPLE_MOTOR_START_GOAL;


    /** Creates a new ExampleSubsystem. */
    public ExampleSubsystem() {

        this.exampleMotorPID.enableContinuousInput(
            ExampleConstants.EXAMPLE_ENCODER_MIN,
            ExampleConstants.EXAMPLE_ENCODER_MAX
        );

        this.exampleMotorPID.setGoal(this.getExampleMotorGoal());

        Tunables.publish("ExampleSubsystem", this);

    }

    @Override
    public void periodic() {

        this.exampleEncoderPos = this.exampleEncoder.get();

        double exampleMotorPIDCalc = this.exampleMotorPID.calculate(
            this.getExampleEncoderPosition(),
            this.getExampleMotorGoal()
        );

        double exampleMotorFFCalc = this.exampleMotorFeedforward.calculate(
            this.exampleMotorPID.getSetpoint().velocity
        );

        this.exampleMotor.setVoltage(exampleMotorFFCalc + exampleMotorPIDCalc); // always use setVoltage

        Telemetry.log("ExampleSubsystem", this);

    }

    public void resetPIDs() {
        this.exampleMotorPID.reset(this.getExampleEncoderPosition());
    }


    /**
     * A command that sets a new goal for exampleMotor
     * 
     * @param newExampleMotorGoal a supplier that provides the new value to set the exampleMotor to
     * @return A command that sets a new goal for the exampleMotor
     */
    public Command setExampleMotorGoal(Supplier<Double> newExampleMotorGoal) {
        return this.runOnce(() -> {
            this.exampleMotorGoal = newExampleMotorGoal.get().doubleValue();
        });
    }

    /**
     * Getter method for the goal for the exampleMotor
     * 
     * @return The goal for the exampleMotor
     */
    public double getExampleMotorGoal() {
        return this.exampleMotorGoal;
    }

    /**
     * Getter method for the position of the encoder
     * 
     * @return The position of the encoder
     */
    public double getExampleEncoderPosition() {
        return this.exampleEncoderPos;
    }

    @Override
    public void logTo(TelemetryTable table) {
        table.log("Example Motor PID", this.exampleMotorPID);
        table.log("Example Motor Goal", this.getExampleMotorGoal());
        table.log("Example Encoder Position", this.getExampleEncoderPosition());
    }

    @Override
    public void publishTunable(TunableTable table) {
        table.publish("Example Motor PID", this.exampleMotorPID);
        table.publishDouble(
            "Example Motor Feedforward kS",
            () -> this.exampleMotorFeedforward.getKs(),
            (newKS) -> this.exampleMotorFeedforward.setKs(newKS)
        );
        table.publishDouble(
            "Example Motor Feedforward kV",
            () -> this.exampleMotorFeedforward.getKv(),
            (newKV) -> this.exampleMotorFeedforward.setKv(newKV)
        );
    }
}
