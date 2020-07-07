package com.hcmut.admin.bktrafficsystem.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.CallPhone;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.ImageDownloader;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.glide.GlideApp;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RoundedImageView imgAvatar;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtManageAccount;
    private TextView txtCallVOH;
    private TextView txtLogout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents(view);
    }

    private void addControls(View view) {
        imgAvatar = view.findViewById(R.id.imgAvatar);
        txtManageAccount = view.findViewById(R.id.txtManageAccount);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtCallVOH = view.findViewById(R.id.txtCallVOH);
        txtLogout = view.findViewById(R.id.txtLogout);
    }

    private void addEvents(View view) {
        txtManageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AccountFragment.this)
                        .navigate(R.id.action_accountFragment_to_profileFragment);
            }
        });
        try {
            final MapActivity mapActivity = (MapActivity) view.getContext();
            txtCallVOH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallPhone callPhone = new CallPhone(mapActivity);
                    callPhone.checkCallPhonePermisstion();
                }
            });
            txtLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapActivity.currentUser.logout(mapActivity);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateView() {
        txtName.setText(MapActivity.currentUser.getUserName());
        txtEmail.setText(MapActivity.currentUser.getUserEmail());

        try {
            GlideApp.with(txtName.getContext())
                    .load(MapActivity.currentUser.getImgUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imgAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
