package com.hcmut.admin.bktrafficsystem.util;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

public class CustomDrawerButton extends AppCompatImageView implements DrawerLayout.DrawerListener {

    private DrawerLayout mDrawerLayout;
    private int side = Gravity.LEFT;

    public CustomDrawerButton(Context context) {
        super(context);
    }

    public CustomDrawerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void changeState() {
        if (mDrawerLayout.isDrawerOpen(side)) {
            mDrawerLayout.closeDrawer(side);
        } else {
            mDrawerLayout.openDrawer(side);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public CustomDrawerButton setDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
        return this;
    }
}