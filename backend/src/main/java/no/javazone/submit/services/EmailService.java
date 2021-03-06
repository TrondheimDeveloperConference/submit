package no.javazone.submit.services;

import no.javazone.submit.api.representations.EmailAddress;
import no.javazone.submit.api.representations.Submission;
import no.javazone.submit.api.representations.Token;
import no.javazone.submit.config.EmailConfiguration;
import no.javazone.submit.util.AuditLogger;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static no.javazone.submit.util.AuditLogger.Event.SENT_EMAIL;

@Service
public class EmailService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final EmailConfiguration emailConfiguration;
    private final AuthenticationService authenticationService;

    @Autowired
    public EmailService(EmailConfiguration emailConfiguration, AuthenticationService authenticationService) {
        this.emailConfiguration = emailConfiguration;
        this.authenticationService = authenticationService;
    }

    public void sendTokenToUser(EmailAddress emailAddress, Token token) {
        send(emailAddress, emailConfiguration.subjectPrefix + "TDC submission login", generateTokenEmail(token));
    }

    public void sendTokenToUserAndInformAboutNewlyCreatedDraft(EmailAddress emailAddress, Token token) {
        send(emailAddress, emailConfiguration.subjectPrefix + "TDC draft ready for editing", generateDraftEmail(token));
    }

    public void notifySpeakerAboutStatusChangeToInReview(Submission submission) {
        submission.speakers.stream()
                .map(s -> s.email)
                .map(EmailAddress::new)
                .forEach(emailAddress -> {
                    Token token = authenticationService.createTokenForEmail(emailAddress);
                    send(emailAddress, emailConfiguration.subjectPrefix + "TDC submission marked for review: " + submission.title, generateReviewEmail(submission, token));
                });
    }

    private void send(EmailAddress address, String subject, String emailBody) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(emailConfiguration.hostName);
            if(emailConfiguration.authenticate) {
                email.setAuthenticator(new DefaultAuthenticator(emailConfiguration.smtpUser, emailConfiguration.smtpPass));
            }

            if(emailConfiguration.useSSL) {
                email.setSSLOnConnect(true);
                email.setSslSmtpPort("" + emailConfiguration.smtpPort);

            } else {
                email.setSmtpPort(emailConfiguration.smtpPort);
            }
            email.setFrom(emailConfiguration.fromAddress, emailConfiguration.fromName);
            email.setSubject(subject);
            email.setMsg(emailBody);
            email.addTo(address.toString());
            email.send();
            AuditLogger.log(SENT_EMAIL, "emailaddress " + address);
        } catch (EmailException e) {
            LOG.warn("Couldn't send email to " + address, e);
        }
    }

    private String generateTokenEmail(Token token) {
        return "Ready to submit a talk to Trondheim Developer Conference, or editing your talk?\n\n" +
                "Use this link to log your browser in to our submission system:\n" +
                emailConfiguration.tokenLinkPrefix + "/" + token + "\n\n" +
                "Clicking this link will authenticate your browser and keep you logged in. Using a public computer? Use the 'forget me' button on the logged in page." + "\n\n" +
                "Don't know why you received this email? Someone probably just misspelled their email address. Don't worry, they can't do anything on your behalf without this link" + "\n\n" +
                "Best regards," + "\n" + "The Trondheim Developer Conference Program Committee";
    }

    private String generateDraftEmail(Token token) {
        return "We have created a draft for you in our submission system which you can use to create and submit your talk.\n\n" +
                "Use this link to log your browser in to our submission system:\n" +
                emailConfiguration.tokenLinkPrefix + "/" + token + "\n\n" +
                "Clicking this link will authenticate your browser and keep you logged in. Using a public computer? Use the 'forget me' button on the logged in page." + "\n\n" +
                "Don't know why you received this email? You receive this email because someone in our program committee would like you to submit a talk. Contact us at program@java.no if you have any further questions." + "\n\n" +
                "Best regards," + "\n" + "The Trondheim Developer Conference Program Committee";
    }

    private String generateReviewEmail(Submission submission, Token token) {
        String speakerNames = submission.speakers.stream()
                .map(s -> s.name)
                .filter(Objects::nonNull)
                .filter((s) -> !s.isEmpty())
                .collect(joining(" & "));

        return ("Dear " + speakerNames + "\n\n") +
                "Thank your for submitting your talk '" + submission.title + "' to Trondheim Developer Conference :)\n\n" +
                "You just marked your talk as ready for review, meaning that the program committee will have a look at it at their earliest convenience. In the case that the program committee has any feedback for you, they will send it to you by email.\n\n" +
                "Feel free to edit your talk further at any time. Just use the same browser as before - the submission system will keep you logged in. Alternatively, you can use this link to log any browser into our submission system to keep working on your talk:\n" +
                emailConfiguration.tokenLinkPrefix + "/" + token + "\n\n" +
                "Best regards," + "\n" + "The Trondheim Developer Conference Program Committee";
    }
}
