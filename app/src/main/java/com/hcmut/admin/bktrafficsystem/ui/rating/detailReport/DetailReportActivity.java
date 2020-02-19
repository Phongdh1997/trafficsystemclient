package com.hcmut.admin.bktrafficsystem.ui.rating.detailReport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.PostRatingResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.ui.rating.photo.PreViewPhotoActivity;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailReportActivity extends AppCompatActivity implements View.OnClickListener, RatingDialogListener {

    LinearLayout img_list;
    Button btnRating;
    ImageView imageView1,imageView2, imageView3,imageView4, imgAvatar;
    AppCompatImageView ivBack;
    View view;
    TextView tvSpeed, tvTitleDescription, tvDescription, tvReason, tvName, tvScore;
    private boolean isOnlyOne = true;
    private String reportId;
    private ReportResponse report;
    private List<ImageView> listImageView;
    private Group groupReason;
    private ProgressDialog progressDialog;
    private ConstraintLayout ctlContent;
    private List<String> reason = Arrays.asList("Tắc đường", "Ngập lụt", "Vật cản", "Tai Nạn", "Công an", "Đường cấm");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        progressDialog = ProgressDialog.show(this, "", getString(R.string.loading), true);
        reportId = getIntent().getStringExtra("REPORT_ID");
        if (Objects.equals(getIntent().getAction(), "detail")){
            if (getIntent().getExtras() != null && getIntent().getExtras().get("reportId") != null){
                Object stringReportID = getIntent().getExtras().get("reportId");
                if (stringReportID != null) {
                    reportId = (String) stringReportID;
                }
            }
        }
        initView();
        getDetailReport();
    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        btnRating = findViewById(R.id.btnRating);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        ctlContent = findViewById(R.id.ctlContent);

        listImageView = Arrays.asList(imageView1,imageView2,imageView3,imageView4);

        img_list = findViewById(R.id.img_list);

        imgAvatar = findViewById(R.id.img_avatar);
        view = findViewById(R.id.lineView);
        tvSpeed = findViewById(R.id.tv_speed);
        tvDescription = findViewById(R.id.tv_more_description);
        tvReason = findViewById(R.id.tv_reason);
        tvName = findViewById(R.id.tv_name);
        tvScore = findViewById(R.id.tv_score);
        groupReason = findViewById(R.id.group_reason);

        tvTitleDescription = findViewById(R.id.tv_title_description);

        ivBack.setOnClickListener(DetailReportActivity.this);
        btnRating.setOnClickListener(DetailReportActivity.this);
        img_list.setOnClickListener(DetailReportActivity.this);
    }


    private void getDetailReport() {
        new CallApi().createService().getDetailTrafficReport(reportId).enqueue(new Callback<BaseResponse<ReportResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<ReportResponse>> call, final Response<BaseResponse<ReportResponse>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    report = response.body().getData();
                    if (report.getUser().getId().equals(SharedPrefUtils.getUser(DetailReportActivity.this).getUserId())) {
                        btnRating.setEnabled(false);
                    }
                    if (report.getImages() == null || report.getImages().size() == 0) {
                        img_list.setVisibility(View.GONE);
                        imageView1.setVisibility(View.GONE);
                        imageView2.setVisibility(View.GONE);
                        imageView3.setVisibility(View.GONE);
                        imageView4.setVisibility(View.GONE);
                    } else {
                        img_list.setVisibility(View.VISIBLE);
                        for (int i = 0; i < report.getImages().size(); i++) {
                            listImageView.get(i).setVisibility(View.VISIBLE);
                            Glide.with(DetailReportActivity.this)
                                    .load(report.getImages().get(i))
                                    .into(listImageView.get(i));
                        }
                    }
                    tvName.setText(report.getUser().getName());
                    tvSpeed.setText(report.getVelocity() + " km/h");
                    if (report.getCauseId().size() > 0) {
                        groupReason.setVisibility(View.VISIBLE);
                        tvReason.setText(reason.get(Integer.parseInt(report.getCauseId().get(0)) - 1));
                    } else groupReason.setVisibility(View.GONE);
                    Glide.with(imgAvatar.getContext())
                            .load(report.getUser().getAvatar())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.icon_avatar_empty)
                                    .fitCenter())
                            .into(imgAvatar);
                    if (report.getDescription() == null || report.getDescription().isEmpty()) {
                        tvDescription.setVisibility(View.GONE);
                        tvTitleDescription.setVisibility(View.GONE);
                    } else {
                        tvDescription.setVisibility(View.VISIBLE);
                        tvTitleDescription.setVisibility(View.VISIBLE);
                        tvDescription.setText(report.getDescription());
                    }
                    if (report.getReputation() == 0.0F) {
                        tvScore.setVisibility(View.GONE);
                    } else {
                        tvScore.setVisibility(View.VISIBLE);
                        tvScore.setText(String.valueOf(report.getReputation()));
                    }

                    btnRating.setOnClickListener(DetailReportActivity.this);

                    img_list.setOnClickListener(DetailReportActivity.this);

                    ctlContent.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Gửi")
                .setNegativeButtonText("Để sau")
                .setNumberOfStars(5)
                .setDefaultRating(3)
                .setTitle("Đánh giá")
                .setDescription("Chọn và gửi phản hồi của bạn")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.yellow)
                .setTitleTextColor(R.color.black)
                .setDescriptionTextColor(R.color.text_hint)
                .setHint("Để lại bình luận của bạn tại đây...")
                .setHintTextColor(R.color.text_hint)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.bg_comment_rating)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(DetailReportActivity.this)
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
        new CallApi().createService().postRating(SharedPrefUtils.getUser(this).getAccessToken()
                , reportId, (float) rate / 5).enqueue(new Callback<BaseResponse<PostRatingResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<PostRatingResponse>> call, Response<BaseResponse<PostRatingResponse>> response) {
                //TODO:
                if (response.code() == 500)
                    Toast.makeText(DetailReportActivity.this, "Bạn đã đánh giá bài này rồi", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(DetailReportActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BaseResponse<PostRatingResponse>> call, Throwable t) {
                Toast.makeText(DetailReportActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
        isOnlyOne = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRating: {
                if (isOnlyOne) {
                    showDialog();
                    isOnlyOne = false;
                }
                break;
            }
            case R.id.img_list: {
                Intent intent = new Intent(DetailReportActivity.this, PreViewPhotoActivity.class);
                intent.putStringArrayListExtra("IMAGE", report.getImages());
                startActivity(intent);
                break;
            }
            case R.id.ivBack: {
                onBackPressed();
            }
        }
    }
}
