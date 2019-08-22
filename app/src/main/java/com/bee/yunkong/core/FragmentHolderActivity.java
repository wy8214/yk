package com.bee.yunkong.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bee.yunkong.R;
import com.bee.yunkong.util.logger.MyLog;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by yuanshenghong on 2017/3/15.
 * 承载Fragment的类
 */

public class FragmentHolderActivity extends BaseActivity {
    public static final String FRAGMENTCLASS = "FRAGMENTCLASS";
    public static final String FRAGMENTEXTRA = "FRAGMENTEXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_holder);
//        setSwipeBackEnable(getIntent().getBooleanExtra(SWIPEBACK, true));
        if (savedInstanceState == null && null != getIntent()) {
            Class<SupportFragment> clazz = (Class<SupportFragment>) getIntent().getSerializableExtra(FRAGMENTCLASS);
            if (null != clazz) {
                try {
                    SupportFragment fragment = clazz.newInstance();
                    if (null != getIntent().getBundleExtra(FRAGMENTEXTRA)) {
                        Bundle bundle = getIntent().getBundleExtra(FRAGMENTEXTRA);
//                        bundle.putBoolean(SWIPBACKENABLE, getIntent().getBooleanExtra(SWIPEBACK, true));
                        fragment.setArguments(bundle);
                    }
                    loadRootFragment(R.id.content, fragment);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    MyLog.d(e.getMessage());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    MyLog.d(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.d(e.getMessage());
                }
            }
        }

    }


    public static void startFragmentInNewActivity(Activity context, SupportFragment fragment) {
        Intent intent = new Intent(context, FragmentHolderActivity.class);
        intent.putExtra(FRAGMENTCLASS, fragment.getClass());
        intent.putExtra(FRAGMENTEXTRA, fragment.getArguments());
        context.startActivity(intent);
    }

    public static void startFragmentActivityForResult(Activity activity, SupportFragment fragment, int requestCode) {
        Intent intent = new Intent(activity, FragmentHolderActivity.class);
        intent.putExtra(FRAGMENTCLASS, fragment.getClass());
        intent.putExtra(FRAGMENTEXTRA, fragment.getArguments());
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startFragmentInNewActivity(Activity context, Class fragmentclazz) {
        Intent intent = new Intent(context, FragmentHolderActivity.class);
        intent.putExtra(FRAGMENTCLASS, fragmentclazz);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((SupportFragment)getTopFragment()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetWork();
        if(null!=getTopFragment()){
            getTopFragment().onResume();
        }
    }
}
