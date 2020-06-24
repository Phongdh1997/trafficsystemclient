package com.hcmut.admin.bktrafficsystem.ui.searchplace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.PlaceAutoCompleteAdapter;
import com.hcmut.admin.bktrafficsystem.ui.direction.DirectionFragment;
import com.hcmut.admin.bktrafficsystem.ui.home.HomeFragment;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.result.SearchPlaceAdapter;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPlaceFragment extends Fragment implements MapActivity.OnBackPressCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String SEARCH_PLACE_RESULT = "search_place_result";
    public static final String CALLER = "caller";
    public static final String HOME_FRAGMENT_CALLER = "HOME_FRAGMENT";
    public static final String BEGIN_DIRECTION_FRAGMENT_CALLER = "BEGIN_DIRECTION_FRAGMENT_CALLER";
    public static final String END_DIRECTION_FRAGMENT_CALLER = "END_DIRECTION_FRAGMENT_CALLER";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AutoCompleteTextView txtSearchInput;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private RecyclerView rcSearchPlaceResult;
    private SearchPlaceAdapter searchPlaceAdapter;
    private ImageView imgBack;

    public SearchPlaceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchPlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPlaceFragment newInstance(String param1, String param2) {
        SearchPlaceFragment fragment = new SearchPlaceFragment();
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
        return inflater.inflate(R.layout.fragment_search_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgBack = view.findViewById(R.id.imgBack);

        rcSearchPlaceResult = view.findViewById(R.id.rcSearchPlaceResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcSearchPlaceResult.setLayoutManager(layoutManager);
        searchPlaceAdapter = new SearchPlaceAdapter();
        rcSearchPlaceResult.setAdapter(searchPlaceAdapter);
        txtSearchInput = view.findViewById(R.id.search_edt);
        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(
                view.getContext(),
                Places.createClient(view.getContext()),
                searchPlaceAdapter);
        txtSearchInput.setAdapter(placeAutoCompleteAdapter);
        txtSearchInput.requestFocus();

        addEvents(view);
    }

    private void addEvents(final View view){
        searchPlaceAdapter.setOnItemClickedListener(new SearchPlaceAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(AutocompletePrediction itemData) {
                // Toast.makeText(view.getContext(), itemData.getPrimaryText(null), Toast.LENGTH_SHORT).show();
                try {
                    String caller = getArguments().getString(SearchPlaceFragment.CALLER);
                    switch (caller) {
                        case HOME_FRAGMENT_CALLER:
                            HomeFragment.searchPlaceResult = itemData;
                            break;
                        case BEGIN_DIRECTION_FRAGMENT_CALLER:
                            DirectionFragment.beginSearchPlaceResult = itemData;
                            break;
                        case END_DIRECTION_FRAGMENT_CALLER:
                            DirectionFragment.endSearchPlaceResult = itemData;
                            break;
                    }
                } catch (Exception e) {}
                NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
            }
        });
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(SearchPlaceFragment.this).popBackStack();
    }
}
