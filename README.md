# Code-Template

Template and guide for FRC 5285 robot code. This should be followed for all code not written during testing or the competition.

# Guide

## Comments

ALWAYS put a documentation comment thingy above methods, classes, and constants (unless the method has an `@Override` above it)

```Java
/**
 * The CAN ID of each component
 */
public static class CANIDConstants {

    /** The CAN ID of exampleMotor from ExampleSubsystem */
    public static final int ID_EXAMPLE_MOTOR = 1;

}
```

See [Commands and Suppliers](#commands-and-suppliers) for another example.

## Variables

All variables in a non-constants class must be `private` and accessed externally only through getter and setter methods.

If there are no getter or setter methods for a variable (it is only referenced inside its class), `this.[variable]` must be used to refer to the variable.

Long constructor methods (that run off the page) should be split into various lines (see [Methods](#methods) section for example).

### Constants

If a variable is a primitive (or never has its private INTERNAL data modified through the code) it should be kept in the subsystem's constants class in the `Constants.java` file.

Constants should be in `SCREAMING_SNAKE_CASE` and provide sufficient information about what it does (so no one messes up when using autocomplete).

Constants should be defined with `public static final`.

### RobotContainer Variables

Subsystems and driver controllers should be defined with `private final` in the beginning of RobotContainer.

Variables defined in RobotContainer should be in `lowerCamelCase`.

The drivetrain subsystem should be defined first, followed by all other subsystems, then the class for autonomous routines, and finally the controller objects.

If the robot needs to do math, the class for math should be defined at the top of RobotContainer (before all other variables) and take in suppliers for subsystem variables through a method that is called in the RobotContainer constructor (see 2026 RobotContainer code).

### Subsystem Variables

Variables that are hardware (or other objects that are never replaced with other objects in the code) should be defined with `private final`.

Variables defined in a subsystem should be in `lowerCamelCase`.

Hardware (motors, encoders, other sensors, etc.) always goes first in a subsystem, then PIDs, then other variables.

## Methods

When calling methods, they should be split into multiple lines if one line runs off the page:

```Java
private final ProfiledPIDController exampleMotorPID = new ProfiledPIDController(
    ExampleConstants.P_EXAMPLE_MOTOR,
    ExampleConstants.I_EXAMPLE_MOTOR,
    ExampleConstants.D_EXAMPLE_MOTOR,
    new TrapezoidProfile.Constraints(
        ExampleConstants.MAX_V_EXAMPLE_MOTOR,
        ExampleConstants.MAX_A_EXAMPLE_MOTOR
    )
);
```

Additionally, when a method is called in the same class where it is declared, `this.method()` should be used instead of `method()`.

When creating methods, overridden methods such as `periodic()`, `logTo(TelemetryTable table)`, and `publishTunable(TunableTable table)` should include `@Override` in the line above.

## Telemetry

### Subsystems

`SubsystemBase` already implements `TelemetryLoggable`, so they should be declared like the following:

```Java
public class ExampleSubsystem extends SubsystemBase implements ComplexTunable {}
```

At the bottom of the class, two methods should be included:

```Java
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
```

`logTo()` should include all telemetry and logged values, while `publishTunable()` should include all values that can be changed when the robot is running.

Only PID, feedforwards, etc. should be configured through Tunables. PID goals should only be modified through RobotContainer with a controller input, and never through Tunables. Tunables should only be changed when the robot is disabled. The operator should be prepared to e-stop a robot whenever it is enabled following an update to PID or other values through Tunables.

At the bottom of `periodic()`:

```Java
Telemetry.log("ExampleSubsystem", this);
```

The above code updates the robot telemetry values.

At the end of the constructor:

```Java
Tunables.publish("ExampleSubsystem", this);
```

The above code places the Tunable values onto the driver dashboard.

### RobotContainer

RobotContainer should implement `TelemetryLoggable`. Any values not published through its own subsystem class should be published through the RobotContainer `logTo()` method.

RobotContainer should be logged with `Telemetry.log("Robot", this.robotContainer);` under the `robotPeriodic()` method in `Robot.java`.

### Autonomous Selector

To create an auton chooser:

```Java
private final Selectable<Supplier<Command>> autonChooser = new Selectable<>();
```

Autonomous selectors should not be placed under any Tunable tables. Instead, they should be published in the autonomous class constructor:

```Java
this.autonChooser.add("Auton 1", () -> this.goToPosition1());
this.autonChooser.add("Auton 2", () -> this.goToPosition2());

this.autonChooser.setDefault("Auton 1");

Tunables.publish("Auton Picker", this.autonChooser);
```

Autonomous selectors should typically return suppliers for the autonomous command or suppliers for portions of the autonomous commmand, which are combined together under `getAutoCommand()` in `AutoSubsystem.java`. For why autonomous choosers should use suppliers, see [Command Factories and Suppliers](#command-factories-and-suppliers) (some commands in autons might not use suppliers as inputs, so a supplier has to wrap the whole command to make it work).

## Commands

Always use `this.runOnce()`, never use `this.run()` within a subsystem unless absolutely necessary.

### Command Factories and Suppliers

A supplier is created like the following example:
```Java
Supplier<Double> a = () -> {
    return Math.random();
};
// OR
Supplier<Double> b = () -> Math.random();
```

Whenever `a.get()` or `b.get()` is run, the methods return what the code from within the supplier returns. In this specific example, the suppliers would return whatever the value of `Math.random()` is. Every time the supplier is run, it reruns the code to get the value again. How is that useful?

Take this snippet of code:

```Java
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
```

It sets the PID goal of `exampleMotor` to the value of a supplier. But why a supplier?

If the method takes in a `double`, the returned `Command` object sets `exampleMotorGoal` to that `double` EVERY SINGLE TIME IT IS RUN. That might not be an issue for some uses, but not for something like the example below (found in `RobotContainer.java` under `configureBindings()`):

```Java
this.driverController.a().onTrue(
    this.exampleSubsystem.setExampleMotorGoal(() -> Math.abs(this.driverController.getLeftX()))
);
```

The above snippet of code runs a command when the "a" button on the driver's controller is pressed down. When the command is run, it sets the PID goal of `exampleMotor` to `Math.abs(this.driverController.getLeftX())`.

So with the supplier, every time the driver presses the "a" button, it checks the value of `Math.abs(this.driverController.getLeftX())` and sets `exampleMotorGoal` to whatever that value is.

But if the method just took in the `double` without a supplier wrapper? It gets the value of `Math.abs(this.driverController.getLeftX())` and sets `exampleMotorGoal` to that ONE VALUE for the *entirety of the time the robot is on*. Essentially, if `Math.abs(this.driverController.getLeftX())` is `0.0` when the robot is booted up, `exampleMotorGoal` will be set to `0.0` EVERY SINGLE TIME the driver hits the "a" button. If `Math.abs(this.driverController.getLeftX())` ever changes (which it probably will), `exampleMotorGoal` will STILL get set to `0.0` instead of whatever new value `Math.abs(this.driverController.getLeftX())` returns.

That's why most `Command` factories (such as the example at the beginning of this section) should use a supplier to take in values instead of a normal object.

### Combining Commands and Autonomous

Suppose that `command1` and `command2` are both `Command`s. It is possible to create a `command3` that is a combination of these two commands through two methods.

Method 1 (creates a `Command` that runs `command1`, then `command2` AFTER `command1` is finished):

```Java
command3 = command1.andThen(command2);
```

Method 2 (creates a `Command` that runs `command1` and `command2` AT THE SAME TIME):

```Java
command3 = command1.alongWith(command2)
```

Note that when using `alongWith()`, `command1` and `command2` cannot be from the same subsystem, as subsystems can only run one `Command` at a time.

`Command`s can further be extended:

```Java
command6 = command1
.andThen(command2)
.andThen(command3)
.alongWith(
    command4
    .andThen(command5)
)
;
```

As seen above, if a `Command` is combined with more `Command`s, place each `Command` on a separate line.

The above `Command` does two things. `command1`, `command2`, and `command3` run in succession. At the same time, `command4` and `command5` are run in succession.

The reason this happens is because `command1.andThen(command2)` creates a new `Command` that runs `command1` and `command2` in succession. `.andThen(command3)` creates another `Command` that runs that new `Command` (`command1` followed by `command2`), and then `command3`. `.alongwith()` is running the `Command` placed in its input (`command4` followed by `command5`) *along with* that previously created `Command` (`command1` then `command2`, then `command3`) at the same time.

However, if any command in `command1`, `command2`, and `command3` affect the same subsystem as `command4` or `command5`, there will be a runtime error as two `Command`s from the same subsystem could not be run at the same time.

There are more `Command` modifiers, such as `.onlyIf(BooleanSupplier condition)`, `.onlyWhile(BooleanSupplier condition)`, `.repeatedly()`, `.unless(BooleanSupplier condition)`, `.until(BooleanSupplier condition)`, `.withTimeout(double seconds)`. `WaitCommand`s and `WaitUntilCommand`s are also useful types of `Command`s. For how to use these, search the WPILib Java API Docs.

## `Robot.java`

PIDs are reset through RobotContainer at robot enable.

Sometimes, the odometry and robot position needs to be reset. However, depending on what order different modes are used in, the robot position needs to be reset at different times:

Using autonomous (in an actual game):

> autonomous enable -> set robot position to autonomous start position (done within autonomous command) -> run rest of autonomous -> disable (don't reset robot position) -> teleoperated enable -> disable

Not using autonomous (testing):

> reset robot position to default -> teleoperated enable -> disable

Therefore, the robot position reset works as follows:

- autonomous enable flips variable `usedAuton` to `true`
- autonomous enable always resets robot position
- teleoperated enable flips variable `usedTeleop` to `true`
- teleoperated enable only resets robot position if `usedAuton` is false (autonomous was not used)
- upon disable, both variables are flipped to `false` only if `usedTeleop` is `true` (so that everything resets ONLY if teleoperated was used, and nothing gets reset in the period of disable between autonomous disable and teleoperated enable in an actual match)

## Hardware

### Motors

For a Kraken motor:

```Java
private final TalonFX exampleMotor = new TalonFX(
    CANIDConstants.ID_EXAMPLE_MOTOR,
    CANBusConstants.BUS_EXAMPLE_MOTOR
);
```

ALWAYS use `.setVoltage()` to control motors. `.setVoltage()` always powers the motor with the same voltage, regardless of the robot's voltage, which under certain conditions, can change between 6 and 13 volts in under a second. Additionally, `.setVoltage()` should appear exactly ONCE for each motor, located in the `periodic()` method of a subsystem. This is to prevent multiple `.setVoltage()`'s from potentially messing with each other.

Krakens also contain internal encoders. To get the number of rotations the motor has turned since it was powered:

```Java
this.exampleMotor.getPosition().getValueAsDouble();
```

The motor can also return its speed:

```Java
this.exampleMotor.getVelocity().getValueAsDouble();
```

### Encoders

5285 uses the REV Through Bore Encoder (both V1 and V2) for non-drivetrain subsystems.

For an absolute encoder (white wire into DIO):

```Java
private final DutyCycleEncoder exampleEncoder = new DutyCycleEncoder(
    DIOConstants.DIO_EXAMPLE_ENCODER
);
```

Absolute encoders return rotations between `0.0` and `1.0`. Additionally, absolute encoders know their position when the robot is started, so there is no need to begin with components at specific positions. It is also possible to specify an absolute encoder's offset and range in the constructor.

For a quadrature encoder (blue wire goes to `channelA`, yellow wire goes to `channelB`):

```Java
// This example was modified from code for our 2025 robot
private final Encoder elevatorEncoder = new Encoder(ElevatorConstants.encoderA, ElevatorConstants.encoderB);
```

Quadrature encoders give information about *how many* rotations there has been since the robot was turned on. The position of the encoder defaults to `0.0` when the robot is turned on.

### Digital Input

For a digital input (limit switch, color sensor, etc.):

```Java
private final DigitalInput digitalIn = new DigitalInput(DIOConstants.DIO_NUMBER);
```

## Other

### Indentation

Indentation should be 4 spaces, files with indentation of 2 spaces should be changed into 4 spaces.

### `int`s and `double`s
Only use the primitive version (`int` instead of `Integer`) unless absolutely necessary.

Only use `int` for things that MUST be ints (CAN IDs, amounts of components on a robot).

Use `double` for everything else. If a `double` is a whole number, still put a `.0` behind it (`double n = 5.0;` instead of `double n = 5;`).

### PIDs and Feedforwards

If a PID is needed, always use a combination of an appropriate feedforward with a `ProfiledPIDController`.

All subsystems should contain a `resetPIDs()` method to reset the PIDs to revent I value buildup. `RobotContainer.java` should contain a `resetPIDs()` method that runs the `resetPIDs()` methods of all subsystems. This is then run in `Robot.java` each time the robot is enabled.

Note that the units PIDs return are always in volts, but the input units can be rotations, velocity, angular velocity, distance, etc. Be aware of this when calibrating PIDs.

`SimpleMotorFeedforward`s and `ProfiledPIDController`s should be calibrated in the following order:

1. Max velocity and acceleration
2. Feedforward `kS` (voltage needed to barely overcome friction)
3. Feedforward `kV` (voltage needed per unit of velocity)
4. PID `kP`
5. PID `kD`
6. PID `kI` (If this value is not 0.0, check that the PID is reset every time the robot is enabled, otherwise the PID will return crazy values after enabling)

Generally, the feedforward should be calibrated before the PID. Check the WPILib docs for information on calibrating other types of feedforwards. The `kA` value for a feedforward can *usually* stay at 0.0.

If the PID/Feedforward is controlling the rotation angle (not velocity) of a component, `.enableContinuousInput()` should be applied to the PID in the subsystem constructor.