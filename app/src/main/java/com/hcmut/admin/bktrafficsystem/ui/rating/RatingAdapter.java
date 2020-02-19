package com.hcmut.admin.bktrafficsystem.ui.rating;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RecyclerViewHolder> {

    private List<ReportResponse> data;
    private List<String> reason = Arrays.asList("Tắc đường", "Ngập lụt", "Vật cản", "Tai Nạn", "Công an", "Đường cấm");
    private OnItemClickListener itemClickListener;
    private Context mContext;

    public RatingAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<ReportResponse> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (data != null) {
            this.data.clear();
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_rating, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RatingAdapter.RecyclerViewHolder holder, final int position) {
        if (data.get(position).getUser().getId().equals(SharedPrefUtils.getUser(mContext).getUserId())) {
            holder.btnRating.setEnabled(false);
        }
        if (position == data.size() - 1) holder.view.setVisibility(View.GONE);

        if (data.get(position).getImages() == null || data.get(position).getImages().size() == 0){
            holder.img_list.setVisibility(View.GONE);
            holder.imageView1.setVisibility(View.GONE);
            holder.imageView2.setVisibility(View.GONE);
            holder.imageView3.setVisibility(View.GONE);
            holder.imageView4.setVisibility(View.GONE);
        }
        else {
            holder.img_list.setVisibility(View.VISIBLE);
            for (int i =0; i < data.get(position).getImages().size() ; i++){
                holder.listImageView.get(i).setVisibility(View.VISIBLE);
                Glide.with(holder.listImageView.get(i).getContext())
                        .load(data.get(position).getImages().get(i))
                        .into(holder.listImageView.get(i));
            }
        }

        holder.tvName.setText(data.get(position).getUser().getName());
        holder.tvSpeed.setText(data.get(position).getVelocity() + " km/h");
        if (data.get(position).getCauseId().size() > 0) {
            holder.tvReasonTitle.setVisibility(View.VISIBLE);
            holder.tvReason.setVisibility(View.VISIBLE);
            holder.tvReason.setText(reason.get(Integer.parseInt(data.get(position).getCauseId().get(0)) - 1));
        } else {
            holder.tvReasonTitle.setVisibility(View.GONE);
            holder.tvReason.setVisibility(View.GONE);
        }

        if (data.get(position).getUser().getAvatar() != null) {
            Glide.with(holder.imgAvatar.getContext()).load(data.get(position).getUser().getAvatar()).into(holder.imgAvatar);
        }
        if (data.get(position).getDescription() == null || data.get(position).getDescription().equals("")) {
            holder.tvDescription.setVisibility(View.GONE);
            holder.tvTitleDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvTitleDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(data.get(position).getDescription());
        }
        if (data.get(position).getUser().getReputation() == 0.0F) {
            holder.tvScore.setVisibility(View.GONE);
        } else {
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.tvScore.setText(String.valueOf(data.get(position).getUser().getReputation()));
        }

        holder.btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.btnRating.getId(),v, position, null);
            }
        });

        holder.img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.img_list.getId(), v, position, data.get(position).getImages());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        LinearLayout img_list;
        Button btnRating;
        ImageView imageView1,imageView2, imageView3,imageView4, imgAvatar;
        View view;
        TextView tvSpeed, tvTitleDescription, tvDescription, tvReason, tvName, tvScore, tvReasonTitle;
        private List<ImageView> listImageView;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            btnRating = itemView.findViewById(R.id.btn_rating);

            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
            imageView4 = itemView.findViewById(R.id.imageView4);

            listImageView = Arrays.asList(imageView1,imageView2,imageView3,imageView4);

            img_list = itemView.findViewById(R.id.img_list);

            imgAvatar = itemView.findViewById(R.id.img_avatar);
            view = itemView.findViewById(R.id.lineView);
            tvSpeed = itemView.findViewById(R.id.tv_speed);
            tvDescription = itemView.findViewById(R.id.tv_more_description);
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvReasonTitle = itemView.findViewById(R.id.tvReason);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvTitleDescription = itemView.findViewById(R.id.tv_title_description);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int id, View view, int position, ArrayList<String> images);
    }

    void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}