package no.javazone.submit.integrations.slack;

import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackPreparedMessage;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import no.javazone.submit.api.representations.Comment;
import no.javazone.submit.config.CakeConfiguration;
import no.javazone.submit.config.SlackConfiguration;
import no.javazone.submit.integrations.sleepingpill.model.common.SessionStatus;
import no.javazone.submit.integrations.sleepingpill.model.common.Speaker;
import no.javazone.submit.integrations.sleepingpill.model.get.Session;
import no.javazone.submit.integrations.sleepingpill.model.get.Sessions;
import no.javazone.submit.util.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static no.javazone.submit.integrations.sleepingpill.model.common.SessionStatus.SUBMITTED;
import static no.javazone.submit.util.AuditLogger.Event.SENT_SLACK_MESSAGE;

@Service
public class SlackClient {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final SlackSession slack;
    private final CakeConfiguration cakeConfiguration;
    private final SlackConfiguration slackConfiguration;

    @Autowired
    public SlackClient(SlackConfiguration slackConfiguration, CakeConfiguration cakeConfiguration) {
        this.slackConfiguration = slackConfiguration;
        this.cakeConfiguration = cakeConfiguration;
        if(slackConfiguration.disabled) {
            slack = null;
        } else {
            slack = SlackSessionFactory.createWebSocketSlackSession(slackConfiguration.token);
            connectIfNessesary();
        }
    }

    public void postTalkMarkedForInReview(String id, String title, String format, String length, String language, String theAbstract, String submitterName, String submitterImage) {
        if(slackConfiguration.disabled) {
            return;
        }
        connectIfNessesary();

        SlackChannel channel = slack.findChannelByName(slackConfiguration.channel);

        SlackAttachment attachment = new SlackAttachment(title, "", "_Speaker has changed the talk status to 'ready for review'_", null);
        attachment.setColor("#30a74d");

        attachment.addField("Description of the talk", theAbstract, false);
        attachment.addField("Format", format + " (" + length + "min)", true);
        attachment.addField("Language", language, true);
        if (submitterImage != null) {
            attachment.setAuthorIcon(submitterImage);
        }
        attachment.setTitleLink(cakeConfiguration.baseUri + "/secured/#/showTalk/" + id);
        attachment.setAuthorName("Speaker: " + submitterName);
        attachment.addMarkdownIn("text");

        SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                .addAttachment(attachment)
                .build();
        slack.sendMessage(channel, message);

        AuditLogger.log(SENT_SLACK_MESSAGE, "session " + id, "type marked-for-review");
    }

    public void postTalkMarkedForNotInReview(String id, String title, String submitterName, String submitterEmail, String submitterImage) {
        if(slackConfiguration.disabled) {
            return;
        }
        connectIfNessesary();

        SlackChannel channel = slack.findChannelByName(slackConfiguration.channel);

        SlackAttachment attachment = new SlackAttachment(title, "", "_Speaker has changed the talk status back to 'not in review'_", null);
        attachment.setColor("#b63d9d");
        if (submitterImage != null) {
            attachment.setAuthorIcon(submitterImage);
        }
        attachment.setAuthorName("Speaker: " + submitterName);
        attachment.addMarkdownIn("text");

        SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                .addAttachment(attachment)
                .build();
        slack.sendMessage(channel, message);

        AuditLogger.log(SENT_SLACK_MESSAGE, "session " + id, "type marked-for-not-in-review");
    }

    public void postTalkReceivedNewComment(String id, String title, String speaker, String submitterImage, Comment comment) {
        if(slackConfiguration.disabled) {
            return;
        }
        connectIfNessesary();

        SlackChannel channel = slack.findChannelByName(slackConfiguration.channel);

        SlackAttachment attachment = new SlackAttachment(title, "", "_Speaker added new comment to his talk. Somebody should probably reply :)_", null);
        attachment.setColor("#F012BE");
        if (submitterImage != null) {
            attachment.setAuthorIcon(submitterImage);
        }
        attachment.setTitleLink(cakeConfiguration.baseUri + "/secured/#/showTalk/" + id);
        attachment.setAuthorName("Speaker: " + speaker);
        attachment.addMarkdownIn("text");

        attachment.addField("The new comment", comment.comment, false);

        SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                .addAttachment(attachment)
                .build();
        slack.sendMessage(channel, message);

        AuditLogger.log(SENT_SLACK_MESSAGE, "session " + id, "type new-comment-from-speaker");
    }

    private void connectIfNessesary() {
        if (!slack.isConnected()) {
            try {
                slack.connect();
            } catch (IOException e) {
                LOG.error("Could not connect to slack", e);
            }
        }
    }

    public void postStatistics(Sessions sessions) {
        if(slackConfiguration.disabled) {
            return;
        }
        List<Session> allTalks = sessions.sessions;
        List<Session> submittedTalks = allTalks.stream().filter(s -> s.status == SUBMITTED).collect(toList());

        int total = allTalks.size();
        long draft = count(allTalks, SessionStatus.DRAFT);
        long submitted = count(allTalks, SUBMITTED);
        long approved = count(allTalks, SessionStatus.APPROVED);
        long rejected = count(allTalks, SessionStatus.REJECTED);

        long presentations45 = submittedTalks.stream().filter(s -> "presentation".equals(s.getFormat()) && "45".equals(s.getLength())).count();
        long presentations60 = submittedTalks.stream().filter(s -> "presentation".equals(s.getFormat()) && "60".equals(s.getLength())).count();
        long lightningTalks10 = submittedTalks.stream().filter(s -> "lightning-talk".equals(s.getFormat()) && "10".equals(s.getLength())).count();
        long lightningTalks20 = submittedTalks.stream().filter(s -> "lightning-talk".equals(s.getFormat()) && "20".equals(s.getLength())).count();
        long workshops = submittedTalks.stream().filter(s -> "workshop".equals(s.getFormat())).count();

        long norwegian = submittedTalks.stream().filter(s -> "no".equals(s.getLanguage())).count();
        long english = submittedTalks.stream().filter(s -> "en".equals(s.getLanguage())).count();

        long totalSpeakers = submittedTalks.stream().map(s -> s.speakers).flatMap(List::stream).map(s -> s.email).count();
        int uniqueSpeakers = submittedTalks.stream().map(s -> s.speakers).flatMap(List::stream).map(s -> s.email).collect(toSet()).size();
        long speakersWithImage = submittedTalks.stream().map(s -> s.speakers).flatMap(List::stream).map(Speaker::getPictureId).filter(Objects::nonNull).count();

        connectIfNessesary();

        SlackChannel channel = slack.findChannelByName(slackConfiguration.channel);

        SlackAttachment attachment = new SlackAttachment("Some statistics about the submitted talks", "", "_These statistics are posted 16.00 every day. All statistics are based on submitted talks (i.e excluding drafts) unless noted_", null);
        attachment.setColor("#3abae9");
        attachment.addMarkdownIn("text");

        attachment.addField("Talks: Draft", draft + " talks", true);
        attachment.addField("Talks: Submitted", submitted + " talks", true);
        attachment.addField("Talks: Approved", approved + " talks", true);
        attachment.addField("Talks: Rejected", rejected + " talks", true);

        attachment.addField("Norwegian", norwegian + " talks", true);
        attachment.addField("English", english + " talks", true);

        attachment.addField("Presentations - 45 min", presentations45 + " stk", true);
        attachment.addField("Presentations - 60 min", presentations60 + " stk", true);
        attachment.addField("Lightning talks - 10 min", lightningTalks10 + " stk", true);
        attachment.addField("Lightning talks - 20 min", lightningTalks20 + " stk", true);
        attachment.addField("Workshops", workshops + " stk", true);

        attachment.addField("Speakers", totalSpeakers + " speakers, " + speakersWithImage + " with picture (" + uniqueSpeakers + " unique emails)", false);

        SlackPreparedMessage message = new SlackPreparedMessage.Builder()
                .addAttachment(attachment)
                .build();
        slack.sendMessage(channel, message);

        AuditLogger.log(SENT_SLACK_MESSAGE, "type statistics");

    }

    private long count(List<Session> talks, SessionStatus status) {
        return talks.stream().filter(s -> s.status == status).count();
    }


}
