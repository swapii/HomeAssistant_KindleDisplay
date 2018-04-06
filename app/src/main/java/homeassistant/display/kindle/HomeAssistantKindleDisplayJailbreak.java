package homeassistant.display.kindle;

import ixtab.jailbreak.Jailbreak;

import java.security.AllPermission;

public class HomeAssistantKindleDisplayJailbreak extends Jailbreak {

    public boolean requestPermissions() {
        return getContext().requestPermission(new AllPermission());
    }

}
