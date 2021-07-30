package com.anningtex.pullloadmoreview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anningtex.pullloadmoreview.view.PullLoadMoreView;

/**
 * @author Song
 * desc:仿微信下拉小程序
 * source:https://github.com/wenwenwen888/PullLoadMoreView
 */
public class MainActivity extends AppCompatActivity {
    private PullLoadMoreView mPullLoadMoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mPullLoadMoreView = findViewById(R.id.pullLoadMoreView);
        //添加头部布局
        mPullLoadMoreView.addHeadView(R.layout.top_layout);
        //添加监听open/close
        mPullLoadMoreView.setViewStateListener(viewState -> {
            if (viewState == PullLoadMoreView.VIewState.OPEN) {
                Toast.makeText(MainActivity.this, "Open", Toast.LENGTH_SHORT).show();
                mPullLoadMoreView.findViewById(R.id.imageView).setOnClickListener(view -> {
                    Toast.makeText(MainActivity.this, "被点击了", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
            }
        });
    }
}