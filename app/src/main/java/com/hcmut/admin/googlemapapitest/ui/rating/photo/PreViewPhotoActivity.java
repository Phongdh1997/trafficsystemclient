package com.hcmut.admin.googlemapapitest.ui.rating.photo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;

import com.hcmut.admin.googlemapapitest.R;

import java.util.ArrayList;

public class PreViewPhotoActivity extends AppCompatActivity {

    private AppCompatTextView tvPosition;
    private ArrayList<String> images;
    private PreViewPhotoAdapter preViewPhotoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        tvPosition = findViewById(R.id.tvPosition);
        images = getIntent().getStringArrayListExtra("IMAGE");
        setupViewPager();
    }

    @SuppressLint("SetTextI18n")
    private void setupViewPager() {
        ViewPager viewPager = findViewById(R.id.vpPhotos);
        preViewPhotoAdapter = new PreViewPhotoAdapter(getSupportFragmentManager());
//        final ArrayList<String> list = new ArrayList<>();
//        list.add("http://string-api.vinova.sg/uploads/posts/original/post_15555690720.jpg");
//        list.add("http://string-api.vinova.sg/uploads/posts/original/post_15555615970.jpg");

        tvPosition.setText( "1 / " + images.size());
        preViewPhotoAdapter.setPhotos(images);
        viewPager.setAdapter(preViewPhotoAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                int curImage = i + 1;
                tvPosition.setText(curImage + " / " + images.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
}
