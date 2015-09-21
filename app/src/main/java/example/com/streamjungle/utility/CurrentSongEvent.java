package example.com.streamjungle.utility;

/**
 * Created by alamatounkara on 9/8/15.
 */
public class CurrentSongEvent extends AbstractEvent {

    public enum CurrentSong{CURRENT_TRACK, SEEK_BAR, SONG_STOP, SONG_PLAY, SONG_NEXT,
        SONG_PREV, PLAYER_ACTIVITY_IS_ON };
    //result code needed to check the result
    private int mResulCode;
    //current track playing
    private int mCurrentTrack;
    //the duration of a specific song
    private int mSongDuration;
    //this will be the actual position of the song whern it is playing
    int mCurrentPositionInASong;

    public CurrentSongEvent(Enum type,int resultCode,int currentPostion) {
        super(type);
        this.mResulCode=resultCode;
        this.mCurrentTrack =currentPostion;
    }

    public CurrentSongEvent(Enum type,int resultCode,int currentPostion, int songDuration) {
        super(type);
        this.mResulCode=resultCode;
        this.mSongDuration=songDuration;
        this.mCurrentTrack =currentPostion;
    }

    public CurrentSongEvent(Enum type,int resultCode,int currentPostion, int songDuration, int currentPositionInASong) {
        super(type);
        this.mResulCode=resultCode;
        this.mSongDuration=songDuration;
        this.mCurrentTrack =currentPostion;
        this.mCurrentPositionInASong = currentPositionInASong;
    }

    public CurrentSongEvent(Enum type,int resultCode) {
        super(type);
       this.mResulCode=resultCode;
    }


    public CurrentSongEvent(Enum type) {
        super(type);
    }

    public int getCurrentTrack() {
        return mCurrentTrack;
    }

    public int getCurrentPositionInASong() {
        return mCurrentPositionInASong;
    }

    public int getSongDuration() {
        return mSongDuration;
    }

    public int getResulCode() {
        return mResulCode;
    }
}
