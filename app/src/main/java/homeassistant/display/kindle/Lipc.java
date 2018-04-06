package homeassistant.display.kindle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class Lipc {

    public String getProperty(String publisher, String property) {

        String command = MessageFormat.format("lipc-get-prop {0} {1}", new Object[]{publisher, property});

        Process process;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        StringBuffer buffer = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return buffer.toString();
    }

    public void setProperty(String publisher, String property, String value) {

        String command = MessageFormat.format("lipc-set-prop {0} {1} {2}", new Object[]{publisher, property, value});

        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
