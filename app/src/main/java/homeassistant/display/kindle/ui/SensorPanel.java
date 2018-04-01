package homeassistant.display.kindle.ui;

import com.amazon.kindle.kindlet.ui.KBox;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;

import java.awt.BorderLayout;
import java.text.MessageFormat;

public class SensorPanel extends KPanel {

    private KLabel valueLabel = new KLabel();
    private KLabel nameLabel = new KLabel();

    {
        valueLabel.setHorizontalAlignment(KLabel.CENTER);
        new AutoTextSizeMediator(valueLabel);
    }

    {
        nameLabel.setHorizontalAlignment(KLabel.CENTER);
    }

    {
        setLayout(new BorderLayout());
        add(KBox.createVerticalStrut(30), BorderLayout.NORTH);
        add(getContent(), BorderLayout.CENTER);
        add(KBox.createVerticalStrut(30), BorderLayout.SOUTH);
    }

    private KPanel getContent() {
        KPanel panel = new KPanel();
        panel.setLayout(new BorderLayout());
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);
        return panel;
    }

    public void showData(Data data) {
        showValue(data.name, data.value, data.units);
    }

    private void showValue(String name, String value, String units) {

        nameLabel.setText(name);
        nameLabel.repaint();

        String valueText = MessageFormat.format("{0} {1}", new Object[]{value, units});
        valueLabel.setText(valueText);
        valueLabel.repaint();

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
