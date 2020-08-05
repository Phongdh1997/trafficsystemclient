package com.hcmut.admin.bktrafficsystem.ui.voucher;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.adapter.TestVoucherAdapter;
import com.hcmut.admin.bktrafficsystem.model.Voucher;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;

public class ResultFragment extends Fragment {
    RecyclerView listOfSearchedList;
    TestVoucherAdapter searchAdapter;
    ArrayList<Voucher> listSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listOfSearchedList= view.findViewById(R.id.listOfSearchedList);
        Search("thanh");
    }
    private void Search(String query) {

      listOfSearchedList.setHasFixedSize(true);
      listOfSearchedList.setLayoutManager(new GridLayoutManager(getContext(), (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 4));

        listSearch = new ArrayList<>();
        listSearch.add(new Voucher(1, "Giảm giá 1", 500,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listSearch.add(new Voucher(2, "Giảm giá 2", 600,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listSearch.add(new Voucher(3, "Giảm giá 3", 700,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listSearch.add(new Voucher(4, "Giảm giá 4", 800,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
        listSearch.add(new Voucher(5, "Giảm giá 5", 900,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));

      searchAdapter = new TestVoucherAdapter( listSearch,
              getContext(),
              new TestVoucherAdapter.ProductAdapterOnClickHandler() {

                  @Override
                  public void onClick(Voucher voucher) {
//              NavHostFragment.findNavController(ResultFragment.this)
//                      .navigate(R.id.action_allVoucherFragment_to_detailVoucherFragment);

                  };


              });
      listOfSearchedList.setAdapter(searchAdapter);
    }
}
