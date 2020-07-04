package com.hcmut.admin.bktrafficsystem.ui.rating;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.ImageDownloader;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.ImageListSmartDownloader;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

public class RatingViewHolder extends RecyclerView.ViewHolder {

    private RatingAdapter.OnItemClickedListener onItemClickedListener;
    private ReportResponse currReportData;

    private LinearLayout img_list;
    private Button btnRating;
    private ImageView imgAvatar;
    private TextView tvSpeed, tvDescription, tvReason, tvName, tvScore;

    public RatingViewHolder(View itemView, RatingAdapter.OnItemClickedListener onItemClickedListener) {
        super(itemView);
        this.onItemClickedListener = onItemClickedListener;

        btnRating = itemView.findViewById(R.id.btn_rating);
        img_list = itemView.findViewById(R.id.img_list);
        imgAvatar = itemView.findViewById(R.id.img_avatar);
        tvSpeed = itemView.findViewById(R.id.tv_speed);
        tvDescription = itemView.findViewById(R.id.tv_more_description);
        tvReason = itemView.findViewById(R.id.tv_reason);
        tvName = itemView.findViewById(R.id.tv_name);
        tvScore = itemView.findViewById(R.id.tv_score);
    }

    public void bindData(ReportResponse reportData) {
        if (reportData == null) {
            return;
        }

        currReportData = reportData;
        if (reportData.getUser().getId().equals(MapActivity.currentUser.getUserId())) {
            btnRating.setEnabled(false);
        }
        if (reportData.getImages() != null || reportData.getImages().size() == 0) {
            ImageListSmartDownloader.Builder builder = new ImageListSmartDownloader.Builder(img_list);
            for (String imageUrl : reportData.getImages()) {
                builder.addImageUrl(imageUrl);
            }
            builder.build().execute();
        }

        tvName.setText(reportData.getUser().getName());
        tvSpeed.setText(reportData.getVelocity() + " km/h");
        if (reportData.getCauseId() != null) {

        if (reportData.getCauseId().size() > 0) {
            // tvReason.setText(reportData.getCauseId().get(0));
        }

        if (reportData.getUser().getAvatar() != null) {
            new ImageDownloader(imgAvatar).execute(reportData.getUser().getAvatar());
        }
        if (reportData.getDescription() == null) {
            tvDescription.setText(reportData.getDescription());
        }
        tvScore.setText(String.valueOf(reportData.getUser().getReputation()));

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null && currReportData != null) {
                    onItemClickedListener.onItemClicked(btnRating.getId(), v, currReportData);
                }
            }
        });
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickedListener.onItemClicked(img_list.getId(), v, currReportData);
            }
        });
    }
}
}
