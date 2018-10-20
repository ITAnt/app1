package com.jancar.bluetooth.phone.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.jancar.bluetooth.phone.widget.CircleImageView;
import com.jancar.bluetooth.phone.widget.ConnectDialog;
import com.jancar.bluetooth.phone.widget.MarqueeTextView;
import com.orhanobut.hawk.Hawk;
import com.ui.mvp.view.BaseActivity;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:22
 */
public class MusicActivity extends BaseActivity<MusicContract.Presenter, MusicContract.View> implements MusicContract.View, View.OnClickListener, BTMusicListener, BTConnectStatusListener, BluetoothRequestFocus.BackCarListener {

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
    CircleImageView circleImageView;
    private boolean isPlay = false;
    private RegisterMediaSession registerMediaSession;
    private BluetoothRequestFocus bluetoothRequestFocus;
    private boolean isConnect;
    private boolean isResume;
    private ConnectDialog connectDialog;
    private int saveConnect = Constants.BT_CONNECT_IS_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();
    }

    private void initView() {
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this);
        bluetoothManager.registerBTMusicListener(this);
        if (connectDialog == null) {
            connectDialog = new ConnectDialog(this, R.style.AlertDialogCustom);
        }
        findView();
    }

    @Override
    protected void onStart() {
        Log.e("MusicActivity", "onStart===");
        bluetoothRequestFocus = BTUIService.bluetoothRequestFocus;
        registerMediaSession = BTUIService.registerMediaSession;
        if (bluetoothRequestFocus == null) {
            bluetoothRequestFocus = BluetoothRequestFocus.getBluetoothRequestFocusStance(this);
        }
        if (registerMediaSession == null) {
            registerMediaSession = new RegisterMediaSession(this, bluetoothManager);
        }
        if (!bluetoothRequestFocus.isNeedGainFocus()) {
            bluetoothRequestFocus.requestAudioFocus();
        }
        bluetoothRequestFocus.setBackCarListener(this);
        registerMediaSession.requestMediaButton();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MusicActivity", "onResume===");
        bluetoothManager.setBTConnectStatusListener(this);
        bluetoothManager.registerBTMusicListener(this);
        isConnect = bluetoothRequestFocus.isBTConnect();
        isResume = true;
        if (!isConnect) {
            showDialog();
        } else {
            saveConnect = Constants.BT_CONNECT_IS_CONNECTED;
            if (connectDialog.isShowing()) {
                connectDialog.dismiss();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("MusicActivity", "onNewIntent===");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("MusicActivity", "onSaveInstanceState===");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("MusicActivity", "onRestoreInstanceState===");
    }

    private void showDialog() {
        connectDialog.setCanelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDialog.dismiss();
            }
        });
        connectDialog.go2SettingOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.jancar.settingss", "com.jancar.settings.view.activity.MainActivity");
                intent.putExtra("position", 1);
                startActivity(intent);
            }
        });
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.setCancelable(false);
        connectDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MusicActivity", "onPause===");
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MusicActivity", "onDestroy===");
        bluetoothRequestFocus.HandPaused = true;
        bluetoothRequestFocus.releaseAudioFocus();
        registerMediaSession.releaseMediaButton();

    }

    private void findView() {
        tvPlayTime = (TextView) findViewById(R.id.tv_music_playTime);
        tvPlayTotalTime = (TextView) findViewById(R.id.tv_music_play_total_time);
        seekBar = (SeekBar) findViewById(R.id.seekbar_btmusic);
        ivPlay = (ImageView) findViewById(R.id.iv_music_play);
        tvTitle = (MarqueeTextView) findViewById(R.id.tv_music_title);
        tvAlbum = (MarqueeTextView) findViewById(R.id.tv_music_album);
        tvArtist = (MarqueeTextView) findViewById(R.id.tv_music_artist);
        circleImageView = (CircleImageView) findViewById(R.id.iv_music_rotating);
//        circleImageView.setImageResource(R.drawable.iv_music_rotating);
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
                    Log.e("MusicActivity", "MSG_INIT_OK===");
                    BluetoothMusicData bluetoothMusicData = bluetoothManager.getBlueMusicData();
                    updateMetadata(bluetoothMusicData.getTitle(), bluetoothMusicData.getArtist(), bluetoothMusicData.getAlbum());
                    updatePlaybackStatus(bluetoothMusicData.getPlay_status(), bluetoothMusicData.getSong_len(), bluetoothMusicData.getSong_pos());
                    break;
                case MSG_UI_REFRESH_ID3_INFO:
                    Log.e("MusicActivity", "MSG_UI_REFRESH_ID3_INFO===");
                    BluetoothMusicData blueMusic = (BluetoothMusicData) msg.obj;
                    updateMetadata(blueMusic.getTitle(), blueMusic.getArtist(), blueMusic.getAlbum());
                    break;
                case MSG_UI_REFRESH_PLAY_STATE:
                    Log.e("MusicActivity", "MSG_UI_REFRESH_PLAY_STATE===");
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
        if (TextUtils.isEmpty(title)) {
            return;
        }
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
        Log.d("MusicActivity", "updatePlaybackStatus===" + play_status);
        switch (play_status) {
            case BluetoothManager.MUSIC_STATE_PLAY:
                Log.e("MusicActivity", "MUSIC_STATE_PLAY===");
                ivPlay.setImageResource(R.drawable.music_pause_selector);
                isPlay = true;
                circleImageView.setAnimatePlaying(isPlay);
                Log.e("MusicActivity", "updatePlaybackStatus===" + BluetoothRequestFocus.CarState + "===" + bluetoothRequestFocus.getPlayStatus());
                if (!bluetoothRequestFocus.getPlayStatus() && !BluetoothRequestFocus.CarState) {
                    Log.e("MusicActivity", "BluetoothRequestFocus.CarState===" + BluetoothRequestFocus.CarState);
                    bluetoothRequestFocus.setBTPlayStatus(true);
                }
                break;
            case BluetoothManager.MUSIC_STATE_PAUSE:
            case BluetoothManager.MUSIC_STATE_STOP:
                Log.e("MusicActivity", "MUSIC_STATE_PAUSE===");
                ivPlay.setImageResource(R.drawable.music_play_selector);
                isPlay = false;
                circleImageView.setAnimatePlaying(isPlay);
                switch (bluetoothRequestFocus.getCurrentBTStatus()) {
                    case BluetoothRequestFocus.BT_INIT:
                        Log.e("MusicActivity", "BT_INIT===");
                        if (bluetoothRequestFocus.isBTConnect()) {
                            bluetoothRequestFocus.btMusicPlay();
                            bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
                        }
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_GAIN:
                        Log.e("MusicActivity", "BT_FOCUSE_GAIN===");
                        if (bluetoothRequestFocus.isBTConnect()) {
                            bluetoothRequestFocus.btMusicPlay();
                            bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
                        }
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_LOSS:
                        Log.e("MusicActivity", "BT_FOCUSE_LOSS===");
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
                        finish();
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_LOSS_TRANSIENT:
                        Log.e("MusicActivity", "BT_FOCUSE_LOSS_TRANSIENT===");
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_TRANSIENT_CAN_DUCK:
                        Log.e("MusicActivity", "BT_FOCUSE_TRANSIENT_CAN_DUCK===");
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        break;
                    case BluetoothRequestFocus.BT_NONE:
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        Log.e("MusicActivity", "BT_NONE===");
                        break;
                }
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
        Log.e("MusicActivity", "onNotifyBTMusicInitSuccess===");
        UIHandler.sendEmptyMessage(MSG_INIT_OK);


    }

    @Override
    public void onNotifyBTMusicID3Info(final BluetoothMusicData bluetoothMusicData) {
        Log.e("MusicActivity", "onNotifyBTMusicID3Info===");
        Message msg = UIHandler.obtainMessage();
        msg.what = MSG_UI_REFRESH_ID3_INFO;
        msg.obj = bluetoothMusicData;
        msg.sendToTarget();


    }

    @Override
    public void onNotifyBTMusicPlayState(final BluetoothMusicData bluetoothMusicData) {
        Log.e("MusicActivity", "onNotifyBTMusicPlayState====");
        Message msg = UIHandler.obtainMessage();
        msg.what = MSG_UI_REFRESH_PLAY_STATE;
        msg.obj = bluetoothMusicData;
        msg.sendToTarget();

    }

    @Override
    public void onClick(View view) {
        if (!bluetoothRequestFocus.isBTConnect()) {
            ToastUtil.ShowToast(MusicActivity.this, getString(R.string.tv_bt_connect_is_close));
            return;
        }
        switch (view.getId()) {
            case R.id.iv_music_pre:
                bluetoothRequestFocus.btMusicPre();
                break;
            case R.id.iv_music_next:
                bluetoothRequestFocus.btMusicNext();
                break;
            case R.id.iv_music_play:
                if (isPlay) {
                    Log.e("MusicActivity", "onClick111===:" + isPlay);
                    bluetoothRequestFocus.btMusicPause();
                    bluetoothRequestFocus.HandPaused = true;
                } else {
                    Log.e("MusicActivity", "onClick222===:" + isPlay);
                    bluetoothRequestFocus.btMusicPlay();
                    if (!bluetoothRequestFocus.getPlayStatus()) {
                        bluetoothRequestFocus.setBTPlayStatus(true);
                    }
                }
                circleImageView.setAnimatePlaying(isPlay);
                break;
        }

    }

    @Override
    public void onNotifyBTConnectStateChange(byte state) {
        Log.e("MusicActivity", "onNotifyBTConnectStateChange===");
        Message message = new Message();
        message.what = Constants.CONTACT_BT_CONNECT;
        message.obj = state;
        handler.sendMessage(message);

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CONTACT_BT_CONNECT:
                    //蓝牙状态
                    byte obj = (byte) msg.obj;
                    if (obj == Constants.BT_CONNECT_IS_CONNECTED) {
                        if (connectDialog.isShowing()) {
                            connectDialog.dismiss();
                        }
                        Log.e("MusicActivity", "Constants.BT_CONNECT_IS_CONNECTED===");
                        if (isResume) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            bluetoothRequestFocus.btMusicPlay();
                            isPlay = true;
                            circleImageView.setAnimatePlaying(isPlay);
                        }
                        saveConnect = obj;
                    } else if (obj != Constants.BT_CONNECT_IS_CONNECTED && saveConnect == Constants.BT_CONNECT_IS_CONNECTED) {
                        Log.e("MusicActivity", "releaseAudioFocusStatus===" + bluetoothRequestFocus.getPlayStatus());
                        saveConnect = obj;
                        isPlay = false;
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        circleImageView.setAnimatePlaying(isPlay);
                    }
                    break;
            }
        }
    };

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
        Log.e("MusicActivity", "onWindowAttributesChanged===");

    }

    @Override
    public void onNotifyBackCarStop() {
        Log.e("MusicActivity", "onNotifyBackCarStop===");
        isResume = true;
        if (isResume) {
            bluetoothManager.setBTConnectStatusListener(this);
            bluetoothManager.registerBTMusicListener(this);
            isConnect = bluetoothRequestFocus.isBTConnect();
            isResume = true;
            if (!isConnect) {
                showDialog();
            } else {
                saveConnect = Constants.BT_CONNECT_IS_CONNECTED;
                if (connectDialog.isShowing()) {
                    connectDialog.dismiss();
                }
                bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
                Log.e("MusicActivity", "BluetoothRequestFocus.CallState===" + BluetoothRequestFocus.CallState);
                if (!BluetoothRequestFocus.HandPaused && BluetoothRequestFocus.CallState == 0) {
                    Log.e("MusicActivity", "onNotifyBackCarStop===CallState");
                    bluetoothRequestFocus.btMusicPlay();
                }
            }
        }
    }

    @Override
    public void onNotifyBackCarStart() {
        Log.e("MusicActivity", "onNotifyBackCarStart=====");
        isResume = false;
    }
}
