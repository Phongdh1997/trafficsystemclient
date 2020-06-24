package com.hcmut.admin.bktrafficsystem.ui.direction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ui.home.HomeFragment;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.SearchPlaceFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AutoCompleteTextView txtBeginAddress;
    private AutoCompleteTextView txtEndAddress;
    private AppCompatImageButton btnBack;

    public static AutocompletePrediction beginSearchPlaceResult;
    public static AutocompletePrediction endSearchPlaceResult;

    public DirectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DirectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DirectionFragment newInstance(String param1, String param2) {
        DirectionFragment fragment = new DirectionFragment();
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
    public void onResume() {
        super.onResume();
        handleSearchResult();
    }

    private void handleSearchResult() {
        if (beginSearchPlaceResult != null) {
            txtBeginAddress.setText(beginSearchPlaceResult.getSecondaryText(null));
        }
        if (endSearchPlaceResult != null) {
            txtEndAddress.setText(endSearchPlaceResult.getSecondaryText(null));
        }
        if (beginSearchPlaceResult != null && endSearchPlaceResult != null) {
            performDirection();
        }
    }

    private void performDirection() {
        Toast.makeText(getContext(), "direct", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtBeginAddress = view.findViewById(R.id.txtBeginAddress);
        txtEndAddress = view.findViewById(R.id.txtEndAddress);
        btnBack = view.findViewById(R.id.btnBack);

        addEvents();
    }

    private void addEvents() {
        txtBeginAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    navigateToSearchFragment(SearchPlaceFragment.BEGIN_DIRECTION_FRAGMENT_CALLER);
                }
            }
        });
        txtEndAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    navigateToSearchFragment(SearchPlaceFragment.END_DIRECTION_FRAGMENT_CALLER);
                }
            }
        });
    }

    private void navigateToSearchFragment(String caller) {
        Bundle bundle = new Bundle();
        bundle.putString(SearchPlaceFragment.CALLER, caller);
        NavHostFragment.findNavController(DirectionFragment.this)
                .navigate(R.id.searchPlaceFragment, bundle);
    }
}
