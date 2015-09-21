package example.com.streamjungle.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alamatounkara on 9/6/15.
 */
public class SoundCloudModel {

    /**
     * @SerializedName is from Gson. It will take json fetched and map its fields  to our java
     * object fields. It acts as a setter here, which deserialize the specific json file into
     * our java object
     */
    @SerializedName("title")
    private String mTitle;

    @SerializedName("id")
    private String mID;

    @SerializedName("stream_url")
    private String mStreamUrl;

    @SerializedName("artwork_url")
    private String mArtworkUrl;

    @SerializedName("genre")
    private String mGenre;


    public String getTitle(){
       return mTitle;
   }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public String getID() {
        return mID;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public String getGenre() {
        return mGenre;
    }


    public void setTitle(String title){this.mTitle = title;}

    public void setArtworkUrl(String img) {
        this.mArtworkUrl= img;
    }

    public void setID(String id) {
        this.mID = id;
    }

    public void setStreamUrl(String streamUrl) {
        this.mStreamUrl = streamUrl;
    }

    public void setGenre(String genre) {
        this.mGenre =genre;
    }

    /*
{kind: "track",
        id: 222658837,
        created_at: "2015/09/06 21:21:58 +0000",
        user_id: 96399875,
        duration: 70119,
        commentable: true,
        state: "finished",
        original_content_size: 12358354,
        last_modified: "2015/09/06 21:21:58 +0000",
        sharing: "public",
        tag_list: "Slow House Music Emotional anonymous you put the name",
        permalink: "you-put-the-name",
        streamable: true,
        embeddable_by: "all",
        downloadable: false,
        purchase_url: null,
        label_id: null,
        purchase_title: null,
        genre: "Edm",
        title: "You Put The Name",
        description: "",
        label_name: null,
        release: null,
        track_type: null,
        key_signature: null,
        isrc: null,
        video_url: null,
        bpm: null,
        release_year: null,
        release_month: null,
        release_day: null,
        original_format: "wav",
        license: "all-rights-reserved",
        uri: "https://api.soundcloud.com/tracks/222658837",
        user: {
        id: 96399875,
        kind: "user",
        permalink: "djablak",
        username: "Dj Ablak",
        last_modified: "2015/09/04 12:42:15 +0000",
        uri: "https://api.soundcloud.com/users/96399875",
        permalink_url: "http://soundcloud.com/djablak",
        avatar_url: "https://i1.sndcdn.com/avatars-000125981443-9k0s9h-large.jpg"
        },
        permalink_url: "http://soundcloud.com/djablak/you-put-the-name",
        artwork_url: null,
        waveform_url: "https://w1.sndcdn.com/gAKGmLcd914T_m.png",
        stream_url: "https://api.soundcloud.com/tracks/222658837/stream",
        playback_count: 0,
        download_count: 0,
        favoritings_count: 0,
        comment_count: 0,
        attachments_uri: "https://api.soundcloud.com/tracks/222658837/attachments",
        policy: "ALLOW",
        monetization_model: "NOT_APPLICABLE"
        }
*/

}

