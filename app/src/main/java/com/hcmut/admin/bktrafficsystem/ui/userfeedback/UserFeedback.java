package com.hcmut.admin.bktrafficsystem.ui.userfeedback;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcmut.admin.bktrafficsystem.BuildConfig;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ui.home.HomeFragment;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFeedback#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedback extends Fragment implements MapActivity.OnBackPressCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imgBack;
    private TextView txtAppVersion;

    public UserFeedback() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFeedback.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFeedback newInstance(String param1, String param2) {
        UserFeedback fragment = new UserFeedback();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents(view);
    }

    private void addControls(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        txtAppVersion = view.findViewById(R.id.txtAppVersion);
        txtAppVersion.setText(BuildConfig.VERSION_NAME);
    }

    private void addEvents(View view) {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(UserFeedback.this).popBackStack();
            }
        });
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(UserFeedback.this).popBackStack();
    }
}