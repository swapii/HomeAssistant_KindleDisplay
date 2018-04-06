package homeassistant.display.kindle.ui;

import com.amazon.kindle.kindlet.ui.KBox;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;

import java.awt.BorderLayout;

public class ValuePanel extends KPanel {

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
        add(getContent(), BorderLayout.CENTER);
        add(KBox.createVerticalStrut(30), BorderLayout.NORTH);
        add(KBox.createVerticalStrut(30), BorderLayout.SOUTH);
        add(KBox.createHorizontalStrut(30), BorderLayout.EAST);
        add(KBox.createHorizontalStrut(30), BorderLayout.WEST);
    }

    public void showValue(String name, String value) {

        nameLabel.setText(name);
        nameLabel.repaint();

        valueLabel.setText(value);
        valueLabel.repaint();

    }

    private KPanel getContent() {
        KPanel panel = new KPanel();
        panel.setLayout(new BorderLayout());
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);
        return panel;
    }

}
