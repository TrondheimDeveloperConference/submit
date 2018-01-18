package no.javazone.submit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email")
public class EmailConfiguration {

    public String smtpUser;

    public String smtpPass;

    public String tokenLinkPrefix;

    public String subjectPrefix;

    public String hostName;

    public int smtpPort;

    public boolean authenticate;
    public boolean useSSL;

    public String fromAddress;

    public String fromName;

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public void setSmtpPass(String smtpPass) {
        this.smtpPass = smtpPass;
    }

    public void setTokenLinkPrefix(String tokenLinkPrefix) {
        this.tokenLinkPrefix = tokenLinkPrefix;
    }

    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }
}
