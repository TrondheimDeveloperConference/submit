package no.javazone.services;

import no.javazone.config.EmailConfiguration;
import no.javazone.representations.EmailAddress;
import no.javazone.representations.Token;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final EmailConfiguration emailConfiguration;

    @Autowired
    public EmailService(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    public void sendTokenToUser(EmailAddress emailAddress, Token token) {
        String emailBody = generateEmailBody(token);

        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator(emailConfiguration.smtpUser, emailConfiguration.smtpPass));
            email.setSSLOnConnect(true);
            email.setFrom("program@java.no");
            email.setSubject("JavaZone submission login");
            email.setMsg(emailBody);
            email.addTo(emailAddress.toString());
            email.send();
            LOG.info("Token " + token + " was sent to: " + emailAddress);
        } catch (EmailException e) {
            LOG.warn("Couldn't send token " + token + " to " + emailAddress, e);
        }
    }

    private String generateEmailBody(Token token) {
        StringBuilder b = new StringBuilder();
        b.append("Ready to submit a talk to JavaZone, or editing your talk?\n\n");
        b.append("Use this link to log your browser in to our submitting system:\n");
        b.append(emailConfiguration.tokenLinkPrefix).append("/").append(token).append("\n\n");
        b.append("Don't know why you received this email? Someone probably just misspelled their email address. Don't worry, they can't do anything on your behalf without this link");
        return b.toString();
    }
}
