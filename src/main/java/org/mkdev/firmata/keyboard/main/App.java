package org.mkdev.firmata.keyboard.main;

import jssc.SerialPortList;
import org.firmata4j.Example;
import org.firmata4j.IODevice;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.PinEventListener;
import org.firmata4j.firmata.FirmataDevice;
import org.mkdev.firmata.keyboard.ut.Debouncer;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-24
 */
public class App {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("org.mkdev");

    private static final long HEARTBEAT_INITIAL_DELAY = 0l;
    private static final long HEARTBEAT_DELAY = 1000l;

    private static final int HEARTBEAT_PIN = 13; // 13 is Arduino internal led pin

    private static final int DEBOUNCE_DELAY = 250;
    public static final int ARDUINO_FIRST_PIN = 2; // 2 is first multipurpose digital pin

    private Robot robot;
    private IODevice device;
    private Debouncer<String> debouncer;

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    public App() throws AWTException {
        robot = new Robot();
    }

    public void initializeGUI() {
        try { // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, "Cannot load system look and feel.", ex);
        }
    }

    public void init() throws IOException, InterruptedException {
        initializeGUI();
        loadProperties();
        initializeDevice();
    }

    private List<String> getPropertyList(Properties properties, String name) {
        return properties.entrySet().stream().filter(entry -> ((String) entry.getKey()).matches("^" + Pattern.quote(name) + "\\.\\d+$")).map(entry -> (String) entry.getValue()).collect(Collectors.toList());
    }

    private Properties loadProperties() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("config.properties");

        Properties props = new Properties();
        try {
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props;
    }

    private void setupPins(List<String> keystrokes) throws IOException {

        debouncer = new Debouncer<>(DEBOUNCE_DELAY, arg -> {
            int pin = findPinForKeystroke(keystrokes, arg);
            long pinValue =  device.getPin(pin+ ARDUINO_FIRST_PIN).getValue();

            LOGGER.debug("found pin: {} [{}] '{}'", pin);

            if (pinValue == 1) {
                char character = keystrokes.get(pin).charAt(0);

                robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(character));
                robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(character));
            }
        });

        int i = ARDUINO_FIRST_PIN; // first two pins are TX/RX for serial communication only
        for (String keystroke: keystrokes) {
            device.getPin(i).setMode(Pin.Mode.INPUT);

            device.getPin(i).addEventListener(new PinEventListener() {
                @Override
                public void onModeChange(IOEvent ioEvent) {
                    // this space intentionally left blank
                }

                @Override
                public void onValueChange(IOEvent ioEvent) {
                    if (ioEvent.getPin().getValue() == 1) {
                        debouncer.execute(keystroke);
                    }
                }
            });

            i++;
        }
    }

    private int findPinForKeystroke(List<String> keystrokes, String arg) {
        return keystrokes.indexOf(arg);
    }

    public void initHeartbeat() throws IOException {

        device.getPin(HEARTBEAT_PIN).setMode(Pin.Mode.OUTPUT);
        device.getPin(HEARTBEAT_PIN).setValue(1);

        executor.scheduleAtFixedRate(() -> {
            LOGGER.debug("heartbeat");
            try {
                device.getPin(HEARTBEAT_PIN).setValue(1 - device.getPin(HEARTBEAT_PIN).getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, HEARTBEAT_INITIAL_DELAY, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws IOException, InterruptedException, AWTException {

        App app = new App();

        app.init();

        app.setupBehaviour();
        app.initHeartbeat();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executor.shutdown();
            }
        });
    }

    private void setupBehaviour() throws IOException {
        setupPins(getPropertyList(loadProperties(), "button.keystroke"));
    }

    private void initializeDevice() throws IOException, InterruptedException {
        // requesting a user to define the port name
        String port = requestPort();

        this.device = new FirmataDevice(port);

        device.start();
        device.ensureInitializationIsDone();
    }

    @SuppressWarnings("unchecked")
    private static String requestPort() {
        JComboBox<String> portNameSelector = new JComboBox<String>();
        portNameSelector.setModel(new DefaultComboBoxModel<>());
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            portNameSelector.addItem(portName);
        }
        if (portNameSelector.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Cannot find any serial port", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(new JLabel("Port "));
        panel.add(portNameSelector);
        if (JOptionPane.showConfirmDialog(null, panel, "Select the port", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            return portNameSelector.getSelectedItem().toString();
        } else {
            System.exit(0);
        }
        return "";
    }
}