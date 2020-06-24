package com.hcmut.admin.bktrafficsystem.ui.report;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcmut.admin.bktrafficsystem.MyApplication;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.ViewReportHandler;
import com.hcmut.admin.bktrafficsystem.model.point.Point;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.rating.RatingFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String REPORT_RATING = "report_rating";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GoogleMap map;
    private Button btnViewDetail;
    private TextView txtSpeed;
    private Button btnGetStatus;
    private TextView txtStatusColor;
    private ConstraintLayout clReview;

    private ViewReportHandler viewReportHandler;
    private ArrayList<Marker> userReportMarker = new ArrayList<>();
    private SegmentData selectedSegment;

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            if (REPORT_RATING.equals(marker.getTag())) {
                View v = getLayoutInflater().inflate(R.layout.layout_info_window, null);
                TextView tvInfo = v.findViewById(R.id.tvInfo);
                String [] temp = marker.getTitle().split("/");
                try {
                    tvInfo.setText(temp[1] + " km/h");
                } catch (Exception e) {}
                return v;
            }
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    };

    public ViewReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportView.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewReportFragment newInstance(String param1, String param2) {
        ViewReportFragment fragment = new ViewReportFragment();
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
        return inflater.inflate(R.layout.fragment_report_view, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewReportHandler = new ViewReportHandler(context);

        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                }
            });
            mapActivity.setUserReportMarkerListener(new OnReportMakerClick() {
                @Override
                public void onClick(Marker marker) {
                    String[] datas = marker.getTitle().split("/");
                    showSelectedUserReport(datas);
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addControls(view);
        addEvents(view);
    }



    private void addControls(View view) {
        btnViewDetail = view.findViewById(R.id.btnViewDetail);
        txtSpeed = view.findViewById(R.id.tv_speed);
        btnGetStatus = view.findViewById(R.id.btnGetStatus);
        clReview = view.findViewById(R.id.clReview);
        txtStatusColor = view.findViewById(R.id.txtStatusColor);
    }

    private void addEvents(View view) {
        btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSegment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RatingFragment.SEGMENT_DATA, selectedSegment);
                    NavHostFragment.findNavController(ViewReportFragment.this)
                            .navigate(R.id.ratingFragment, bundle);
                }
            }
        });
        btnGetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderUserReport();
            }
        });
    }

    private void showSelectedUserReport(String [] datas) {
        try {
            selectedSegment = new SegmentData(Long.parseLong(datas[0]), Integer.parseInt(datas[1]), datas[2]);
            txtSpeed.setText("Vận tốc trung bình: " + datas[1] + "km/h");
            txtStatusColor.setBackgroundColor(Color.parseColor(datas[2]));
        } catch (Exception e) {}
    }

    private void toggleReview(int state) {
        clReview.setVisibility(state);
    }

    private void renderUserReport() {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getContext(),
                "",
                getString(R.string.loading),
                true);
        viewReportHandler.getUserReport(10.778013, 106.656323, new ViewReportHandler.SegmentResultCallback() {
            @Override
            public void onSuccess(List<MarkerOptions> markerOptionsList) {
                Marker marker;
                for (MarkerOptions markerOptions : markerOptionsList) {
                    marker = map.addMarker(markerOptions);
                    userReportMarker.add(marker);
                    marker.setTag(REPORT_RATING);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onHaveNotResult() {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideReportStatus() {
        if (userReportMarker != null) {
            for (Marker marker : userReportMarker) {
                marker.remove();
            }
            userReportMarker.clear();
        }
    }

    public interface OnReportMakerClick {
        void onClick(Marker marker);
    }

    public static class SegmentData implements Serializable {
        public final int speed;
        public final String color;
        public final long segmentId;

        public SegmentData(long segmentId, int speed, String color) {
            this.speed = speed;
            this.color = color;
            this.segmentId = segmentId;
        }
    }
}
