package com.hcmut.admin.bktrafficsystem.util;

import com.hcmut.admin.bktrafficsystem.R;

import java.util.ArrayList;

public class Slide {
    private static final ArrayList<Integer> slides = new ArrayList<Integer>() {{
        add(R.drawable.slide1);
        add(R.drawable.slide2);
        add(R.drawable.slide3);
        add(R.drawable.slide4);
        add(R.drawable.slide5);
    }};

    public static ArrayList<Integer> getSlides() {
        return slides;
    }
}
