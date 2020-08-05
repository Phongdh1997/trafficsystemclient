package com.hcmut.admin.bktrafficsystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.Voucher;

import java.text.DecimalFormat;
import java.util.ArrayList;

//public class TestVoucherAdapter extends BaseAdapter {
////    final ArrayList<Voucher> listVoucher;
////
////    public TestVoucherAdapter(ArrayList<Voucher> listVoucher) {
////        this.listVoucher = listVoucher;
////    }
////
////    @Override
////    public int getCount() {
////        //Trả về tổng số phần tử, nó được gọi bởi ListView
////        return listVoucher.size();
////    }
////
////    @Override
////    public Object getItem(int position) {
////        //Trả về dữ liệu ở vị trí position của Adapter, tương ứng là phần tử
////        //có chỉ số position trong listProduct
////        return listVoucher.get(position);
////    }
////
////    @Override
////    public long getItemId(int position) {
////        //Trả về một ID của phần
////        return listVoucher.get(position).getVoucherId();
////    }
////
////    @Override
////    public View getView(int i, View view, ViewGroup viewGroup) {
////
////        View viewProduct;
////        if (view == null) {
////            viewProduct = View.inflate(viewGroup.getContext(), R.layout.voucher_list_item, null);
////        } else viewProduct = view;
////
////        //Bind sữ liệu phần tử vào View
////        Voucher voucher = (Voucher) getItem(i);
////        String voucherName = voucher.getVoucherName();
////        ((TextView) viewProduct.findViewById(R.id.txtVoucherName)).setText(voucherName);
////
////        DecimalFormat formatter = new DecimalFormat("#,###,###");
////        String formattedPrice = formatter.format(voucher.getVoucherValue());
////        ((TextView) viewProduct.findViewById(R.id.txtVoucherValue)).setText(formattedPrice+" điểm");
////        ((ImageView) viewProduct.findViewById(R.id.imgVoucherImage)).setImageResource(R.drawable.voucher1);
////
////        // Load the Product image into ImageView
//////        String imageUrl =voucher.getVoucherImage().replaceAll("\\\\", "/");
//////        Glide.with()
//////                .load(imageUrl)
//////                .into(holder.binding.imgVoucherImage);
//////
//////        Log.d("imageUrl", imageUrl);
////
////
////        return viewProduct;
////    }
////
////
////}
public class TestVoucherAdapter extends RecyclerView.Adapter<TestVoucherAdapter.ViewHolder> {

    final ArrayList<Voucher> listVoucher;
    private Context mContext;
    private ProductAdapterOnClickHandler clickHandler;
    public interface ProductAdapterOnClickHandler {
        void onClick(Voucher voucher);
    }


    public TestVoucherAdapter(ArrayList<Voucher> listVoucher, Context mContext,ProductAdapterOnClickHandler clickHandler) {
        super();

        this.listVoucher = listVoucher;
        this.mContext = mContext;
        this.clickHandler=clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View voucherView =
                inflater.inflate(R.layout.voucher_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(voucherView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = listVoucher.get(position);
        holder.voucherName.setText(voucher.getVoucherName());
        holder.voucherValue.setText(voucher.getVoucherValue()+" điểm");
        holder.imageView.setImageResource(R.drawable.voucher1);

    }





    @Override
    public int getItemCount() {
        return listVoucher.size();
    }

    /**
     * Lớp nắm giữ cấu trúc view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView voucherName;
        public TextView voucherValue;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            voucherName = itemView.findViewById(R.id.txtVoucherName);
            voucherValue = itemView.findViewById(R.id.txtVoucherValue);
            imageView = itemView.findViewById(R.id.imgVoucherImage);

        }

        @Override
        public void onClick(View view) {
            System.out.println("sfsdaf");
            int position = getAdapterPosition();
            Voucher voucher =listVoucher.get(position);
            switch (view.getId()) {
                case R.id.card_view:
                    clickHandler.onClick(voucher);
            }
        }
    }



}
