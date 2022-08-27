package pl.edu.pw.parser;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;

@Configuration
public class ParserConfiguration {
    @Bean
    public UserAgentParser userAgentParser() throws IOException, ParseException {
        return new UserAgentService().loadParser(Arrays.asList(BrowsCapField.BROWSER, BrowsCapField.BROWSER_TYPE,
                BrowsCapField.BROWSER_MAJOR_VERSION,
                BrowsCapField.DEVICE_TYPE, BrowsCapField.PLATFORM, BrowsCapField.PLATFORM_VERSION,
                BrowsCapField.RENDERING_ENGINE_VERSION, BrowsCapField.RENDERING_ENGINE_NAME,
                BrowsCapField.PLATFORM_MAKER, BrowsCapField.RENDERING_ENGINE_MAKER));
    }
}
