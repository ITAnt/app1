package com.jancar.bluetooth.phone.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.Listener.BTMusicListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothMusicData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.MusicContract;
import com.jancar.bluetooth.phone.presenter.MusicPresenter;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.TimeUtil;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.jancar.bluetooth.phone.widget.MarqueeTextView;
import com.ui.mvp.view.BaseActivity;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:22
 */
public class MusicActivity extends BaseActivity<MusicContract.Presenter, MusicContract.View> implements MusicContract.View, View.OnClickListener, BTMusicListener, BTConnectStatusListener {

    private final int MSG_INIT_OK = 0;
    private final int MSG_UI_REFRESH_ID3_INFO = 1;
    private final int MSG_UI_REFRESH_PLAY_STATE = 2;
    private final int MSG_UI_LISTENER = 3;
    public static final byte CMD_UPDATE_PLAY_STATUS = (byte) 0x01;
    public static final byte CMD_UPDATE_PLAY_POSITION = (byte) 0x02;
    private BluetoothManager bluetoothManager;
    private int mDefaultMusicLong = 0; //00:00:00
    private int mDefaultPlayingTime = 0; //00:00:00
    private int musicTotalLength = 0;
    private int musicPlayingTime = 0;
    TextView tvPlayTime;
    TextView tvPlayTotalTime;
    SeekBar seekBar;
    ImageView ivPlay;
    MarqueeTextView tvTitle;
    MarqueeTextView tvAlbum;
    MarqueeTextView tvArtist;
    private boolean isPlay = false;
    private RegisterMediaSession registerMediaSession;
    private BluetoothRequestFocus bluetoothRequestFocus;
    private boolean isConnect;
    private boolean isResume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this);
        isConnect = bluetoothManager.isConnect();
        if (isConnect) {
            setContentView(R.layout.activity_music);
            bluetoothManager.registerBTMusicListener(this);
//            UIHandler.sendEmptyMessageDelayed(MSG_UI_LISTENER, 200);
            updateView();
        } else {
            setTheme(R.style.AlertDialogCustom);
            setContentView(R.layout.dialog_connect);
            findViewById(R.id.tv_connect_dialog_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClassName("com.jancar.settingss", "com.jancar.settings.view.activity.MainActivity");
                    intent.putExtra("position", 1);
                    startActivity(intent);
                    finish();
                }
            });
            findViewById(R.id.tv_connect_dialog_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }


    @Override
    protected void onStart() {
        bluetoothRequestFocus = BluetoothRequestFocus.getBluetoothRequestFocusStance(this);
        if (!bluetoothRequestFocus.isNeedGainFocus()) {
            bluetoothRequestFocus.requestAudioFocus();
        }
//        bluetoothManager.setPlayerState(true);
//        if (!isPlay) {
//            bluetoothManager.play();
//        }
        registerMediaSession = new RegisterMediaSession(this, bluetoothManager);
        registerMediaSession.requestMediaButton();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothManager.setBTConnectStatusListener(this);
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isPlay) {
//            bluetoothManager.pause();
//        }
//        bluetoothManager.unRegisterBTMusicListener();
        bluetoothRequestFocus.releaseAudioFocus();
        registerMediaSession.releaseMediaButton();

    }

    private void updateView() {
        tvPlayTime = (TextView) findViewById(R.id.tv_music_playTime);
        tvPlayTotalTime = (TextView) findViewById(R.id.tv_music_play_total_time);
        seekBar = (SeekBar) findViewById(R.id.seekbar_btmusic);
        ivPlay = (ImageView) findViewById(R.id.iv_music_play);
        tvTitle = (MarqueeTextView) findViewById(R.id.tv_music_title);
        tvAlbum = (MarqueeTextView) findViewById(R.id.tv_music_album);
        tvArtist = (MarqueeTextView) findViewById(R.id.tv_music_artist);
        findViewById(R.id.iv_music_pre).setOnClickListener(this);
        findViewById(R.id.iv_music_next).setOnClickListener(this);
        findViewById(R.id.iv_music_play).setOnClickListener(this);
        seekBar.setEnabled(false);
        tvTitle.enableMarquee(true);
        tvArtist.enableMarquee(true);
        tvAlbum.enableMarquee(true);
        updateMusicPlayingProgress(CMD_UPDATE_PLAY_STATUS, mDefaultMusicLong, mDefaultPlayingTime);
        updatePlayingStatus(mDefaultMusicLong, mDefaultPlayingTime, (byte) 0);

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
                    Log.e("MusicActivity", "start:blueMusic");
                    BluetoothMusicData blueMusic = (BluetoothMusicData) msg.obj;
                    updateMetadata(blueMusic.getTitle(), blueMusic.getArtist(), blueMusic.getAlbum());
                    Log.e("MusicActivity", "end:blueMusic");
                    break;
                case MSG_UI_REFRESH_PLAY_STATE:
                    BluetoothMusicData blueMusicStatus = (BluetoothMusicData) msg.obj;
                    updatePlaybackStatus(blueMusicStatus.getPlay_status(), blueMusicStatus.getSong_len(), blueMusicStatus.getSong_pos());
                    break;
                case MSG_UI_LISTENER:
                    bluetoothManager.registerBTMusicListener(MusicActivity.this);
                    UIHandler.sendEmptyMessageDelayed(MSG_UI_LISTENER, 200);
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
        Log.d("MusicActivity", "play_status:" + play_status);
        switch (play_status) {
            case BluetoothManager.MUSIC_STATE_PLAY:
                ivPlay.setImageResource(R.drawable.music_pause_selector);
                isPlay = true;
                if (!bluetoothRequestFocus.isNeedGainFocus() && isResume) {
                    bluetoothRequestFocus.requestAudioFocus();
                }
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

    @Override
    public void onNotifyBTMusicInitSuccess() {
        if (isConnect) {
            UIHandler.sendEmptyMessage(MSG_INIT_OK);
        }

    }

    @Override
    public void onNotifyBTMusicID3Info(final BluetoothMusicData bluetoothMusicData) {
        Log.d("MusicActivity", "onNotifyBTMusicID3Info:" + isConnect);
        if (isConnect) {
            Message msg = UIHandler.obtainMessage();
            msg.what = MSG_UI_REFRESH_ID3_INFO;
            msg.obj = bluetoothMusicData;
            msg.sendToTarget();
        }

    }

    @Override
    public void onNotifyBTMusicPlayState(final BluetoothMusicData bluetoothMusicData) {
        Log.d("MusicActivity", "onNotifyBTMusicPlayState:" + isConnect);
        if (isConnect) {
            Message msg = UIHandler.obtainMessage();
            msg.what = MSG_UI_REFRESH_PLAY_STATE;
            msg.obj = bluetoothMusicData;
            msg.sendToTarget();
        }

    }

    @Override
    public void onClick(View view) {
        if (!bluetoothManager.isConnect()) {
            ToastUtil.ShowToast(MusicActivity.this, getString(R.string.tv_bt_connect_is_close));
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
    public void onNotifyBTConnectStateChange(byte state) {
        if (state != BluetoothManager.BT_CONNECT_IS_CONNECTED) {
            bluetoothRequestFocus.releaseAudioFocus();
            isPlay = false;
//            isConnect = false;
        } else {
//            isConnect = true;
            Message msg = handler.obtainMessage();
            msg.what = state;
            handler.sendMessage(msg);
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BluetoothManager.BT_CONNECT_IS_CONNECTED) {
                finish();
            }
        }
    };

}
