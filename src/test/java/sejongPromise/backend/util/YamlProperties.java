package sejongPromise.backend.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class YamlProperties {
    private Map<String, Object> props;

    public void load() throws IOException {
        final String path = "/application.yml";
        try(InputStream is = YamlProperties.class.getResourceAsStream(path)){
            this.props = new Yaml().load(is);
        }
    }

    private Object getValue(String key){
        String[] path = key.split("\\.");
        Map<String, Object> root = props;
        for(int i=0; i<path.length; i++){
            Object value = root.get(path[i]);
            if(value == null){
                throw new RuntimeException("Can't resolve key path: " + path[i]);
            }
            if(i < path.length - 1){
                if(!(value instanceof Map)){
                    throw new RuntimeException("Can't resolve key path: " + path[i]);
                }
                root = (Map<String, Object>) value;
            }else{
                return value;
            }
        }
        throw new RuntimeException("empty key");
    }

    public String get(String key){ return getValue(key).toString(); }

}
