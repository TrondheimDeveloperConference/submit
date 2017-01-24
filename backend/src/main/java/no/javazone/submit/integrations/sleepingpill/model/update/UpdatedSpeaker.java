package no.javazone.submit.integrations.sleepingpill.model.update;

import no.javazone.submit.integrations.sleepingpill.model.common.SpeakerData;
import no.javazone.submit.api.representations.Speaker;

public class UpdatedSpeaker {

    public String id;
    public String name;
    public String email;
    public SpeakerData data;

    @SuppressWarnings("unused")
    private UpdatedSpeaker() { }

    public UpdatedSpeaker(String id, String name, String email, String bio, String zipCode, String twitter) {
        this.id = id;
        this.name = name;
        this.email = email;

        data = new SpeakerData();
        data.setBio(bio);
        data.setZipCode(zipCode);
        data.setTwitter(twitter);
    }

    public static UpdatedSpeaker fromApiObject(Speaker speaker) {
        return new UpdatedSpeaker(speaker.id, speaker.name, speaker.email, speaker.bio, speaker.zipCode, speaker.twitter);
    }
}
