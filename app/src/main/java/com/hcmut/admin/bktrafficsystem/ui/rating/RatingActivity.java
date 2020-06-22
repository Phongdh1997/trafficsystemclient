package com.hcmut.admin.bktrafficsystem.ui.rating;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.PostRatingResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.model.response.TrafficStatusResponse;
import com.hcmut.admin.bktrafficsystem.ui.rating.photo.PreViewPhotoActivity;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;
import com.hcmut.admin.bktrafficsystem.util.TimeUtil;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Deprecated
public class RatingActivity extends AppCompatActivity implements RatingDialogListener {

    private RecyclerView recyclerView;
    private boolean isOnlyOne = true;
//    private ArrayList<ReportResponse> responses;
    private ReportResponse curReport;
    private RatingAdapter ratingAdapter;
    private int segmentId;
    private long date;
    private float velocity;
    private String dateTime, resultDate, resultTime;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextView tvDateTime, tvVelocity;
    private Calendar calendar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        progressDialog = ProgressDialog.show(this, "", getString(R.string.loading), true);
        tvVelocity = findViewById(R.id.tv_speed);
        segmentId = Integer.parseInt(getIntent().getStringExtra("SEGMENT_ID"));
        String txtDate = getIntent().getStringExtra("DATE");
        velocity = Float.parseFloat(getIntent().getStringExtra("VELOCITY"));
        setupToolbar(txtDate);
        initDateTimeDialog();
        initView();
    }

    private void initDateTimeDialog() {
        timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        resultTime = TimeUtil.formatHour(hourOfDay) + ":" + TimeUtil.formatMinute(minute);
                        dateTime = resultDate + resultTime;
                        tvDateTime.setText(dateTime);
                        //TODO: parse date + adapter
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        try {
                            Date parsedDate = df.parse(dateTime);
                            date = parsedDate.getTime();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        progressDialog = ProgressDialog.show(RatingActivity.this, "", getString(R.string.loading), true);
                        CallApi.createService().getVelocity(date, segmentId).enqueue(new Callback<BaseResponse<List<TrafficStatusResponse>>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<List<TrafficStatusResponse>>> call, Response<BaseResponse<List<TrafficStatusResponse>>> response) {
                                if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                                    getReport(response.body().getData().get(0).getVelocity());
                                } else {
                                    ratingAdapter.clearData();
                                    tvVelocity.setText(getString(R.string.text_no_report));
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<List<TrafficStatusResponse>>> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(RatingActivity.this, "Đã xảy ra lỗi không xác định", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);

        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        timePickerDialog.show();
                        resultDate = TimeUtil.formatDay(dayOfMonth) + "-" + TimeUtil.formatMonth(month + 1) + "-" + year + " ";
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupToolbar(String txtDate) {
        ConstraintLayout toolbar = findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date parsedDate = df.parse(txtDate);
            date = parsedDate.getTime() - 180000; //3 minutes ago
            calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Date parsedDate = new Date(date);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvDateTime.setText(df.format(parsedDate));
        tvDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private void initView() {
        TextView tvSpeed = findViewById(R.id.tv_speed);
//        tvSpeed.setText("Vận tốc trung bình: " + velocity + "km/h");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = findViewById(R.id.rvRating);
        recyclerView.setLayoutManager(layoutManager);
        ratingAdapter = new RatingAdapter(this);
        getReport(String.valueOf(velocity));
        if (ratingAdapter != null) {

        }
    }

    private void getReport(final String velocity) {
        CallApi.createService().getOldTrafficReport(date, segmentId).enqueue(new Callback<BaseResponse<List<ReportResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<ReportResponse>>> call, final Response<BaseResponse<List<ReportResponse>>> response) {
                if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    ratingAdapter.setData(response.body().getData());
                    recyclerView.setAdapter(ratingAdapter);
                    ratingAdapter.setOnItemClickListener(new RatingAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int id, View view, int position, ArrayList<String> images) {
                            switch (view.getId()) {
                                case R.id.btn_rating: {
                                    if (isOnlyOne) {
                                        curReport = response.body().getData().get(position);
                                        showDialog();
                                        isOnlyOne = false;
                                    }
                                    break;
                                }
                                case R.id.img_list: {
                                    Intent intent = new Intent(RatingActivity.this, PreViewPhotoActivity.class);
                                    intent.putStringArrayListExtra("IMAGE", images);
                                    startActivity(intent);
                                    break;
                                }
                            }

                        }
                    });
                    tvVelocity.setText("Vận tốc trung bình: " + velocity + " km/h");
                } else {
                    tvVelocity.setText(getString(R.string.text_no_report));
                }
                tvVelocity.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse<List<ReportResponse>>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Leo", t.getMessage());
            }
        });
    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Gửi")
                .setNegativeButtonText("Để sau")
//                .setNeutralButtonText("Later")
                .setNumberOfStars(5)
//                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(3)
                .setTitle("Đánh giá")
                .setDescription("Chọn và gửi phản hồi của bạn")
                .setCommentInputEnabled(true)
//                .setDefaultComment("This app is pretty cool !")
                .setStarColor(R.color.yellow)
//                .setNoteDescriptionTextColor(R.color.black)
                .setTitleTextColor(R.color.black)
                .setDescriptionTextColor(R.color.text_hint)
                .setHint("Để lại bình luận của bạn tại đây...")
                .setHintTextColor(R.color.text_hint)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.bg_comment_rating)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(RatingActivity.this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {
        isOnlyOne = true;
    }

    @Override
    public void onNeutralButtonClicked() {
        isOnlyOne = true;
    }

    @Override
    public void onPositiveButtonClicked(int rate, @NotNull String comment) {
        CallApi.createService().postRating(SharedPrefUtils.getUser(this).getAccessToken()
                , curReport.getId(), (float) rate / 5).enqueue(new Callback<BaseResponse<PostRatingResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<PostRatingResponse>> call, Response<BaseResponse<PostRatingResponse>> response) {
                //TODO
                if (response.code() == 500) Toast.makeText(RatingActivity.this, "Bạn đã đánh giá bài này rồi", Toast.LENGTH_SHORT).show();
                else Toast.makeText(RatingActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BaseResponse<PostRatingResponse>> call, Throwable t) {
                Toast.makeText(RatingActivity.this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        isOnlyOne = true;
    }
}
