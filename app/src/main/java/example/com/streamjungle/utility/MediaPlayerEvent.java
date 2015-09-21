package example.com.streamjungle.utility;

/**
 * Created by alamatounkara on 9/17/15.
 */
public class MediaPlayerEvent extends AbstractEvent {


    public enum MediaSong{CURRENT_TRACK, SEEK_BAR, SONG_STOP, SONG_PLAY, SONG_NEXT,
        SONG_PREV, PLAYER_ACTIVITY_IS_ON };
    //result code needed to check the result
    private int mResulCode;
    //current track playing
    private int mCurrentTrack;
    //the duration of a specific song
    private int mSongDuration;
    //this will be the actual position of the song whern it is playing
    int mCurrentPositionInASong;

    public MediaPlayerEvent(Enum type,int resultCode,int currentPositionInASong) {
        super(type);
        this.mResulCode=resultCode;
        this.mCurrentPositionInASong = currentPositionInASong;
    }
    public MediaPlayerEvent(Enum type, int currentPositionInASong) {
        super(type);
        this.mCurrentPositionInASong = currentPositionInASong;
    }

    protected MediaPlayerEvent(Enum type) {
        super(type);
    }

    public int getResulCode() {
        return mResulCode;
    }

    public int getSongDuration() {
        return mSongDuration;
    }

    public int getCurrentPositionInASong() {
        return mCurrentPositionInASong;
    }
}
