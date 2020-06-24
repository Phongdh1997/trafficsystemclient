package com.hcmut.admin.bktrafficsystem.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.MarkerCreating;
import com.hcmut.admin.bktrafficsystem.model.SearchPlaceHandler;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.SearchPlaceFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static AutocompletePrediction searchPlaceResult;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap map;
    private AutoCompleteTextView txtSearchInput;
    private CardView customToolbar;
    private ImageView imgClearText;
    private ImageView imgBack;

    private MarkerCreating searchMarkerCreating;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFeatureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchPlaceResult != null) {
            onSearchResultReady(searchPlaceResult);
            searchPlaceResult = null;
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        if (searchMarkerCreating != null) {
            searchMarkerCreating.removeMarker();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtSearchInput = view.findViewById(R.id.search_edt);
        customToolbar = view.findViewById(R.id.custom_toolbar);
        imgBack = view.findViewById(R.id.imgBack);
        imgClearText = view.findViewById(R.id.imgClearText);
        addEvents();
    }

    private void addEvents() {
        txtSearchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SearchPlaceFragment.CALLER, SearchPlaceFragment.HOME_FRAGMENT_CALLER);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.searchPlaceFragment, bundle);
                }
            }
        });
    }

    // TODO:
    private void onSearchResultReady(AutocompletePrediction placeResult) {
        CharSequence addressString = placeResult.getSecondaryText(null);
        txtSearchInput.setText(addressString);
        toggleBackAndCancelView(View.VISIBLE);

        // search place and set marker
        LatLng latLng = SearchPlaceHandler.getLatLngFromAddressTextInput(getContext(), addressString.toString());
        if (latLng != null) {
            createMarker(latLng);
        } else {
            Toast.makeText(getContext(), "Kết nối thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleBackAndCancelView(int state) {
        imgBack.setVisibility(state);
        imgClearText.setVisibility(state);
    }

    public void createMarker(LatLng latLng) {
        if (searchMarkerCreating != null) {
            searchMarkerCreating.removeMarker();
        }
        searchMarkerCreating = new MarkerCreating(latLng);
        searchMarkerCreating.createMarker(getContext(), map, null, true);
    }
}
