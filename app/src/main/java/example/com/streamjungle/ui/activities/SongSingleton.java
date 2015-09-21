package example.com.streamjungle.ui.activities;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import example.com.streamjungle.utility.AbstractEvent;

/**
 * We are  making several calls to the EventBus object. Each call is experience.
 * And we all need one instance of the Bus t work accross our application.
 * Therefore we create a singleton for it.
 *
 * @author Alama Tounkara
 */
public class SongSingleton {

    private static SongSingleton mSongSingleton = null;
    private final Bus mBus;

    private SongSingleton() {
        mBus = new Bus(ThreadEnforcer.ANY);
    }

    public static SongSingleton getInstance() {
        if (mSongSingleton == null) {
            mSongSingleton = new SongSingleton();
        }
        return mSongSingleton;
    }

    public Bus getBus() {
        return mBus;
    }

    public void registerMyBus(Object obj) {
        mBus.register(obj);
    }

    public void unRegisterMyBus(Object obj) {
        mBus.unregister(obj);
    }

    public void postMsg(AbstractEvent event) {
        mBus.post(event);
    }
}
