package io.github.renegrob.movehub.demo;

import com.formdev.flatlaf.FlatDarculaLaf;
import io.github.renegrob.movehub.MoveHub;
import io.github.renegrob.movehub.peripheral.Led;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import tinyb.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class MyApp implements Runnable {

    private static final Logger LOG = LogManager.getLogger();

    private BluetoothManager bluetoothManager;
    private MoveHub moveHub;
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MyApp());
    }

    public void run() {
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
        // Invoked on the event dispatching thread.
        // Construct and show GUI.
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            LOG.error("Failed to initialize LaF", ex);
        }

        frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);


        Panel content = new Panel();
        frame.getContentPane().add(content);

        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");

        // https://docs.oracle.com/javase/tutorial/uiswing/layout/groupExample.html
        GroupLayout layout = new GroupLayout(content);
        content.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(button1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.DEFAULT_SIZE, 20)
                        .addComponent(button2)

//                        .addComponent(button1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
//                                GroupLayout.PREFERRED_SIZE)
//                        .addComponent(button2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
//                                GroupLayout.PREFERRED_SIZE)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
//                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                                .addComponent(c3)
//                                .addComponent(c4))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addComponent(button1)
                        .addComponent(button2)
        );
        frame.pack();
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> lazyInit());
    }

    public void lazyInit() {
        bluetoothManager = BluetoothManager.getBluetoothManager();
        discoverDevices();
    }

    public void discoverDevices() {
        bluetoothManager.startDiscovery();
        List<BluetoothDevice> devices = bluetoothManager.getDevices();
        for (BluetoothDevice device : devices) {
            LOG.info(device.getAddress() + ", name: " + device.getName() + ", alias: " + device.getAlias());
            LOG.info("  service data: " + device.getServiceData());
            for (BluetoothGattService service : device.getServices()) {
                LOG.info("- " + service.getUUID() + ": " + service.getBluetoothType());
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    LOG.info("  -  " + characteristic.getUUID() + ": " + characteristic.getBluetoothType());
                    for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                        LOG.info("    - " + descriptor.getUUID() + ": " + descriptor.getBluetoothType());
                    }
                }
            }
            if (Objects.equals(device.getName(), MoveHub.DEVICE_NAME)) {
                moveHub = new MoveHub(device);
            }
        }
        LOG.info("--------------------------");
        if (moveHub != null) {
            try {
                moveHub.connect();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    moveHub.led().setColor(Led.Color.RED);
                    moveHub.close();
                }));
                moveHub.led().setColor(Led.Color.GREEN);
            } catch (Exception e) {
                LOG.error(e, e);
                JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}