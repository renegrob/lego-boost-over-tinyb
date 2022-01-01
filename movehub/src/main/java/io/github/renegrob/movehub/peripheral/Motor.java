package io.github.renegrob.movehub.peripheral;

import io.github.renegrob.movehub.MoveHub;
import io.github.renegrob.movehub.message.PortOutputCommand;

import static io.github.renegrob.movehub.Util.b;
import static io.github.renegrob.movehub.Util.checkRange;

public class Motor extends Peripheral {

    private final int port;

    public enum EndState {
        FLOAT(0),
        HOLD(126),
        BREAK(127);

        private final byte value;

        EndState(int value) {
            this.value = (byte) value;
        }

        public byte value() {
            return value;
        }
    }

    public Motor(MoveHub hub, int port) {
        super(hub);
        this.port = port;
    }

    public void setAccTime(int millis) {
        hub.writeValue(new SetAccTimeCommand(port, millis, 0));
    }

    public void setDecTime(int millis) {
        hub.writeValue(new SetDecTimeCommand(port, millis, 0));
    }

    public void startPower(int power) {
        hub.writeValue(new StartPowerCommand(port, power));
    }

    public void startSpeed(int speed, int maxPower) {
        hub.writeValue(new StartSpeedCommand(port, speed, maxPower, 0));
    }

    public void startSpeedForTime(int millis, int speed, int maxPower, EndState endState) {
        hub.writeValue(new StartSpeedForTimeCommand(port, millis, speed, maxPower, endState, 0));
    }

    public void startSpeedForTime(int millis, int speed, int maxPower, EndState endState, int profile) {
        hub.writeValue(new StartSpeedForTimeCommand(port, millis, speed, maxPower, endState, profile));
    }

    public void startSpeedForDegrees(int degrees, int speed, int maxPower, EndState endState) {
        hub.writeValue(new StartSpeedForDegreesCommand(port, degrees, speed, maxPower, endState, 0));
    }

    public void startSpeedForDegrees(int degrees, int speed, int maxPower, EndState endState, int profile) {
        hub.writeValue(new StartSpeedForDegreesCommand(port, degrees, speed, maxPower, endState, profile));
    }

    public void startSpeedLRForTime(int millis, int speedL, int speedR, int maxPower, EndState endState) {
        hub.writeValue(new StartSpeedLRForTimeCommand(port, millis, speedL, speedR, maxPower, endState, 0));
    }

    public void gotoAbsolutePosition(int absPos, int speed, int maxPower, EndState endState) {
        checkSpeed(speed);
        checkPower(maxPower);
        hub.writeValue(new GotoAbsolutePositionCommand(port, absPos, speed, maxPower, endState, 0));
    }

    private static byte checkPower(int maxPower) {
        checkRange("maxPower", maxPower, 0, 100);
        return (byte) maxPower;
    }

    private static byte checkSpeed(int speed) {
        checkRange("speed", speed, -100, 100);
        return (byte) speed;
    }

    /**
     * Port ID, Startup and Completion Information, 0x51, 0x00, Power
     * Set port into adjustable PWM output using the param Power. No Speed regulation applied.
     */
    static class StartPowerCommand extends PortOutputCommand {
        public StartPowerCommand(int portId, int power) {
            super(portId, false, true, Peripheral.WRITE_DIRECT_MODE_DATA, (byte) 0x00, (byte) power);
        }
    }

    /**
     * If FLOAT or BRAKE should be used e.g. when a slider is set to 0 (zero) the user has to use either StartPower(0 or 127).
     */
    static class StartSpeedCommand extends PortOutputCommand {
        public StartSpeedCommand(int portId, int speed, int maxPower, int profile) {
            super(portId, false, true, b(0x07), checkSpeed(speed), checkPower(maxPower), b(profile));
        }
    }

    static class SetAccTimeCommand extends PortOutputCommand {
        public SetAccTimeCommand(int portId, int millis, int profile) {
            super(portId, false, true, b(0x05), (byte) millis, (byte) (millis >> 8), b(profile));
        }
    }

    static class SetDecTimeCommand extends PortOutputCommand {
        public SetDecTimeCommand(int portId, int millis, int profile) {
            super(portId, false, true, b(0x06), (byte) millis, (byte) (millis >> 8), b(profile));
        }
    }

    static class StartSpeedForTimeCommand extends PortOutputCommand {
        public StartSpeedForTimeCommand(int portId, int millis, int speed, int maxPower, EndState endState, int profile) {
            super(portId, false, true, b(0x09), (byte) millis, (byte) (millis >> 8), checkSpeed(speed), checkPower(maxPower), endState.value(), b(profile));
        }
    }

    static class StartSpeedLRForTimeCommand extends PortOutputCommand {
        public StartSpeedLRForTimeCommand(int portId, int millis, int speedL, int speedR, int maxPower, EndState endState, int profile) {
            super(portId, false, true, b(0x0A), (byte) millis, (byte) (millis >> 8), checkSpeed(speedL), checkSpeed(speedR), checkPower(maxPower), endState.value(), b(profile));
        }
    }

    static class StartSpeedForDegreesCommand extends PortOutputCommand {
        public StartSpeedForDegreesCommand(int portId, int degrees, int speed, int maxPower, EndState endState, int profile) {
            super(portId, false, true, b(0x0D),(byte) degrees, (byte) (degrees >> 8), (byte) (degrees >> 16), (byte) (degrees >> 24), checkSpeed(speed), checkPower(maxPower), endState.value(), b(profile));
        }
    }

    static class GotoAbsolutePositionCommand extends PortOutputCommand {
        public GotoAbsolutePositionCommand(int portId, int absPos, int speed, int maxPower, EndState endState, int profile) {
            super(portId, false, true, b(0x0D),(byte) absPos, (byte) (absPos >> 8), (byte) (absPos >> 16), (byte) (absPos >> 24), checkSpeed(speed), checkPower(maxPower), endState.value(), b(profile));
        }
    }

    static class PresetEncoderCommand extends PortOutputCommand {
        public PresetEncoderCommand(int portId, int position) {
            super(portId, false, true, WRITE_DIRECT_MODE_DATA, (byte) position, (byte) (position >> 8), (byte) (position >> 16), (byte) (position >> 24));
        }
    }
}
