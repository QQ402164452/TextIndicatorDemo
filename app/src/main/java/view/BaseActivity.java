package view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Jason on 2017/1/23.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initListener();

}
