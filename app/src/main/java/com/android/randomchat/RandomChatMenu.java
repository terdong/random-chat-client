package com.android.randomchat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


public class RandomChatMenu extends Activity {
    private static final String TAG="RandomChatMenu";
    private final Handler handler = new Handler();
    boolean bool=true;
    Button bt;
    ImageView img;
    AnimationDrawable mframeAnimation=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu);
        img = (ImageView)findViewById(R.id.iv);
        
    }

    public void onClick(View v) {
        if(v==bt) {
            if(bool) {
                Log.d(TAG,"startAnimation");
                handler.post(new Runnable() {
                    public void run() {
                        startAnimation();
                    }
                });
                bool=false;
            }
            else if(!bool) {
                Log.d(TAG,"stopAnimation");
                handler.post(new Runnable() {
                    public void run() {
                        stopAnimation();                        
                    }
                });
                bool=true;
            }
        }
    }

    private void stopAnimation() {
        mframeAnimation.stop();
        mframeAnimation.setVisible(false, false);
    }

    private void startAnimation() {
        
        img.setBackgroundResource(R.anim.ani_touch);
        mframeAnimation = (AnimationDrawable)img.getBackground();
        mframeAnimation.start();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG,"Call");
        startAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int tE = event.getAction();
        switch (tE) {
            case MotionEvent.ACTION_DOWN:
                Intent room = new Intent(this,RandomChat.class);
                startActivity(room);
                stopAnimation();
                this.finish();
                break;
            default:
                break;
        }
        
        return true;
    }

}













