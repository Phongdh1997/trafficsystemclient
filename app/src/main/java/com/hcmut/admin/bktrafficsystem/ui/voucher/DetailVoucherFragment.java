package com.hcmut.admin.bktrafficsystem.ui.voucher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.User;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.LoginResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.VoucherResponse;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.signin.SignInActivity;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailVoucherFragment extends Fragment implements MapActivity.OnBackPressCallback,View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private androidx.appcompat.widget.Toolbar toolbar;
    private Button btnBuy;
    private TextView name;
    private TextView price;
    private TextView content;
    private VoucherResponse detailVoucher;
    Bundle bundle = new Bundle();

    AndroidExt androidExt = new AndroidExt();
    public static DetailVoucherFragment newInstance(String param1, String param2) {
        DetailVoucherFragment fragment = new DetailVoucherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        return inflater.inflate(R.layout.fragment_detail_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbardetail);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });
        btnBuy = view.findViewById(R.id.buy);
        name = view.findViewById(R.id.nameOfVoucher);
        price = view.findViewById(R.id.priceOfVoucher);
        content = view.findViewById(R.id.txtDetailContent);
        btnBuy.setOnClickListener(this);

        getDetailVoucher(getArguments().getString("idVoucher"));
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(DetailVoucherFragment.this).popBackStack();
    }

    private void getProductDetails() {
        // Receive the product object
//        product = getIntent().getParcelableExtra(PRODUCT);
//
//        Log.d(TAG,"isFavourite " + product.isFavourite() + " isInCart " + product.isInCart());

        // Set Custom ActionBar Layout
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
////        actionBar.setCustomView(R.layout.action_bar_title_layout);
//        ((TextView) findViewById(R.id.action_bar_title)).setText(product.getProductName());
//
//        binding.nameOfProduct.setText(product.getProductName());
//        binding.priceOfProduct.setText(String.valueOf(product.getProductPrice()));
//
//        String imageUrl = LOCALHOST + product.getProductImage().replaceAll("\\\\", "/");
//        Glide.with(this)
//                .load(imageUrl)
//                .into(binding.imageOfProduct);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buy:
                bundle.putString("idVoucher", getArguments().getString("idVoucher"));
                NavHostFragment.findNavController(DetailVoucherFragment.this)
                        .navigate(R.id.action_detailVoucherFragment_to_PayVoucherFragment,bundle);
                break;
        }
    }
    private void getDetailVoucher(String id){
        RetrofitClient.getApiService().getDetailVoucher(id)
                .enqueue(new Callback<BaseResponse<VoucherResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<VoucherResponse>> call, Response<BaseResponse<VoucherResponse>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                detailVoucher = response.body().getData();
                                System.out.println(response.body().getData());
                                name.setText(detailVoucher.getName());
                                price.setText(detailVoucher.getValue()+" điểm");
                                content.setText(detailVoucher.getContent());
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<VoucherResponse>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }

}
