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

import org.wpilib.hardware.bus.CANPort;

import com.ctre.phoenix6.CANBus;

/**
 * The constants for the robot
 */
public final class Constants {

    /**
     * The CAN ID of each component
     */
    public static class CANIDConstants {

        /** The CAN ID of exampleMotor from ExampleSubsystem */
        public static final int ID_EXAMPLE_MOTOR = 1;

    }

    /**
     * What CAN bus each component is on
     * <br><br>
     * CAN bus numbers go from 0 to 4 (inclusive)
     * <br><br>
     * CAN bus 2 is reserved for drivetrain (it is the only CAN bus on its own SPI port, making it more reliable)
     */
    public static class CANBusConstants {

        /** CAN bus 0 */
        public static final CANBus CAN_BUS_0 = new CANBus(CANPort.CAN_S0);

        /** CAN bus 1 */
        public static final CANBus CAN_BUS_1 = new CANBus(CANPort.CAN_S1);

        // CAN bus 2 is reserved for the drivetrain

        /** CAN bus 3 */
        public static final CANBus CAN_BUS_3 = new CANBus(CANPort.CAN_S3);

        /** CAN bus 4 */
        public static final CANBus CAN_BUS_4 = new CANBus(CANPort.CAN_S4);

        /** CAN bus for exampleMotor in ExampleSubsystem */
        public static final CANBus BUS_EXAMPLE_MOTOR = CANBusConstants.CAN_BUS_3;

    }

    /**
     * All the DIO devices of the robot and the ports they connect to
     * <br><br>
     * There are 6 Smart DIO ports, numbered 0-5 (inclusive)
     */
    public static class DIOConstants {

        /** Example subsystem encoder */
        public static final int DIO_EXAMPLE_ENCODER = 0;

    }

    /**
     * Driver control constants, tuned to the driver's preference
     */
    public static class OperatorConstants {

        /** The driver controller port */ // ALWAYS put a documentation thingy above constants, classes, and methods
        public static final int DRIVER_CONTROLLER_PORT = 0;

    }

    /**
     * Constants for autonomous mode
     */
    public static class AutoConstants{

        /** Position 1 for auton */
        public static final double POS_1 = 0.1;

        /** Position 2 for auton */
        public static final double POS_2 = 0.2;

    }

    /**
     * Constants for the ExampleSubsystem
     */
    public static class ExampleConstants {

        /** Feedforward S (voltage needed to overcome friction) for exampleMotor, configure first */
        public static final double S_EXAMPLE_MOTOR = 0.5;

        /** Feedforward V (voltage needed per unit of speed) for exampleMotor, configure second */
        public static final double V_EXAMPLE_MOTOR = 0.5;

        /** Max velocity of exampleMotor */
        public static final double MAX_V_EXAMPLE_MOTOR = 0.5;

        /** Max acceleration of exampleMotor */
        public static final double MAX_A_EXAMPLE_MOTOR = 0.5;

        /** P value of exampleMotor, configure third */
        public static final double P_EXAMPLE_MOTOR = 5.0; // ALWAYS double, ALWAYS put .0 after the number

        /** I value of exampleMotor (keep this at a low number), configure last */
        public static final double I_EXAMPLE_MOTOR = 0.0;

        /** D value of exampleMotor, configure fourth */
        public static final double D_EXAMPLE_MOTOR = 0.5;

        /** Minimum encoder value */
        public static final double EXAMPLE_ENCODER_MIN = 0.0;

        /** Maximum encoder value, wraps around back to EXAMPLE_ENCODER_MIN */
        public static final double EXAMPLE_ENCODER_MAX = 1.0;

        /** The goal position of exampleMotor when the robot is powered on */
        public static final double EXAMPLE_MOTOR_START_GOAL = 0.0;

    }

}
