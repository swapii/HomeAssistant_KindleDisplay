package smarthouse.kindledisplay;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.*;
import com.amazon.kindle.kindlet.util.Timer;
import com.amazon.kindle.kindlet.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

public class SmartHouseDisplay extends AbstractKindlet {

    private Timer timer;

    private KLabelMultiline temperatureLabel;
    private KLabelMultiline humidityLabel;
    private KLabelMultiline airQualityLabel;

    public void create(KindletContext context) {
        super.create(context);

        KPanel panel = new KPanel(new BorderLayout(5, 5));
        panel.setLayout(new GridLayout(0, 1));

        temperatureLabel = new KLabelMultiline();
        temperatureLabel.setRows(KTextComponent.SHOW_ALL_LINES);
        panel.add(temperatureLabel);

        humidityLabel = new KLabelMultiline();
        humidityLabel.setRows(KTextComponent.SHOW_ALL_LINES);
        panel.add(humidityLabel);

        airQualityLabel = new KLabelMultiline();
        airQualityLabel.setRows(KTextComponent.SHOW_ALL_LINES);
        panel.add(airQualityLabel);

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
        updateSensor("room_temperature", temperatureLabel);
        updateSensor("room_humidity", humidityLabel);
        updateSensor("room_air_quality", airQualityLabel);
    }

    private void updateSensor(final String sensorName, KLabel label) {

        String urlString = "http://192.168.1.21:8123/api/states/sensor." + sensorName;

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            showText(e.getMessage(), label);
            e.printStackTrace();
            return;
        }

        InputStream stream;
        try {
            stream = url.openStream();
        } catch (IOException e) {
            showText(e.getMessage(), label);
            e.printStackTrace();
            return;
        }

        Object unknownJson;

        InputStreamReader reader = new InputStreamReader(stream);

        try {
            unknownJson = new JSONParser().parse(reader);
        } catch (ParseException e) {
            showText(e.getMessage(), label);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            showText(e.getMessage(), label);
            e.printStackTrace();
            return;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                showText(e.getMessage(), label);
            }
        }

        if (!(unknownJson instanceof JSONObject)) {
            return;
        }

        JSONObject json = (JSONObject) unknownJson;

        String state = ((String) json.get("state"));

        JSONObject attributes = ((JSONObject) json.get("attributes"));
        String friendlyName = ((String) attributes.get("friendly_name"));
        String unitOfMeasurement = ((String) attributes.get("unit_of_measurement"));

        String text = MessageFormat.format("{0}: {1} {2}", new Object[]{friendlyName, state, unitOfMeasurement});

        showText(text, label);
    }

    private void showText(String text, KLabel label) {
        label.setText(text);
        label.repaint();
    }

}
