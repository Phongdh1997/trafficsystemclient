package com.hcmut.admin.bktrafficsystem.ui.rating.photo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class PreViewPhotoAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> photos;

    PreViewPhotoAdapter(FragmentManager fm) {
        super(fm);
    }

    void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    @Override
    public Fragment getItem(int position) {
        String url = photos.get(position);
        return PhotoFragment.newInstance(url);
    }

    @Override
    public int getCount() {
        return photos.size();
    }
}
