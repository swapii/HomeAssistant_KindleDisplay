package homeassistant.display.kindle.ui;

import com.amazon.kindle.kindlet.ui.KLabel;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

class AutoTextSizeMediator {

    private KLabel label;

    AutoTextSizeMediator(KLabel label) {
        this.label = label;
        label.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                adaptLabelFont();
            }
        });
    }

    private void adaptLabelFont() {
        label.setFont(findMaximumFontFitLabel(label));
        label.repaint();
    }

    private Font findMaximumFontFitLabel(KLabel label) {
        return findMaximumFontFitBounds(label.getFont(), label.getBounds(), label.getText());
    }

    private Font findMaximumFontFitBounds(Font font, Rectangle bounds, String text) {
        font = deriveFont(font, 1);
        while (isTextFitBounds(text, font, bounds)) {
            font = deriveFont(font, font.getSize() + 1);
        }
        return deriveFont(font, font.getSize() - 1);
    }

    private Font deriveFont(Font font, int fontSize) {
        return new Font(font.getName(), font.getStyle(), fontSize);
    }

    private boolean isTextFitBounds(String text, Font font, Rectangle bounds) {
        return bounds.contains(getTextBounds(font, text));
    }

    private Rectangle getTextBounds(Font font, String text) {

        KLabel label = new KLabel(text);
        label.setFont(font);

        FontMetrics fontMetrics = label.getFontMetrics(font);

        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();

        return new Rectangle(textWidth, textHeight);
    }

}
