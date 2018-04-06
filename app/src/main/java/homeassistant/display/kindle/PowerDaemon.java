package homeassistant.display.kindle;

public class PowerDaemon {

    private Lipc lipc = new Lipc();

    public boolean isScreenSaverEnabled() {
        return getLipcProperty("preventScreenSaver").equals("0");
    }

    public void enableScreenSaver() {
        setLipcProperty("preventScreenSaver", "0");
    }

    public void disableScreenSaver() {
        setLipcProperty("preventScreenSaver", "1");
    }

    private String getLipcProperty(String property) {
        return lipc.getProperty("com.lab126.powerd", property);
    }

    private void setLipcProperty(String property, String value) {
        lipc.setProperty("com.lab126.powerd", property, value);
    }

}
