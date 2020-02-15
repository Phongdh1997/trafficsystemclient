package com.hcmut.admin.googlemapapitest.ui.rating.photo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hcmut.admin.googlemapapitest.R;

public class PhotoFragment extends Fragment {

    private AppCompatImageView imageView;
    private String url;

    public static Fragment newInstance(String photoUrl) {
        Fragment fragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("URL", photoUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments() != null ? getArguments().getString("URL") : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_preview_photo, container, false);
        imageView = view.findViewById(R.id.ivPhoto);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(this).load(url).into(imageView);
    }
}
