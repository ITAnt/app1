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

import com.jancar.JancarManager;
import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.Listener.BTMusicListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.lib.BluetoothMusicData;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.contract.MusicContract;
import com.jancar.bluetooth.phone.presenter.MusicPresenter;
import com.jancar.bluetooth.phone.util.ApplicationUtil;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.util.TimeUtil;
import com.jancar.bluetooth.phone.util.ToastUtil;
import com.jancar.bluetooth.phone.widget.ConnectDialog;
import com.jancar.bluetooth.phone.widget.MarqueeTextView;
import com.jancar.key.KeyDef;
import com.jancar.key.keyFocuser;
import com.ui.mvp.view.BaseActivity;

import static com.jancar.key.KeyDef.KeyAction.KEY_ACTION_UP;


/**
 * @author Tzq
 * @date 2018-9-4 16:00:22
 */
public class MusicActivity extends BaseActivity<MusicContract.Presenter, MusicContract.View> implements MusicContract.View, View.OnClickListener, BTMusicListener, BTConnectStatusListener, BluetoothRequestFocus.BackCarListener {

    private static final String TAG = "MusicActivity";
    private static final int MSG_INIT_OK = 0;
    private static final int MSG_UI_REFRESH_ID3_INFO = 1;
    private static final int MSG_UI_REFRESH_PLAY_STATE = 2;
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
    //    CircleImageView circleImageView;
    private boolean isPlay = false;
    //    private RegisterMediaSession registerMediaSession;
    private BluetoothRequestFocus bluetoothRequestFocus;
    private boolean isConnect;
    private boolean isResume;
    private ConnectDialog connectDialog;
    private int saveConnect = Constants.BT_CONNECT_IS_NONE;

    private JancarManager jancarManager;
    public ToastUtil mToast;
    keyFocuser keyFocusListener = new keyFocuser() {
        @Override
        public boolean OnKeyEvent(int key, int state) {
            boolean bRet = true;
            try {
                KeyDef.KeyType keyType = KeyDef.KeyType.nativeToType(key);
                KeyDef.KeyAction keyAction = KeyDef.KeyAction.nativeToType(state);
                if (keyAction == KEY_ACTION_UP) {
                    switch (keyType) {
                        case KEY_PREV:
                            bluetoothManager.prev();
                            break;
                        case KEY_NEXT:
                            bluetoothManager.next();
                            break;
                        case KEY_PAUSE:
                            Log.e(TAG, "KEY_PAUSE===");
                            bluetoothManager.pause();
                            break;
                        case KEY_PLAY:
                            bluetoothManager.play();
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

        @Override
        public void OnKeyFocusChange(int i) {

        }
    };

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate===");
        setContentView(R.layout.activity_music);
        mToast = new ToastUtil(this);
        initView();
        jancarManager = (JancarManager) getSystemService(JancarManager.JAC_SERVICE);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart===");
        super.onStart();
        jancarManager.requestKeyFocus(keyFocusListener.asBinder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume===");
        registerListener();
        BluetoothRequestFocus.HandPaused = false;
        isConnect = bluetoothRequestFocus.isBTConnect();
        isResume = true;
        if (!isConnect) {
            showDialog();
        } else {
            saveConnect = Constants.BT_CONNECT_IS_CONNECTED;
            if (connectDialog != null && connectDialog.isShowing()) {
                connectDialog.dismiss();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause===");
        isResume = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop===");
//        BluetoothRequestFocus.HandPaused = false;
    }

    @Override
    protected void onDestroy() {
        if (connectDialog != null && connectDialog.isShowing()) {
            connectDialog.dismiss();
        }
        if (mToast != null) {
            mToast.Cancel();
        }
        super.onDestroy();
        Log.e(TAG, "onDestroy===");
        BluetoothRequestFocus.HandPaused = false;
        bluetoothRequestFocus.setBackCarListener(null);
        bluetoothRequestFocus.releaseAudioFocus();
//        registerMediaSession.releaseMediaButton();
        bluetoothManager.unRegisterBTMusicListener();
        bluetoothManager.setBTConnectStatusListener(null);
        jancarManager.abandonKeyFocus(keyFocusListener.asBinder());
    }

    private void initView() {
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this);
//        bluetoothManager.registerBTMusicListener(this);
        findView();
    }

    private void showDialog() {
        if (connectDialog == null) {
            connectDialog = new ConnectDialog(this, R.style.AlertDialogCustom);
            connectDialog.setCanelOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectDialog.dismiss();
                }
            });
            connectDialog.go2SettingOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    go2Setting();
                }
            });
            connectDialog.setCanceledOnTouchOutside(false);
            connectDialog.setCancelable(false);
            connectDialog.show();
        }
    }

    private void go2Setting() {
        if (ApplicationUtil.hasApplication(this, Constants.PACKNAME)) {
            Intent intent = new Intent();
            intent.setClassName(Constants.PACKNAME, Constants.CLASSNAME);
            intent.putExtra(Constants.SETTING_POSITION, Constants.SETTING_POSITION_NUM);
            startActivity(intent);
        }
    }

    private void registerListener() {
        bluetoothRequestFocus = BTUIService.bluetoothRequestFocus;
//        registerMediaSession = BTUIService.registerMediaSession;
        if (bluetoothRequestFocus == null) {
            bluetoothRequestFocus = BluetoothRequestFocus.getBluetoothRequestFocusStance(this);
        }
//        if (registerMediaSession == null) {
//            registerMediaSession = new RegisterMediaSession(this, bluetoothManager);
//        }
//        Log.e("MusicActivity", "registerMediaSession===" + registerMediaSession);
//        registerMediaSession.requestMediaButton();
        bluetoothManager.registerBTMusicListener(this);

        bluetoothManager.setBTConnectStatusListener(this);
        bluetoothRequestFocus.setBackCarListener(this);
        Log.e(TAG, "registerListenerFocus()===" + bluetoothRequestFocus.isNeedGainFocus());
        if (!bluetoothRequestFocus.isNeedGainFocus()) {
            bluetoothRequestFocus.requestAudioFocus();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent===");
    }

    private void findView() {
        tvPlayTime = (TextView) findViewById(R.id.tv_music_playTime);
        tvPlayTotalTime = (TextView) findViewById(R.id.tv_music_play_total_time);
        seekBar = (SeekBar) findViewById(R.id.seekbar_btmusic);
        ivPlay = (ImageView) findViewById(R.id.iv_music_play);
        tvTitle = (MarqueeTextView) findViewById(R.id.tv_music_title);
        tvAlbum = (MarqueeTextView) findViewById(R.id.tv_music_album);
        tvArtist = (MarqueeTextView) findViewById(R.id.tv_music_artist);
//        circleImageView = (CircleImageView) findViewById(R.id.iv_music_rotating);
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
                    Log.e(TAG, "MSG_INIT_OK===");
                    BluetoothMusicData bluetoothMusicData = bluetoothManager.getBlueMusicData();
                    updateMetadata(bluetoothMusicData.getTitle(), bluetoothMusicData.getArtist(), bluetoothMusicData.getAlbum());
                    updatePlaybackStatus(bluetoothMusicData.getPlay_status(), bluetoothMusicData.getSong_len(), bluetoothMusicData.getSong_pos());
                    break;
                case MSG_UI_REFRESH_ID3_INFO:
                    Log.e(TAG, "MSG_UI_REFRESH_ID3_INFO===");
                    BluetoothMusicData blueMusic = (BluetoothMusicData) msg.obj;
                    updateMetadata(blueMusic.getTitle(), blueMusic.getArtist(), blueMusic.getAlbum());
                    break;
                case MSG_UI_REFRESH_PLAY_STATE:
                    Log.e(TAG, "MSG_UI_REFRESH_PLAY_STATE===");
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
        Log.e(TAG, "updatePlaybackStatus===" + play_status);
        int btStatus = bluetoothRequestFocus.getCurrentBTStatus();
        switch (play_status) {
            case BluetoothManager.MUSIC_STATE_PLAY:
                isPlay = true;
                ivPlay.setImageResource(R.drawable.music_pause_selector);
//                circleImageView.setAnimatePlaying(isPlay);
                Log.e(TAG, "MUSIC_STATE_PLAY===" + "isPlay==" + isPlay);
                if (!bluetoothRequestFocus.getPlayStatus() && !BluetoothRequestFocus.CarState && isResume) {
                    Log.e(TAG, "BTPlay===");
                    bluetoothRequestFocus.setBTPlayStatus(true);
                }
                Log.e(TAG, "play===btStatus===" + btStatus);
                switch (btStatus) {
                    case BluetoothRequestFocus.BT_INIT:
                    case BluetoothRequestFocus.BT_FOCUSE_GAIN:
                    case BluetoothRequestFocus.BT_FOCUSE_LOSS:
                    case BluetoothRequestFocus.BT_FOCUSE_LOSS_TRANSIENT:
                    case BluetoothRequestFocus.BT_FOCUSE_TRANSIENT_CAN_DUCK:
                        break;
                    default:
                        bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_NONE);
                        BluetoothRequestFocus.HandPaused = false;
                        break;
                }
                break;
            case BluetoothManager.MUSIC_STATE_PAUSE:
            case BluetoothManager.MUSIC_STATE_STOP:
                isPlay = false;
                ivPlay.setImageResource(R.drawable.music_play_selector);
//                circleImageView.setAnimatePlaying(isPlay);
                Log.e(TAG, "pause===btStatus===" + btStatus);
                switch (btStatus) {
                    case BluetoothRequestFocus.BT_INIT:
                        if (bluetoothRequestFocus.isBTConnect()) {
                            Log.e(TAG, "Play====BT_INIT=====");
                            bluetoothRequestFocus.btMusicPlay();
                            bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
                        }
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_GAIN:
                        Log.e(TAG, "BT_FOCUSE_GAIN=======" + BluetoothRequestFocus.HandPaused);
                        if (bluetoothRequestFocus.isBTConnect() && !BluetoothRequestFocus.HandPaused) {
                            bluetoothRequestFocus.btMusicPlay();
                            bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
                        }
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_LOSS:
                        Log.e("Finish", "BT_FOCUSE_LOSS===");
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);

                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_LOSS_TRANSIENT:
                        Log.e(TAG, "BT_FOCUSE_LOSS_TRANSIENT===");
                        break;
                    case BluetoothRequestFocus.BT_FOCUSE_TRANSIENT_CAN_DUCK:
                        Log.e(TAG, "BT_FOCUSE_TRANSIENT_CAN_DUCK===");
                        break;
                    case BluetoothRequestFocus.BT_NONE:
                        Log.e(TAG, "BT_NONE===");
                        BluetoothRequestFocus.HandPaused = true;
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
        Log.e(TAG, "onNotifyBTMusicInitSuccess===");
        UIHandler.sendEmptyMessage(MSG_INIT_OK);


    }

    @Override
    public void onNotifyBTMusicID3Info(final BluetoothMusicData bluetoothMusicData) {
        Log.e(TAG, "onNotifyBTMusicID3Info===");
        Message msg = UIHandler.obtainMessage();
        msg.what = MSG_UI_REFRESH_ID3_INFO;
        msg.obj = bluetoothMusicData;
        msg.sendToTarget();


    }

    @Override
    public void onNotifyBTMusicPlayState(final BluetoothMusicData bluetoothMusicData) {
        Log.e(TAG, "onNotifyBTMusicPlayState====");
        Message msg = UIHandler.obtainMessage();
        msg.what = MSG_UI_REFRESH_PLAY_STATE;
        msg.obj = bluetoothMusicData;
        msg.sendToTarget();

    }

    @Override
    public void onClick(View view) {
        if (!bluetoothRequestFocus.isBTConnect()) {
            mToast.ShowTipText(MusicActivity.this, getString(R.string.tv_bt_connect_is_close));
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
                    Log.e(TAG, "Play====music_play=====");
                    bluetoothRequestFocus.btMusicPause();
                    BluetoothRequestFocus.HandPaused = true;
                } else {
                    bluetoothRequestFocus.btMusicPlay();
                    BluetoothRequestFocus.HandPaused = false;
                    if (!bluetoothRequestFocus.getPlayStatus()) {
                        bluetoothRequestFocus.setBTPlayStatus(true);
                    }
                }
//                circleImageView.setAnimatePlaying(isPlay);
                break;
        }

    }

    @Override
    public void onNotifyBTConnectStateChange(byte state) {
        Log.e(TAG, "onNotifyBTConnectStateChange===");
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
                        if (connectDialog != null && connectDialog.isShowing()) {
                            connectDialog.dismiss();
                        }
                        if (isResume) {
                            Log.e(TAG, "Constants.BT_CONNECT_IS_CONNECTED===");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!BluetoothRequestFocus.HandPaused) {
                                isPlay = true;
                                bluetoothRequestFocus.btMusicPlay();
                            }
//                            circleImageView.setAnimatePlaying(isPlay);
                        }
                        saveConnect = obj;
                    } else if (obj != Constants.BT_CONNECT_IS_CONNECTED && saveConnect == Constants.BT_CONNECT_IS_CONNECTED) {
                        Log.e(TAG, "BT_CONNECT_IS_NONE===" + bluetoothRequestFocus.getPlayStatus());
                        saveConnect = obj;
                        isPlay = false;
//                        circleImageView.setAnimatePlaying(isPlay);
                        if (bluetoothRequestFocus.getPlayStatus()) {
                            bluetoothRequestFocus.setBTPlayStatus(false);
                        }
                        bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);

                    }
                    break;
            }
        }
    };

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
        Log.e(TAG, "onWindowAttributesChanged===");

    }

    @Override
    public void onNotifyBackCarStop() {
        Log.e(TAG, "onNotifyBackCarStop===");
//        isResume = true;
//        if (isResume) {
//            bluetoothManager.setBTConnectStatusListener(this);
//            bluetoothManager.registerBTMusicListener(this);
//            isConnect = bluetoothRequestFocus.isBTConnect();
//            if (!isConnect) {
//                showDialog();
//            } else {
//                saveConnect = Constants.BT_CONNECT_IS_CONNECTED;
//                if (connectDialog != null && connectDialog.isShowing()) {
//                    connectDialog.dismiss();
//                }
//                Log.e(TAG, "onNotifyBackCarStop.CallState===" + BluetoothRequestFocus.CallState);
//                if (!BluetoothRequestFocus.HandPaused && BluetoothRequestFocus.CallState == 0) {
//                    Log.e(TAG, "Play====BackCarStop=====");
//                    bluetoothRequestFocus.btMusicPlay();
//                }
//                bluetoothRequestFocus.setCurrentBTStatus(BluetoothRequestFocus.BT_IDL);
//            }
//        }
    }

    @Override
    public void onNotifyBackCarStart() {
        Log.e(TAG, "onNotifyBackCarStart=====");
//        isResume = false;
    }


    @Override
    public void onNotifyActivityFinish() {
        Log.e(TAG, "onNotifyActivityFinish===");
        this.finish();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory===");
    }
}
