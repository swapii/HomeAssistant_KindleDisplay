package homeassistant.display.kindle;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.util.Timer;
import com.amazon.kindle.kindlet.util.TimerTask;
import homeassistant.display.kindle.ui.SensorPanel;
import homeassistant.display.kindle.ui.ValuePanel;
import ixtab.jailbreak.Jailbreak;
import ixtab.jailbreak.SuicidalKindlet;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeAssistantKindleDisplay extends SuicidalKindlet {

    private Timer timer;

    private PowerDaemon powerDaemon = new PowerDaemon();
    private boolean isScreenSaverWasEnabled;

    private ValuePanel timePanel = new ValuePanel();
    private SensorPanel temperaturePanel = new SensorPanel();
    private SensorPanel humidityPanel = new SensorPanel();
    private SensorPanel airQualityPanel = new SensorPanel();

    private boolean started;

    public void onCreate(KindletContext context) {
        super.onCreate(context);

        GridLayout gridLayout = new GridLayout(0, 1);

        KPanel panel = new KPanel(gridLayout);
        panel.add(timePanel);
        panel.add(temperaturePanel);
        panel.add(humidityPanel);
        panel.add(airQualityPanel);
        context.getRootContainer().add(panel);
    }

    public void onStart() {

        if (started) {
            return;
        }

        super.onStart();

        started = true;

        if (!jailbreak.isAvailable()) {
            throw new IllegalStateException("Jailbreak not available");
        }

        if (!jailbreak.isEnabled()) {
            throw new IllegalStateException("Can't enable ixtab.jailbreak");
        }

        if (!((HomeAssistantKindleDisplayJailbreak) jailbreak).requestPermissions()) {
            throw new IllegalStateException("Can't request all permissions");
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {

                isScreenSaverWasEnabled = powerDaemon.isScreenSaverEnabled();

                if (isScreenSaverWasEnabled) {
                    powerDaemon.disableScreenSaver();
                }

            }
        });

        updateScreenInfo();
        scheduleNextTimerRun();
    }

    public void onStop() {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (isScreenSaverWasEnabled) {
                    powerDaemon.enableScreenSaver();
                }
            }
        });

        if (timer != null) {
            timer.cancel();
        }

        super.onStop();
    }

    protected Jailbreak instantiateJailbreak() {
        return new HomeAssistantKindleDisplayJailbreak();
    }

    private void scheduleNextTimerRun() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                updateScreenInfo();
                scheduleNextTimerRun();
            }
        }, 10000);
    }

    private void updateScreenInfo() {
        updateTime();
        loadDataFromServer();
    }

    private void updateTime() {
        timePanel.showValue("Time", new SimpleDateFormat("HH:mm").format(new Date()));
    }

    private void loadDataFromServer() {
        updateSensor("room_temperature", temperaturePanel);
        updateSensor("room_humidity", humidityPanel);
        updateSensor("room_air_quality", airQualityPanel);
    }

    private void updateSensor(final String sensorName, SensorPanel sensorPanel) {
        SensorPanel.Data data = getSensorData(sensorName, sensorPanel);
        if (data == null) return;
        sensorPanel.showData(data);
    }

    private SensorPanel.Data getSensorData(String sensorName, ValuePanel label) {
        String urlString = "http://192.168.1.21:8123/api/states/sensor." + sensorName;

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        InputStream stream;
        try {
            stream = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Object unknownJson;

        InputStreamReader reader = new InputStreamReader(stream);

        try {
            unknownJson = new JSONParser().parse(reader);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject json = (JSONObject) unknownJson;

        String state = ((String) json.get("state"));

        JSONObject attributes = ((JSONObject) json.get("attributes"));
        String friendlyName = ((String) attributes.get("friendly_name"));
        String unitOfMeasurement = ((String) attributes.get("unit_of_measurement"));

        return new SensorPanel.Data(friendlyName, state, unitOfMeasurement);
    }

}
