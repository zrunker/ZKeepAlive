package cc.ibooker.zkeepalive;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cc.ibooker.zkeepalivelib.ZKeepAlive;

/**
 * 进程保活方案
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZKeepAlive.Companion.getInstance().register(this);
    }
}
