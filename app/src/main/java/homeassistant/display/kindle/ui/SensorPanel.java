package homeassistant.display.kindle.ui;

import java.text.MessageFormat;

public class SensorPanel extends ValuePanel {

    public void showData(Data data) {
        showValue(data.name, MessageFormat.format("{0} {1}", new Object[]{data.value, data.units}));
    }

    public static class Data {

        String name;
        String value;
        String units;

        public Data(String name, String value, String units) {
            this.name = name;
            this.value = value;
            this.units = units;
        }

    }

}
