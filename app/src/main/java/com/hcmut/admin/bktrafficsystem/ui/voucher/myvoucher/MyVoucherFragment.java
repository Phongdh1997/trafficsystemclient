package com.hcmut.admin.bktrafficsystem.ui.voucher.myvoucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.adapter.MyVoucherAdapter;
import com.hcmut.admin.bktrafficsystem.model.MyVoucher;
import com.hcmut.admin.bktrafficsystem.model.Voucher;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.voucher.DetailVoucherFragment;
import com.hcmut.admin.bktrafficsystem.ui.voucher.VoucherFragment;

import java.util.ArrayList;

public class MyVoucherFragment extends Fragment implements MyVoucherAdapter.OrderAdapterOnClickHandler, MapActivity.OnBackPressCallback {
    MyVoucherAdapter myVoucherAdapter;
    ArrayList<MyVoucher> listVoucher;
    RecyclerView listViewAll;
    Toolbar toolbar;
    @Override
    public void onClick(MyVoucher order) {
        NavHostFragment.findNavController(MyVoucherFragment.this)
                .navigate(R.id.action_myVoucherFragment_to_detailMyVoucherFragment);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_voucher, container, false);
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
        listViewAll = view.findViewById(R.id.myVoucherList);
        setUpRecycleView();

        getOrders();

    }
    private void setUpRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listViewAll.setLayoutManager(layoutManager);
        listViewAll.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        listViewAll.addItemDecoration(dividerItemDecoration);
    }

    private void getOrders() {
        listVoucher = new ArrayList<>();
        listVoucher.add(new MyVoucher(1, "Giảm giá 1", "1234567","1/1/2020",500,"1/1/2020","thanh"));
        listVoucher.add(new MyVoucher(2, "Giảm giá 2", "9876543","1/1/2020",600,"1/1/2020","thanh"));
        listVoucher.add(new MyVoucher(3, "Giảm giá 3", "1234789","1/1/2020",700,"1/1/2020","thanh"));
        listVoucher.add(new MyVoucher(4, "Giảm giá 4", "4564564","1/1/2020",800,"1/1/2020","thanh"));
        listVoucher.add(new MyVoucher(5, "Giảm giá 5", "2342343","1/1/2020",900,"1/1/2020","thanh"));
            myVoucherAdapter = new MyVoucherAdapter(getContext(), listVoucher,this);
            listViewAll.setAdapter(myVoucherAdapter);
        myVoucherAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(MyVoucherFragment.this).popBackStack();
    }
}
