package com.hcmut.admin.bktrafficsystem.ui.voucher;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.adapter.TestVoucherAdapter;
import com.hcmut.admin.bktrafficsystem.model.Voucher;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

import java.util.ArrayList;

public class AllVoucherFragment extends Fragment implements TestVoucherAdapter.ProductAdapterOnClickHandler, MapActivity.OnBackPressCallback {

    TestVoucherAdapter allVoucherAdapter;
    ArrayList<Voucher> listVoucher;
    RecyclerView listViewAll;
    private Toolbar toolbar;
    @Override
    public void onClick(Voucher voucher) {
        NavHostFragment.findNavController(AllVoucherFragment.this)
                .navigate(R.id.action_allVoucherFragment_to_detailVoucherFragment);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_all_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbarall);
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
        listVoucher = new ArrayList<>();
        listVoucher.add(new Voucher(1, "Giảm giá 1", 500,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listVoucher.add(new Voucher(2, "Giảm giá 2", 600,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listVoucher.add(new Voucher(3, "Giảm giá 3", 700,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listVoucher.add(new Voucher(4, "Giảm giá 4", 800,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listVoucher.add(new Voucher(5, "Giảm giá 5", 900,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        allVoucherAdapter=new TestVoucherAdapter(listVoucher,getContext(),AllVoucherFragment.this);
        listViewAll = view.findViewById(R.id.allvoucher);
        setUpRecycleView();
        listViewAll.setAdapter(allVoucherAdapter);
    }
    private void setUpRecycleView() {
//        listViewAll.setHasFixedSize(true);
//        listViewAll.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        listViewAll.setItemAnimator(null);
    }


    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(AllVoucherFragment.this).popBackStack();
    }
}
