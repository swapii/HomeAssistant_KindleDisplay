package homeassistant.display.kindle;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.util.Timer;
import com.amazon.kindle.kindlet.util.TimerTask;
import homeassistant.display.kindle.ui.SensorPanel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HomeAssistantKindleDisplay extends AbstractKindlet {

    private Timer timer;

    private SensorPanel temperaturePanel = new SensorPanel();
    private SensorPanel humidityPanel = new SensorPanel();
    private SensorPanel airQualityPanel = new SensorPanel();

    public void create(KindletContext context) {
        super.create(context);

        GridLayout gridLayout = new GridLayout(0, 1);

        KPanel panel = new KPanel(gridLayout);
        panel.add(temperaturePanel);
        panel.add(humidityPanel);
        panel.add(airQualityPanel);
        context.getRootContainer().add(panel);
    }

    public void start() {
        super.start();
        loadDataFromServer();
        scheduleNextTimerRun();
    }

    public void stop() {
        super.stop();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void scheduleNextTimerRun() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                loadDataFromServer();
                scheduleNextTimerRun();
            }
        }, 10000);
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

    private SensorPanel.Data getSensorData(String sensorName, SensorPanel label) {
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
