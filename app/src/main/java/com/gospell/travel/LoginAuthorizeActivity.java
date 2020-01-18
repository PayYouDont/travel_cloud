package com.gospell.travel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.gospell.travel.helper.ReplaceViewHelper;
import com.gospell.travel.ui.util.ViewUtil;
import com.gospell.travel.ui.view.CustomButton;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class LoginAuthorizeActivity extends AppCompatActivity {
    private ImageView qrImage;
    private Button exitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login_authorize);
        getSupportActionBar ().hide ();
        qrImage = findViewById (R.id.qr_img);
        exitBtn = findViewById (R.id.exit_btn);
        setExitBtn();
        setQrImage ("test");
    }
    private void setQrImage(String content){
        Bitmap bitmap = CodeCreator.createQRCode(content, 150, 150, null);
        qrImage.setImageBitmap (bitmap);
    }
    private void setExitBtn(){
        CustomButton button = new CustomButton (this, CustomButton.Type.success);
        button.setText (exitBtn.getText ());
        button.setTextColor (Color.parseColor ("#1BCC87"));
        button.setBackgroundColor (Color.WHITE);
        button.setOnClickListener (v -> gotoMainActivity());
        button.setTextSize (16);
        button.setPressedColor (Color.GRAY);
        ReplaceViewHelper helper = new ReplaceViewHelper (this);
        helper.toReplaceView (exitBtn,button);
    }
    private void gotoMainActivity(){
        Intent intent = new Intent (this,MainActivity.class);
        startActivity (intent);
    }
}
