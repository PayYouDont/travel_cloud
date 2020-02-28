package com.gospell.travel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gospell.travel.common.util.NfcUtils;

public class NFCActivity extends AppCompatActivity {
    private NfcUtils nfcUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_nfc);
        getSupportActionBar ().hide ();
        getWindow ().setStatusBarColor (getResources ().getColor (R.color.colorStatusBar,null));
        nfcUtils = new NfcUtils (this);
        LinearLayout backView = findViewById (R.id.custom_nav_back);
        backView.setOnClickListener (v -> onBackPressed ());

    }

    @Override
    protected void onPostResume() {
        super.onPostResume ();
        if(nfcUtils.mNfcAdapter!=null){
            nfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //关闭前台调度系统
        if(nfcUtils.mNfcAdapter!=null){
            nfcUtils.mNfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取NFC数据
        try {
            String id = NfcUtils.readNFCId (intent);
            System.out.println ("nfcId:"+id);
            if(!"".equals (id)){
                NfcUtils.writeNFCToTag ("gospell",intent);
                System.out.println (NfcUtils.readNFCFromTag (intent));
            }
            String str = NfcUtils.readNFCFromTag (intent);
            Toast.makeText (this,str,Toast.LENGTH_SHORT).show ();
            System.out.println ("识别到NFC,Id:"+id+",内容为:"+str);
        }catch (Exception e){
            e.printStackTrace ();
        }
    }

}
