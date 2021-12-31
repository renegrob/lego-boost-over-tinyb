package com.github.renegrob.lwp;

import com.github.renegrob.lwp.message.LWPMessage;
import com.github.renegrob.lwp.peripheral.Led;
import com.github.renegrob.lwp.peripheral.Motor;
import com.github.renegrob.lwp.peripheral.Peripheral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tinyb.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.github.renegrob.lwp.Util.toHex;
import static java.util.Objects.requireNonNull;

public class MoveHub implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger();

    public static final String DEVICE_NAME = "Move Hub";

    private static final byte[] LED_LIGHT = { 0x00, 0x08 };

    private final Led led;
    private final Motor motorA;
    private final Motor motorB;
    private final Motor motorAB;

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

        for (BluetoothGattService service : device.getServices()) {
            LOG.info("- " + service.getUUID() + ": " + service.getBluetoothType());
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                LOG.info("  -  " + characteristic.getUUID() + ": " + characteristic.getBluetoothType() + ": " + Arrays.toString(characteristic.getFlags()));
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    LOG.info("    - " + descriptor.getUUID() + ": " + descriptor.getBluetoothType());
                }
            }
        }

        this.legoHubService = device.getServices().stream()
                .filter(bluetoothGattService -> UUID.fromString(bluetoothGattService.getUUID()).equals(LEGO_HUB_SERVICE))
                .findAny()
                .orElseThrow(() -> new IOException("Did not find LEGO_HUB_SERVICE: " + LEGO_HUB_SERVICE));


        this.legoHubCharacteristics =  legoHubService.getCharacteristics().stream()
                .filter(characteristic -> UUID.fromString(characteristic.getUUID()).equals(LEGO_HUB_CHARACTERISTIC))
                .findAny()
                .orElseThrow(() -> new IOException("Did not find LEGO_HUB_CHARACTERISTIC: " + LEGO_HUB_CHARACTERISTIC));

        motorA().setAccTime(2000);
        motorA().setDecTime(2000);
        motorA().startSpeedForDegrees(720, 100, 100, Motor.EndState.FLOAT, 3);
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

    protected void writeValue(byte[] bytes) {
        LOG.info("Sending " + toHex(bytes));
        legoHubCharacteristics.writeValue(bytes);
    }

    public void writeValue(LWPMessage message) {
        legoHubCharacteristics.writeValue(message.toBytes());
    }

}
