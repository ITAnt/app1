package com.jancar.bluetooth.phone.view;

import android.content.Context;
import android.os.RemoteException;

import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.listener.JacMediaSessionLitener;
import com.jancar.key.KeyDef;
import com.jancar.media.JacMediaSession;

import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_UP;

/**
 * @anthor Tzq
 * @time 2018/12/6 16:45
 * @describe JacMediaSession封装类
 */
public class JacMediaSessionUtils implements JacMediaSessionLitener {
    JacMediaSession jacMediaSession;
    private Context context;
    private KeyEventListener keyEventListener;
    private static JacMediaSessionUtils jacMediaSessionUtils;

    public void setKeyEventListener(KeyEventListener keyEventListener) {
        this.keyEventListener = keyEventListener;
    }

    public interface KeyEventListener {
        void keyCallBack(int keyType);
    }

    public static JacMediaSessionUtils getJacMediaSessionUtils(Context context) {
        if (jacMediaSessionUtils == null) {
            jacMediaSessionUtils = new JacMediaSessionUtils(context.getApplicationContext());
        }
        return jacMediaSessionUtils;
    }

    public JacMediaSessionUtils(Context context) {
        this.context = context;
        JacMediaInit();
    }

    private void executeKey(int type) {
        if (keyEventListener != null) {
            keyEventListener.keyCallBack(type);
        }
    }

    @Override
    public void JacMediaInit() {
        jacMediaSession = new JacMediaSession(context) {
            @Override
            public boolean OnKeyEvent(int key, int state) throws RemoteException {
                boolean bRet = true;
                try {
                    KeyDef.KeyType keyType = KeyDef.KeyType.nativeToType(key);
                    KeyDef.KeyAction keyAction = KeyDef.KeyAction.nativeToType(state);
                    if (keyAction == KEY_ACTION_UP) {
                        switch (keyType) {
                            case KEY_PREV:
                                //上一首
                                executeKey(0);
                                break;
                            case KEY_NEXT:
                                //下一首
                                executeKey(1);
                                break;
                            case KEY_PAUSE:
                                //暂停
                                executeKey(2);
                                break;
                            case KEY_PLAY:
                                //播放
                                executeKey(3);
                                break;
                            default:
                                bRet = false;
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bRet;
            }
        };

    }

    @Override
    public void JacMediaRegister() {
        jacMediaSession.setActive(true);

    }

    @Override
    public void JacMadiaUnRegister() {
        jacMediaSession.setActive(false);

    }

    @Override
    public void JacNotifyPlayUri(String uri) {
        jacMediaSession.notifyPlayUri(uri);

    }

    @Override
    public void JacNotifyId3(String title, String artist, String album, byte[] artWork) {
        jacMediaSession.notifyId3(title, artist, album, artWork);

    }

    @Override
    public void JacNotifyPlayState(int play_status) {
        jacMediaSession.notifyPlayState(play_status);

    }

    @Override
    public void JacNotifyProgress(long progress, long duration) {
        jacMediaSession.notifyProgress(progress, duration);

    }

}
