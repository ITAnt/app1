package com.jancar.bluetooth.phone.view;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jancar.JancarServer;
import com.jancar.bluetooth.Listener.BTMusicListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothMusicData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.MusicContract;
import com.jancar.bluetooth.phone.presenter.MusicPresenter;
import com.jancar.bluetooth.phone.util.TimeUtil;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.jancar.key.KeyDef;
import com.jancar.key.keyFocuser;
import com.ui.mvp.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_UP;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:22
 */
public class MusicActivity extends BaseActivity<MusicContract.Presenter, MusicContract.View> implements MusicContract.View, BTMusicListener {

    private final int MSG_INIT_OK = 0;
    private final int MSG_UI_REFRESH_ID3_INFO = 1;
    private final int MSG_UI_REFRESH_PLAY_STATE = 2;
    public static final byte CMD_UPDATE_PLAY_STATUS = (byte) 0x01;
    public static final byte CMD_UPDATE_PLAY_POSITION = (byte) 0x02;
    private BluetoothManager bluetoothManager;
    private int mDefaultMusicLong = 0; //00:00:00
    private int mDefaultPlayingTime = 0; //00:00:00
    private int musicTotalLength = 0;
    private int musicPlayingTime = 0;
    Unbinder unbinder;
    @BindView(R.id.tv_music_playTime)
    TextView tvPlayTime;
    @BindView(R.id.tv_music_play_total_time)
    TextView tvPlayTotalTime;
    @BindView(R.id.seekbar_btmusic)
    SeekBar seekBar;
    @BindView(R.id.iv_music_play)
    ImageView ivPlay;
    @BindView(R.id.tv_music_title)
    TextView tvTitle;
    @BindView(R.id.tv_music_album)
    TextView tvAlbum;
    @BindView(R.id.tv_music_artist)
    TextView tvArtist;
    private boolean isPlay;
    private AudioManager audioManager;
    private JancarServer jancarServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        unbinder = ButterKnife.bind(this);
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this);
        seekBar.setEnabled(false);
        updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, mDefaultMusicLong, mDefaultPlayingTime);
        updatePlayingStatus(mDefaultMusicLong, mDefaultPlayingTime, (byte) 0);
        jancarServer = (JancarServer) getSystemService(JancarServer.JAC_SERVICE);
        jancarServer.requestKeyFocus(keyFocusListener);
    }

    private keyFocuser keyFocusListener = new keyFocuser() {

        @Override
        public int getDescriptor() throws RemoteException {
            return super.getDescriptor();
        }

        @Override
        public boolean OnKeyEvent(int key, int state) {

            boolean bRet = true;
            KeyDef.KeyType keyType = KeyDef.KeyType.nativeToType(key);
            KeyDef.KeyAction keyAction = KeyDef.KeyAction.nativeToType(state);
            switch (keyType) {
                case KEY_PREV:
                    if (keyAction == KEY_ACTION_UP) {
                        bluetoothManager.prev();
                    }
                    break;
                case KEY_NEXT:
                    if (keyAction == KEY_ACTION_UP) {
                        bluetoothManager.next();
                    }
                    break;
                default:
                    bRet = false;
                    break;
            }
            return bRet;
        }

        @Override
        public void OnKeyFocusChange(int ifocus) {
        }
    };

    @Override
    protected void onStart() {
        requestAudioFoucse();
        super.onStart();
    }

    private void requestAudioFoucse() {

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    bluetoothManager.setPlayerState(true);
                    if (!isPlay) {
                        bluetoothManager.play();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    bluetoothManager.setPlayerState(false);
                    if (isPlay) {
                        bluetoothManager.pause();
                    }
                    if (mAudioFocusListener != null) {
                        audioManager.abandonAudioFocus(mAudioFocusListener);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    bluetoothManager.setPlayerState(false);
                    if (isPlay) {
                        bluetoothManager.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        bluetoothManager.registerBTMusicListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            bluetoothManager.pause();
        }
        if (mAudioFocusListener != null) {
            audioManager.abandonAudioFocus(mAudioFocusListener);
        }
        bluetoothManager.unRegisterBTMusicListener();
        jancarServer.abandonKeyFocus(keyFocusListener);
    }

    @Override
    public MusicContract.Presenter createPresenter() {
        return new MusicPresenter();
    }

    @Override
    public MusicContract.View getUiImplement() {
        return this;
    }

    @SuppressLint("HandlerLeak")
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_INIT_OK:
                    BluetoothMusicData bluetoothMusicData = bluetoothManager.getBlueMusicData();
                    updateMetadata(bluetoothMusicData.getTitle(), bluetoothMusicData.getArtist(), bluetoothMusicData.getAlbum());
                    updatePlaybackStatus(bluetoothMusicData.getPlay_status(), bluetoothMusicData.getSong_len(), bluetoothMusicData.getSong_pos());
                    break;
                case MSG_UI_REFRESH_ID3_INFO:
                    BluetoothMusicData blueMusic = (BluetoothMusicData) msg.obj;
                    updateMetadata(blueMusic.getTitle(), blueMusic.getArtist(), blueMusic.getAlbum());
                    break;
                case MSG_UI_REFRESH_PLAY_STATE:
                    BluetoothMusicData blueMusicStatus = (BluetoothMusicData) msg.obj;
                    updatePlaybackStatus(blueMusicStatus.getPlay_status(), blueMusicStatus.getSong_len(), blueMusicStatus.getSong_pos());
                    break;
            }
        }
    };

    /**
     * 更新音乐信息
     *
     * @param title
     * @param artist
     * @param album
     */

    private void updateMetadata(String title, String artist, String album) {
        if (!TextUtils.isEmpty(title.trim())) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(artist.trim())) {
            tvArtist.setText(artist);
        }
        if (!TextUtils.isEmpty(album.trim())) {
            tvAlbum.setText(album);
        }
    }

    /**
     * 更新播放状态
     *
     * @param play_status 播放的状态
     * @param song_len    总长度
     * @param song_pos    已经播放的位置
     */


    private void updatePlaybackStatus(byte play_status, int song_len, int song_pos) {
        switch (play_status) {
            case BluetoothManager.MUSIC_STATE_PLAY:
                ivPlay.setImageResource(R.drawable.music_pause_selector);
                isPlay = true;
                break;
            case BluetoothManager.MUSIC_STATE_PAUSE:
            case BluetoothManager.MUSIC_STATE_STOP:
                ivPlay.setImageResource(R.drawable.music_play_selector);
                isPlay = false;
                break;
        }
        if (song_len == 0) {
            musicTotalLength = 0;
            updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, mDefaultMusicLong, mDefaultPlayingTime);
            updatePlayingStatus(mDefaultMusicLong, mDefaultPlayingTime, (byte) 0);
        } else if (song_len == musicTotalLength) {
            if (song_pos != musicPlayingTime) {
                musicPlayingTime = song_pos;
                updateMusicPlayingProgress(CMD_UPDATE_PLAY_POSITION, musicTotalLength, musicPlayingTime);
                updatePlayingProcess(musicPlayingTime);
            }

        } else {
            musicTotalLength = song_len;
            musicPlayingTime = song_pos;
            updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, musicTotalLength, musicPlayingTime);
            updatePlayingStatus(musicTotalLength, musicPlayingTime, play_status);
        }

    }

    /**
     * @param pos 播放时长
     */

    private void updatePlayingProcess(int pos) {
        String position = null;
        if (pos != 0xFFFFFFFF) {
            position = TimeUtil.millSeconds2readableTime(pos);
        } else {
            position = "00:00:00";
        }
        tvPlayTime.setText(position);
    }

    /**
     * @param cmdType
     * @param total_long   总时长
     * @param playing_time 播放的时长 进度条
     */

    private void updateMusicPlayingProgress(byte cmdType, int total_long, int playing_time) {

        switch (cmdType) {
            case CMD_UPDATE_PLAY_STATUS: {
                if (total_long == (int) 0xFFFFFFFF) {
                    total_long = 0;
                }
                if (playing_time == (int) 0xFFFFFFFF) {
                    playing_time = 0;
                }

                seekBar.setMax(total_long);
                seekBar.setProgress(playing_time);
            }
            break;
            case CMD_UPDATE_PLAY_POSITION: {
                if (playing_time == (int) 0xFFFFFFFF)
                    playing_time = 0;
                seekBar.setProgress(playing_time);
            }
            break;
        }
    }

    private void updatePlayingStatus(int song_length, int song_position, byte play_status) {
        String length = null;
        String pos = null;

        if (song_length != 0xFFFFFFFF) {
            length = TimeUtil.millSeconds2readableTime(song_length);
        } else {
            length = "00:00:00";
        }
        if (song_position != 0xFFFFFFFF) {
            pos = TimeUtil.millSeconds2readableTime(song_position);
        } else {
            pos = "00:00:00";
        }
        tvPlayTotalTime.setText(length);//总时间
        tvPlayTime.setText(pos);//播放的时间
    }

    @OnClick({R.id.iv_music_pre, R.id.iv_music_next, R.id.iv_music_play})
    public void onclick(View view) {
        if (!bluetoothManager.isConnect()) {
            ToastUtil.ShowToast(this, "蓝牙未连接");
            return;
        }
        switch (view.getId()) {
            case R.id.iv_music_pre:
                bluetoothManager.prev();
                break;
            case R.id.iv_music_next:
                bluetoothManager.next();
                break;
            case R.id.iv_music_play:
                if (isPlay) {
                    bluetoothManager.pause();

                } else {
                    bluetoothManager.play();
                    bluetoothManager.setPlayerState(true);
                }
                break;
        }
    }


    @Override
    public void onNotifyBTMusicInitSuccess() {
        UIHandler.sendEmptyMessage(MSG_INIT_OK);
    }

    @Override
    public void onNotifyBTMusicID3Info(final BluetoothMusicData bluetoothMusicData) {
        Message msg = UIHandler.obtainMessage();
        msg.what = MSG_UI_REFRESH_ID3_INFO;
        msg.obj = bluetoothMusicData;
        msg.sendToTarget();
    }

    @Override
    public void onNotifyBTMusicPlayState(final BluetoothMusicData bluetoothMusicData) {
        Message msg = UIHandler.obtainMessage();
        msg.what = MSG_UI_REFRESH_PLAY_STATE;
        msg.obj = bluetoothMusicData;
        msg.sendToTarget();

    }
}
