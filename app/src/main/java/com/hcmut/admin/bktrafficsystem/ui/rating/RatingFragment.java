package com.hcmut.admin.bktrafficsystem.ui.rating;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.ReportRatingTest;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.ui.rating.photo.PreViewPhotoActivity;
import com.hcmut.admin.bktrafficsystem.ui.report.ViewReportFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String SEGMENT_DATA = "segment_data";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private boolean isOnlyOne = true;
    private RatingAdapter ratingAdapter;
    private TextView tvVelocity;

    private ViewReportFragment.SegmentData currentSegmentData;

    public RatingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingFragment newInstance(String param1, String param2) {
        RatingFragment fragment = new RatingFragment();
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
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            currentSegmentData = (ViewReportFragment.SegmentData) getArguments().getSerializable(SEGMENT_DATA);
        } catch (Exception e) {
            currentSegmentData = new ViewReportFragment.SegmentData(-1, 0, "");
        }

        addControls(view);
        addEvents();
        initReports();
    }

    private void addControls(View view) {
        tvVelocity = view.findViewById(R.id.tv_speed);
        recyclerView = view.findViewById(R.id.rvRating);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ratingAdapter = new RatingAdapter(view.getContext());
        recyclerView.setAdapter(ratingAdapter);
    }

    private void addEvents() {
        ratingAdapter.setOnItemClickListener(new RatingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int id, View view, int position, ArrayList<String> images) {
                switch (view.getId()) {
                    case R.id.btn_rating: {
                        ReportResponse reportResponse = ratingAdapter.getItem(position);
                        showRatingDialog(reportResponse);
                        break;
                    }
                    case R.id.img_list: {
                        Intent intent = new Intent(getActivity(), PreViewPhotoActivity.class);
                        intent.putStringArrayListExtra("IMAGE", images);
                        startActivity(intent);
                        break;
                    }
                }

            }
        });
    }

    private void initReports() {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getContext(),
                "",
                getString(R.string.loading),
                true);
        CallApi.createService()
                .getOldTrafficReport(Calendar.getInstance().getTimeInMillis(), (int) currentSegmentData.segmentId)
                .enqueue(new Callback<BaseResponse<List<ReportResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<ReportResponse>>> call, final Response<BaseResponse<List<ReportResponse>>> response) {
                        progressDialog.dismiss();
//                        if (response.body() != null) {
//                            updateData(response.body().getData());
//                        }
                        updateData(ReportRatingTest.getReportFromSegment(currentSegmentData.segmentId));
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<List<ReportResponse>>> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateData(List<ReportResponse> newDatas) {
        if (newDatas != null && newDatas.size() > 0) {
            ratingAdapter.setData(newDatas);
            tvVelocity.setText("Vận tốc trung bình: " + currentSegmentData.speed + " km/h");
        }
    }

    private void showRatingDialog(ReportResponse reportResponse) {

    }
}
