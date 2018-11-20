package com.jancar.settings.view.fragment;import android.app.Dialog;import android.app.Fragment;import android.content.Context;import android.content.Intent;import android.content.pm.PackageInfo;import android.content.pm.PackageManager;import android.graphics.Bitmap;import android.os.Bundle;import android.support.annotation.NonNull;import android.support.annotation.Nullable;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.RelativeLayout;import android.widget.TextView;import android.widget.Toast;import com.jancar.settings.R;import com.jancar.settings.lib.SettingManager;import com.jancar.settings.listener.Contract.OnContractImpl;import com.jancar.settings.listener.Contract.SoundContractImpl;import com.jancar.settings.listener.IPresenter;import com.jancar.settings.manager.BaseFragments;import com.jancar.settings.presenter.OnPresenter;import com.jancar.settings.presenter.SoundPresenter;import com.jancar.settings.util.QRCodeUtil;import com.jancar.settings.view.activity.MainActivity;import static com.jancar.settings.util.Tool.setDialogParam;/** * Created by ouyan on 2018/9/11. */public class OnFragment extends BaseFragments<OnPresenter> implements OnContractImpl.View, View.OnClickListener {    private View view;    private TextView systemVersionNumberSummaryTxt;    private TextView MCUVersionNumberSummaryTxt;    private TextView CANersionNumberSummaryTxt;    private TextView androidVersionSummaryTxt;    private TextView kernelVersionSummaryTxt;    private TextView upgradeUpdateTxt;    private TextView identifierTxt;    private RelativeLayout identifierRlayout;    private RelativeLayout upgradeUpdateRlayout;    private SettingManager settingManager;    @Override    public void initView(@Nullable Bundle savedInstanceState) {        if (view != null) {            systemVersionNumberSummaryTxt = (TextView) view.findViewById(R.id.txt_system_version_number_summary);            MCUVersionNumberSummaryTxt = (TextView) view.findViewById(R.id.txt_MCU_version_number_summary);            CANersionNumberSummaryTxt = (TextView) view.findViewById(R.id.txt_CAN_version_number_summary);            androidVersionSummaryTxt = (TextView) view.findViewById(R.id.txt_android_version_summary);            kernelVersionSummaryTxt = (TextView) view.findViewById(R.id.txt_kernel_version_summary);            upgradeUpdateTxt = (TextView) view.findViewById(R.id.txt_upgrade_update);            identifierTxt = (TextView) view.findViewById(R.id.txt_identifier);            identifierRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_identifier);            upgradeUpdateRlayout = (RelativeLayout) view.findViewById(R.id.rlayout_upgrade_update);        }    }    @Override    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {        view = inflater.inflate(R.layout.fragment_on, null);        initView(savedInstanceState);        initData(savedInstanceState);        return view;    }    @Override    public void onDestroy() {        super.onDestroy();        view=null;        Log.w("OnFragment","onDestroy");    }    @Override    public int initResid() {        return 0;    }    @Override    public IPresenter initPresenter() {        return new OnPresenter(this);    }    @Override    public void initData(@Nullable Bundle savedInstanceState) {     //settingManager=   ((MainActivity)getActivity()).getSettingManager();       settingManager = SettingManager.getSettingManager(this.getContext());        systemVersionNumberSummaryTxt.setText(settingManager.getSystemCustomVersion());        MCUVersionNumberSummaryTxt.setText(settingManager.getMcuVersion());        kernelVersionSummaryTxt.setText(settingManager.getKernelVersion());        androidVersionSummaryTxt.setText(settingManager.getSystemVersion());        identifierRlayout.setOnClickListener(this);        identifierTxt.setText(settingManager.getSerialNumber());        upgradeUpdateRlayout.setOnClickListener(this);        try {            upgradeUpdateTxt.setText(settingManager.getVersionName());        } catch (Exception e) {            e.printStackTrace();        }        Log.w("packageCode",packageCode(getContext()));    }    public static String packageCode(Context context) {        PackageManager manager = context.getPackageManager();        String code = "";        try {            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);            code = info.versionName;        } catch (PackageManager.NameNotFoundException e) {            e.printStackTrace();        }        return code;    }    @Override    public void setData(@Nullable Object data) {    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.rlayout_identifier:                showDialog();                break;            case R.id.rlayout_upgrade_update:                Intent intent = new Intent();                intent.setClassName("com.jancar.upgrade", "com.jancar.upgrade.MainActivity");                startActivity(intent);                break;        }    }    private void showDialog() {        final Dialog dialog = new Dialog(getContext(), R.style.record_voice_dialogs);        dialog.setContentView(R.layout.display_dialog_on);        ImageView dialogImg = (ImageView) dialog.findViewById(R.id.img_dialog);        dialog.setCanceledOnTouchOutside(true);        dialog.setCancelable(true);        String vCardString = settingManager.getSerialNumber();        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(vCardString, 480, 480);        dialogImg.setImageBitmap(mBitmap);        dialogImg.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                dialog.dismiss();            }        });    /*    VCard vCard = VCardParser.parse(vCardString);        vCard.setNote("vCard generate and modified!");        vCard.addTelephone("+39 3486454314");        String vCardcontent = vCard.buildString();        //sample using QrGen to generate QrCode bitmap        dialogImg.setImageBitmap(QRCode.from(meCardcontent).bitmap());*/        dialog.show();        setDialogParam(dialog, 324, 324);    }}