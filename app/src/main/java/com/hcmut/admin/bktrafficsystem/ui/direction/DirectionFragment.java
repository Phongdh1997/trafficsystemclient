package com.hcmut.admin.bktrafficsystem.ui.direction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.MarkerCreating;
import com.hcmut.admin.bktrafficsystem.model.SearchDirectionHandler;
import com.hcmut.admin.bktrafficsystem.model.response.Coord;
import com.hcmut.admin.bktrafficsystem.ui.home.HomeFragment;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.SearchPlaceFragment;

import java.util.ArrayList;
import java.util.List;

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
    private Button btnDistance;
    private Button btnTime;
    private boolean isTimeDirectionSelected = true;

    public static AutocompletePrediction beginSearchPlaceResult;
    public static AutocompletePrediction endSearchPlaceResult;

    private MarkerCreating beginMarkerCreating;
    private MarkerCreating endMarkerCreating;
    private GoogleMap map;
    private List<Polyline> directPolylines = new ArrayList<>();

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
        updateTimeAndDistanceView();
        handleSearchResult();
    }

    private void handleSearchResult() {
        if (beginSearchPlaceResult != null) {
            txtBeginAddress.setText(beginSearchPlaceResult.getSecondaryText(null));
        }
        if (endSearchPlaceResult != null) {
            txtEndAddress.setText(endSearchPlaceResult.getSecondaryText(null));
        }
        performDirection();
    }

    private void performDirection() {
        if (isCouldDirect()) {
            SearchDirectionHandler.direct(
                    getContext(),
                    beginSearchPlaceResult.getSecondaryText(null).toString(),
                    endSearchPlaceResult.getSecondaryText(null).toString(),
                    isTimeDirectionSelected,
                    new SearchDirectionHandler.DirectResultCallback() {
                        @Override
                        public void onSuccess(List<Coord> directs) {
                            renderDirection(directs);
                        }

                        @Override
                        public void onFail() {
                            try {
                                Toast.makeText(getContext(), "Không thể tìm thấy đường đến đó, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }
                    });
        }
    }

    private boolean isCouldDirect() {
        return beginSearchPlaceResult != null && endSearchPlaceResult != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_direction, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtBeginAddress = view.findViewById(R.id.txtBeginAddress);
        txtEndAddress = view.findViewById(R.id.txtEndAddress);
        btnBack = view.findViewById(R.id.btnBack);

        btnDistance = view.findViewById(R.id.btnDistance);
        btnTime = view.findViewById(R.id.btnTime);

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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMarker();
                removeDirect();
                NavHostFragment.findNavController(DirectionFragment.this).popBackStack();
            }
        });
        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDistanceButtonClick();
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimeButtonClick();
            }
        });
    }

    private void onTimeButtonClick() {
        if(!isTimeDirectionSelected) {
            isTimeDirectionSelected = true;
            updateTimeAndDistanceView();
            performDirection();
        }
    }

    private void onDistanceButtonClick() {
        if (isTimeDirectionSelected) {
            isTimeDirectionSelected = false;
            updateTimeAndDistanceView();
            performDirection();
        }
    }

    private void updateTimeAndDistanceView() {
        Context context = getContext();
        if (context != null) {
            if (isTimeDirectionSelected) {
                btnTime.setBackground(context.getDrawable(R.drawable.bg_button_selected));
                btnTime.setTextColor(Color.BLUE);
                btnDistance.setBackground(context.getDrawable(R.drawable.bg_default_button));
                btnDistance.setTextColor(Color.BLACK);
            } else {
                btnDistance.setBackground(context.getDrawable(R.drawable.bg_button_selected));
                btnDistance.setTextColor(Color.BLUE);
                btnTime.setBackground(context.getDrawable(R.drawable.bg_default_button));
                btnTime.setTextColor(Color.BLACK);
            }
        }
    }

    private void navigateToSearchFragment(String caller) {
        Bundle bundle = new Bundle();
        bundle.putString(SearchPlaceFragment.CALLER, caller);
        NavHostFragment.findNavController(DirectionFragment.this)
                .navigate(R.id.searchPlaceFragment, bundle);
    }

    private void createMarker(LatLng beginLatLng, LatLng endLatLng) {
        removeMarker();
        beginMarkerCreating = new MarkerCreating(beginLatLng);
        endMarkerCreating = new MarkerCreating(endLatLng);
        beginMarkerCreating.createMarker(getContext(), map, null, false);
        endMarkerCreating.createMarker(getContext(), map, null, false);
    }

    private void renderDirection(List<Coord> directs) {
        LatLng beginLatLng = new LatLng(directs.get(0).getLat(), directs.get(0).getLng());
        LatLng endLatLng = new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng());
        createMarker(beginLatLng, endLatLng);
        removeDirect();
        for (int i = 0; i < directs.size() - 1; i++) {
            LatLng start = new LatLng(directs.get(i).getLat(), directs.get(i).getLng());
            LatLng end = new LatLng(directs.get(i + 1).getLat(), directs.get(i + 1).getLng());
            try {
                directPolylines.add(map.addPolyline(
                        new PolylineOptions().add(
                                start,
                                end
                        ).width(5).geodesic(true)
                                .clickable(true)
                                .color(Color.BLUE)
                ));
            } catch (Exception e) {}
        }
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(directs.get(0).getLat(), directs.get(0).getLng()))
                .include(new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng()))
                .build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        map.animateCamera(cu);
    }

    private void removeDirect() {
        if (directPolylines != null) {
            for (Polyline polyline : directPolylines) {
                polyline.remove();
            }
            directPolylines.clear();
        }
    }

    private void removeMarker() {
        if (beginMarkerCreating != null) {
            beginMarkerCreating.removeMarker();
        }
        if (endMarkerCreating != null) {
            endMarkerCreating.removeMarker();
        }
    }
}
