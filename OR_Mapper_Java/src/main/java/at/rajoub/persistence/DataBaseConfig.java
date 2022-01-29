package at.rajoub.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;

@Data
@NoArgsConstructor
/*
 * Create the connection after checking the configurations
 */
public class DataBaseConfig {
    @JsonProperty
    private String url;

    @JsonProperty
    private String userName;

    @JsonProperty
    private String password;

    @SneakyThrows
    public static DataBaseConfig getInstance() {

        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(new File("src/main/java/at/rajoub/settings/config.xml"),DataBaseConfig.class);
    }
}
