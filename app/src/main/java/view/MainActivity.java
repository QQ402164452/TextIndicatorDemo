package view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import custom.LetterView;
import com.example.jason.textindicatordemo.R;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity {
    private LetterView letterView;
    private PopupWindow popupWindow;
    private TextView letterText;

    private MainHandler handler;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        letterView= (LetterView) findViewById(R.id.MainActivity_LetterView);

        View popupView=getLayoutInflater().inflate(R.layout.popupwindow_layout,null);
        letterText= (TextView) popupView.findViewById(R.id.MainActivity_LetterText);

        popupWindow=new PopupWindow(popupView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
    }

    @Override
    protected void initData() {
        handler=new MainHandler(this);
    }

    @Override
    protected void initListener() {
        letterView.setListener(new LetterView.LetterOnClickListener() {
            @Override
            public void onItemClickListener(int position, char letter) {
                if(popupWindow!=null){
                    if(popupWindow.isShowing()){
                        letterText.setText(String.valueOf(letter));
                        startDelay();
                    }else{
                        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);
                        startDelay();
                    }
                }
            }
        });
    }

    private void startDelay(){
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(0,500);
    }

    private void hidePopupWindow(){
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
    }

    private static class MainHandler extends Handler{
        private WeakReference<MainActivity> weakReference;

        private MainHandler(MainActivity activity){
            weakReference=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            MainActivity activity=weakReference.get();
            if(activity!=null){
                activity.hidePopupWindow();
            }
        }

    }
}
