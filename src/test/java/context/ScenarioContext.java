package context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private Map<String, Object> contextData = new HashMap<>();

    public void setContext(String key, Object data) {
        contextData.put(key, data);
    }

    public Object getContext(String key) {
        return contextData.get(key);
    }
}
