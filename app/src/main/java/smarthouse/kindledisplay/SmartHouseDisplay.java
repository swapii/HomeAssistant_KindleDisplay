package smarthouse.kindledisplay;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextComponent;
import com.amazon.kindle.kindlet.util.Timer;
import com.amazon.kindle.kindlet.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

public class SmartHouseDisplay extends AbstractKindlet {

    private Timer timer;
    private KLabelMultiline label;

    public void create(KindletContext context) {
        super.create(context);

        label = new KLabelMultiline("Hello, World!");
        label.setRows(KTextComponent.SHOW_ALL_LINES);

        KPanel panel = new KPanel(new BorderLayout(5, 5));
        panel.add(label);

        context.getRootContainer().add(panel);
    }

    public void start() {
        super.start();
        updateWeather();
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
                updateWeather();
                scheduleNextTimerRun();
            }
        }, 10000);
    }

    private void updateWeather() {

        String urlString = "http://api.apixu.com/v1/current.json?key=824ae92000b44a7aafe233320182203&q=Moscow";

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            showText(e.getMessage());
            e.printStackTrace();
            return;
        }

        InputStream stream;
        try {
            stream = url.openStream();
        } catch (IOException e) {
            showText(e.getMessage());
            e.printStackTrace();
            return;
        }

        Object unknownJson;

        InputStreamReader reader = new InputStreamReader(stream);

        try {
            unknownJson = new JSONParser().parse(reader);
        } catch (ParseException e) {
            showText(e.getMessage());
            e.printStackTrace();
            return;
        } catch (IOException e) {
            showText(e.getMessage());
            e.printStackTrace();
            return;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                showText(e.getMessage());
            }
        }

        if (!(unknownJson instanceof JSONObject)) {
            return;
        }

        JSONObject json = (JSONObject) unknownJson;

        JSONObject location = ((JSONObject) json.get("location"));
        String city = ((String) location.get("name"));
        String country = ((String) location.get("country"));

        JSONObject current = ((JSONObject) json.get("current"));
        String temperature = ((Double) current.get("temp_c")).toString();
        String condition = ((JSONObject) current.get("condition")).get("text").toString();

        String text = MessageFormat.format("{0}, {1}: {2} {3}", new Object[]{country, city, temperature, condition});

        showText(text);
    }

    private void showText(String text) {
        label.setText(text);
        label.repaint();
    }

}
