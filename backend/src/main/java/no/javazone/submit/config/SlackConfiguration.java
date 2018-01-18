package no.javazone.submit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "slack")
public class SlackConfiguration {

    public String token;

    public String channel;
    public boolean disabled;

    public void setToken(String token) {
        this.token = token;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
