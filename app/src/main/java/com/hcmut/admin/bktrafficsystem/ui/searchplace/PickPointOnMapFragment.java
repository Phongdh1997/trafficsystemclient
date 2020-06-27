package com.hcmut.admin.bktrafficsystem.ui.searchplace;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;

import static com.hcmut.admin.bktrafficsystem.ui.searchplace.SearchPlaceFragment.SEARCH_PLACE_FRAGMENT_CALLER;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PickPointOnMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickPointOnMapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imgBack;
    private TextView btnOk;
    private TextView middlePoint;

    private GoogleMap map;

    public PickPointOnMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickPointOnMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickPointOnMapFragment newInstance(String param1, String param2) {
        PickPointOnMapFragment fragment = new PickPointOnMapFragment();
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
        return inflater.inflate(R.layout.fragment_pick_point_on_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnOk = view.findViewById(R.id.btnOk);
        imgBack = view.findViewById(R.id.imgBack);
        middlePoint = view.findViewById(R.id.middlePoint);

        addEvents();
    }

    private void addEvents() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleGetPointOnMap();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PickPointOnMapFragment.this).popBackStack();
            }
        });
    }

    private void handleGetPointOnMap() {
        int [] screenCoord = new int[2];
        middlePoint.getLocationOnScreen(screenCoord);
        try {
            LatLng location = map.getProjection().fromScreenLocation(
                    new Point(screenCoord[0], screenCoord[1]));
            int type = getArguments().getInt(SearchPlaceResultHandler.SEARCH_TYPE, -1);
            SearchPlaceResultHandler.getInstance()
                    .dispatchSearchPlaceResult(type, null, location);
            NavHostFragment.findNavController(PickPointOnMapFragment.this).popBackStack();
        } catch (Exception e) {

        }
    }
}
