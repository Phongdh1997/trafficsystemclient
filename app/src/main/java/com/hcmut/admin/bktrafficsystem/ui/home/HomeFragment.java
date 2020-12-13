package com.hcmut.admin.bktrafficsystem.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.business.MarkerCreating;
import com.hcmut.admin.bktrafficsystem.business.SearchPlaceHandler;
import com.hcmut.admin.bktrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.bktrafficsystem.customview.SearchInputView;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.callback.SearchResultCallback;
import com.hcmut.admin.bktrafficsystem.util.MapUtil;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment
        implements SearchResultCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AutocompletePrediction searchPlaceResult;
    private boolean isHaveSearchResult = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap map;
    private FloatingActionButton btnDirect;
    private SearchInputView searchInputView;
    private FloatingActionButton btnCurrentLocation;
    private AppCompatButton btnToggleRender;

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
        try {
            MapActivity mapActivity = ((MapActivity) getContext());
            mapActivity.showBottomNav();
            updateRenderStatusOptionBackground(mapActivity.isRenderStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isHaveSearchResult && searchPlaceResult != null) {
            Objects.requireNonNull((MapActivity) getActivity()).hideBottomNav();
            handleSearchResult();
        } else {
            searchInputView.updateView();
        }
    }

    private void handleSearchResult() {
        String addressString = searchPlaceResult.getSecondaryText(null).toString();
        searchInputView.setTxtSearchInputText(addressString);
        searchInputView.handleBackAndClearView(true);

        // search place and set marker
        LatLng latLng = SearchPlaceHandler.getLatLngFromAddressTextInput(getContext(), addressString);
        if (latLng != null) {
            createMarker(latLng);
        } else {
            Toast.makeText(getContext(), "Kết nối thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
        searchPlaceResult = null;
        isHaveSearchResult = false;
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

        btnDirect = view.findViewById(R.id.btnDirect);
        searchInputView = view.findViewById(R.id.searchInputView);
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        btnToggleRender = view.findViewById(R.id.btnToggleRender);
        addEvents();
    }

    private void addEvents() {
        searchInputView.setTxtSearchInputEvent(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SearchPlaceResultHandler.getInstance()
                            .addSearchPlaceResultListener(HomeFragment.this);
                    Bundle bundle = new Bundle();
                    bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.NORMAL_SEARCH);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_mapFeatureFragment_to_searchPlaceFragment, bundle);
                }
            }
        });
        searchInputView.setImgClearTextEvent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshSearch();
            }
        });
        searchInputView.setImgBackEvent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshSearch();
            }
        }, false);
        btnDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_mapFeatureFragment_to_directionFragment);
            }
        });
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        if (map != null) {
                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                    new LatLng(location.getLatitude(), location.getLongitude()), 18));
                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Bản đồ chưa được tải lên, vui lòng thử lại",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Không thể lấy vị trí, vui lòng thử lại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        btnToggleRender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity mapActivity = (MapActivity) getContext();
                boolean toggleValue = !mapActivity.isRenderStatus();
                mapActivity.setTrafficEnable(toggleValue);
                updateRenderStatusOptionBackground(toggleValue);
            }
        });
    }

    public void updateRenderStatusOptionBackground(boolean isEnable) {
        if (isEnable) {
            btnToggleRender.setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.bg_button_active));
        } else {
            btnToggleRender.setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.gray_bg_custom));
        }
    }

    public void createMarker(LatLng latLng) {
        if (searchMarkerCreating != null) {
            searchMarkerCreating.removeMarker();
        }
        searchMarkerCreating = new MarkerCreating(latLng);
        searchMarkerCreating.createMarker(getContext(), map, null, true, true);
    }

    /**
     * refresh Search Place
     */
    public void refreshSearch() {
        Objects.requireNonNull((MapActivity) getActivity()).showBottomNav();
        if (searchMarkerCreating != null) {
            searchMarkerCreating.removeMarker();
        }
        searchInputView.handleBackAndClearView(false);
    }

    @Override
    public void onSearchResultReady(AutocompletePrediction placeResult) {
        searchPlaceResult = placeResult;
        isHaveSearchResult = true;
    }

    @Override
    public void onBeginSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onEndSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onSelectedBeginSearchPlaceResultReady(LatLng result) {

    }

    @Override
    public void onSelectedEndSearchPlaceResultReady(LatLng result) {

    }
}
