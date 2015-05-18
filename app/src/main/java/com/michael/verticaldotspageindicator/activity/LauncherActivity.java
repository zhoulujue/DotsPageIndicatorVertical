package com.michael.verticaldotspageindicator.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michael.dotspageindicatorvertical.widget.DotsPageIndicatorVertical;
import com.michael.verticaldotspageindicator.R;

/**
 * Created by zhoulujue on 15/4/27.
 */
public class LauncherActivity extends Activity {

    private GridViewPager viewPager;
    private DotsPageIndicatorVertical verticalIndicator;

    private static final int[] STRING_RESOURCES = {R.string.page1, R.string.page2, R.string.page3, R.string.page4, R.string.page5};
    private static final int PAGE_ROW_COUNT = STRING_RESOURCES.length;
    private static final int PAGE_COLUMN_COUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        initView();
    }

    private void initView() {
        viewPager = (GridViewPager) findViewById(R.id.launcher_view_pager);
        LauncherViewPagerAdapter pagerAdapter = new LauncherViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        verticalIndicator = (DotsPageIndicatorVertical) findViewById(R.id.launcher_page_indicator);
        verticalIndicator.setPager(viewPager);

        initListeners();
    }

    private void initListeners() {
    }



    class LauncherViewPagerAdapter extends GridPagerAdapter {
        private Context mContext;

        public LauncherViewPagerAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getRowCount() {
            return PAGE_ROW_COUNT;
        }

        @Override
        public int getColumnCount(int i) {
            return PAGE_COLUMN_COUNT;
        }

        @Override
        protected Object instantiateItem(ViewGroup viewGroup, int row, int column) {
            View v = View.inflate(mContext, R.layout.launcher_background, null);
            TextView pageText = (TextView) v.findViewById(R.id.page_text);
            pageText.setText(STRING_RESOURCES[row]);
            viewGroup.addView(v);
            return v;
        }

        @Override
        protected void destroyItem(ViewGroup viewGroup, int row, int column, Object object) {
            viewGroup.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
