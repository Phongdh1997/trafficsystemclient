package com.hcmut.admin.bktrafficsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.MyVoucher;

import java.text.DecimalFormat;
import java.util.List;

public class MyVoucherAdapter extends RecyclerView.Adapter<MyVoucherAdapter.MyVoucherViewHolder>{
    private Context mContext;
    private List<MyVoucher> orderList;
    private MyVoucher currentOrder;

    private MyVoucherAdapter.OrderAdapterOnClickHandler clickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface OrderAdapterOnClickHandler {
        void onClick(MyVoucher order);
    }

    public MyVoucherAdapter(Context mContext, List<MyVoucher> orderList, MyVoucherAdapter.OrderAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        this.orderList = orderList;
        this.clickHandler = clickHandler;
    }





    @NonNull
    @Override
    public MyVoucherAdapter.MyVoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View voucherView =
                inflater.inflate(R.layout.list_my_voucher, parent, false);

        MyVoucherAdapter.MyVoucherViewHolder viewHolder = new MyVoucherAdapter.MyVoucherViewHolder(voucherView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVoucherAdapter.MyVoucherViewHolder holder, int position) {
        currentOrder = orderList.get(position);

//        DecimalFormat formatter = new DecimalFormat("#,###,###");
//        String formattedPrice = formatter.format(currentOrder.getProductPrice());
        holder.nameMyVoucher.setText(currentOrder.getProductName());
        holder.priceMyVoucher.setText(currentOrder.getProductPrice() + " điểm");

        holder.numberMyVoucher.setText(currentOrder.getOrderNumber());
        holder.dateMyVoucher.setText(currentOrder.getOrderDate());

    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0;
        }
        return orderList.size();
    }

    class MyVoucherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Create view instances
        public TextView nameMyVoucher;
        public TextView priceMyVoucher;
        public TextView dateMyVoucher;
        public TextView numberMyVoucher;


        public MyVoucherViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameMyVoucher = itemView.findViewById(R.id.nameMyVoucher);
            priceMyVoucher = itemView.findViewById(R.id.priceMyVoucher);
            numberMyVoucher = itemView.findViewById(R.id.numberMyvoucher);
            dateMyVoucher = itemView.findViewById(R.id.dateMyvoucher);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // Get position of the product
            currentOrder = orderList.get(position);
            // Send product through click
            clickHandler.onClick(currentOrder);
        }
    }
}
