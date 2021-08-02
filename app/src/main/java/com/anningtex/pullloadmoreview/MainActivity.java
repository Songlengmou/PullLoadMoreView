package com.anningtex.pullloadmoreview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.anningtex.pullloadmoreview.view.PullLoadMoreView;
import com.syp.library.BaseRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

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
        RecyclerView mRecycle = findViewById(R.id.recycle);
        mPullLoadMoreView = findViewById(R.id.pullLoadMoreView);
        //添加头部布局
        mPullLoadMoreView.addHeadView(R.layout.top_layout);
        //添加监听open/close
        mPullLoadMoreView.setViewStateListener(viewState -> {
            if (viewState == PullLoadMoreView.VIewState.OPEN) {
                Toast.makeText(MainActivity.this, "Open", Toast.LENGTH_SHORT).show();
                mPullLoadMoreView.findViewById(R.id.imageView_one).setOnClickListener(view -> {
                    Toast.makeText(MainActivity.this, "1被点击了", Toast.LENGTH_SHORT).show();
                });
                mPullLoadMoreView.findViewById(R.id.imageView_two).setOnClickListener(view -> {
                    Toast.makeText(MainActivity.this, "2被点击了", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
            }
        });
        //列表
        List<String> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < 20; i++) {
            index++;
            list.add(index + "");
        }
        BaseRecycleAdapter<String> adapter = new BaseRecycleAdapter<>(R.layout.adapter_item, list);
        adapter.setOnDataToViewListener((helper, item, position) -> {
            String str = (String) item;
            helper.setText(R.id.tv_item, str);
        });
        mRecycle.setAdapter(adapter);
    }
}