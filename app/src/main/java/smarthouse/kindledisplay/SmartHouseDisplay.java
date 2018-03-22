package smarthouse.kindledisplay;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.amazon.kindle.kindlet.ui.KPanel;

import java.awt.BorderLayout;

public class SmartHouseDisplay extends AbstractKindlet {

    public void create(KindletContext context) {
        super.create(context);

        KPanel panel = new KPanel(new BorderLayout(5, 5));
        panel.add(new KLabel("Hello, World!"));

        context.getRootContainer().removeAll();
        context.getRootContainer().add(panel);

    }

}
