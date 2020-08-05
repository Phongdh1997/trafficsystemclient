package com.hcmut.admin.bktrafficsystem.ui.voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.voucher.myvoucher.DetailMyVoucherFragment;

public class PayVoucherFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Toolbar toolbar;
    Button btnPay;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payButton:
                NavHostFragment.findNavController(PayVoucherFragment.this)
                        .navigate(R.id.action_payVoucherFragment_to_PaySuccessVoucherFragment);
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar_voucher);
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
        btnPay = view.findViewById(R.id.payButton);
        btnPay.setOnClickListener(this);
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(PayVoucherFragment.this).popBackStack();
    }
}
