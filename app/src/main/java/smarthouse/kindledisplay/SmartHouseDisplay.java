package smarthouse.kindledisplay;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.util.Timer;
import com.amazon.kindle.kindlet.util.TimerTask;

import java.awt.BorderLayout;

public class SmartHouseDisplay extends AbstractKindlet {

    private Timer timer;
    private KLabel label;
    private int counter;

    public void create(KindletContext context) {
        super.create(context);

        label = new KLabel("Hello, World!");

        KPanel panel = new KPanel(new BorderLayout(5, 5));
        panel.add(label);

        context.getRootContainer().add(panel);
    }

    public void start() {
        super.start();
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
                counter++;
                label.setText(String.valueOf(counter));
                label.repaint();
                scheduleNextTimerRun();
            }
        }, 3000);
    }

}
