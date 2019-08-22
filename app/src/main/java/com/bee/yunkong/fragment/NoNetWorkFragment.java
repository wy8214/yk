package com.bee.yunkong.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bee.yunkong.R;
import com.bee.yunkong.core.BaseFragment;

public class NoNetWorkFragment extends BaseFragment {
    public static NoNetWorkFragment newInstance() {

        Bundle args = new Bundle();

        NoNetWorkFragment fragment = new NoNetWorkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_no_network;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //todo 这个界面做啥？不能返回到主页面？
    }
}
