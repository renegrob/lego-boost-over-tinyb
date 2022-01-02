package io.github.renegrob.movehub;

import io.github.renegrob.movehub.message.*;
import io.github.renegrob.movehub.message.event.HubAttachedIOEvent;
import io.github.renegrob.movehub.message.event.PortValueSingle;
import io.github.renegrob.movehub.peripheral.Led;
import io.github.renegrob.movehub.peripheral.Motor;
import io.github.renegrob.movehub.peripheral.Peripheral;
import io.github.renegrob.movehub.peripheral.event.PeripheralAttachedEvent;
import io.github.renegrob.movehub.peripheral.event.PeripheralDetachedEvent;
import io.github.renegrob.movehub.peripheral.event.PeripheralEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tinyb.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static io.github.renegrob.movehub.Util.toHex;
import static io.github.renegrob.movehub.message.IOTypeID.EXTERNAL_MOTOR_WITH_TACHO;
import static io.github.renegrob.movehub.message.MessageType.HUB_ATTACHED_IO;
import static io.github.renegrob.movehub.message.MessageType.PORT_VALUE_SINGLE;
import static java.util.Objects.requireNonNull;

public class MoveHub implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger();

    public static final String DEVICE_NAME = "Move Hub";

    private static final byte[] LED_LIGHT = { 0x00, 0x08 };

    private final Led led;
    private final Motor motorA;
    private final Motor motorB;
    private final Motor motorAB;
    private volatile Peripheral portC;
    private volatile Peripheral portD;

    // LWP3_HUB_SERVICE_UUID
    private static final UUID LEGO_HUB_SERVICE = UUID.fromString("00001623-1212-EFDE-1623-785FEABCD123");
    // LWP3_HUB_CHARACTERISTIC_UUID
    private static final UUID LEGO_HUB_CHARACTERISTIC = UUID.fromString("00001624-1212-EFDE-1623-785FEABCD123");

    private final BluetoothDevice device;
    private BluetoothGattService legoHubService;
    private BluetoothGattCharacteristic legoHubCharacteristics;

    public MoveHub(BluetoothDevice device) {
        this.device = requireNonNull(device);
        this.led = new Led(this);
        this.motorA = new Motor(this, Peripheral.PORT_A);
        this.motorB = new Motor(this, Peripheral.PORT_B);
        this.motorAB = new Motor(this, Peripheral.PORT_AB);
    }

    public void connect() throws IOException {
        LOG.info("Connecting to: " + toString(device));
        if (!device.connect()) {
            throw new IOException("Could not connect to " + toString(device));
        }

        dumpDebugInfo();

        this.legoHubService = device.getServices().stream()
                .filter(bluetoothGattService -> UUID.fromString(bluetoothGattService.getUUID()).equals(LEGO_HUB_SERVICE))
                .findAny()
                .orElseThrow(() -> new IOException("Did not find LEGO_HUB_SERVICE: " + LEGO_HUB_SERVICE));

        this.legoHubCharacteristics =  legoHubService.getCharacteristics().stream()
                .filter(characteristic -> UUID.fromString(characteristic.getUUID()).equals(LEGO_HUB_CHARACTERISTIC))
                .findAny()
                .orElseThrow(() -> new IOException("Did not find LEGO_HUB_CHARACTERISTIC: " + LEGO_HUB_CHARACTERISTIC));


        legoHubCharacteristics.enableValueNotifications(this::onNotification);
    }

    private void dumpDebugInfo() {
        for (BluetoothGattService service : device.getServices()) {
            LOG.debug("- " + service.getUUID() + ": " + service.getBluetoothType());
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                LOG.debug("  -  " + characteristic.getUUID() + ": " + characteristic.getBluetoothType() + ": " + Arrays.toString(characteristic.getFlags()));
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    LOG.debug("    - " + descriptor.getUUID() + ": " + descriptor.getBluetoothType());
                }
            }
        }
    }

    private void onNotification(byte[] bytes) {
        LOG.info("onNotification {}", toHex(bytes));
        LWPUpstreamMessage message = LWPUpstreamMessage.parseMessage(bytes);

        if (message.messageType() != null) {
            LOG.info("{}: {}", message.messageType().name(), toHex(Arrays.copyOfRange(bytes, 3, bytes.length)));
        }
        if (message.messageType() == HUB_ATTACHED_IO) {
            HubAttachedIOEvent msg = (HubAttachedIOEvent) message;
            if (msg.event() == IOEvent.DETACHED) {
                PeripheralDetachedEvent event = new PeripheralDetachedEvent(msg.port());
                if (msg.port() == Port.C) {
                    portC.event(event);
                    portC = null;
                }
                if (msg.port() == Port.D) {
                    portD.event(event);
                    portD = null;
                }
                event(event);
            }
            if (msg.event() == IOEvent.ATTACHED) {
                Peripheral newPeripheral = null;
                if (msg.type() == EXTERNAL_MOTOR_WITH_TACHO) {
                    newPeripheral = new Motor(this, msg.rawPort());
                } else {
                    LOG.warn("Unhandled peripheral: " + msg.type());
                }
                if (msg.port() == Port.C) {
                    portC = newPeripheral;
                }
                if (msg.port() == Port.D) {
                    portD = newPeripheral;
                }
                event(new PeripheralAttachedEvent(msg.port(), msg.type(), newPeripheral));
            }
            LOG.info("Event: {}", msg);
        }
        if (message.messageType() == PORT_VALUE_SINGLE) {
            PortValueSingle msg = (PortValueSingle) message;
            LOG.info("Value: {}", msg);
        }
    }

    private void event(PeripheralEvent peripheralEvent) {
    }

    private static String toString(BluetoothDevice device) {
        return device.getAddress() + " - " + device.getName();
    }

    @Override
    public void close() throws BluetoothException {
        LOG.info("Disconnecting from: " + toString(device));
        device.disconnect();
    }

    public Led led() {
        return led;
    }

    public Motor motorA() {
        return motorA;
    }

    public Motor motorB() {
        return motorB;
    }

    public Motor motorAB() {
        return motorAB;
    }

    public Peripheral portC() {
        return portC;
    }

    public Peripheral portD() {
        return portD;
    }

    protected void writeValue(byte[] bytes) {
        LOG.info("Sending " + toHex(bytes));
        legoHubCharacteristics.writeValue(bytes);
    }

    public void writeValue(LWPDownstreamMessage message) {
        legoHubCharacteristics.writeValue(message.toBytes());
    }


}
