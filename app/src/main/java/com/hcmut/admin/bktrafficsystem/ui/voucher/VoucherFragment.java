package com.hcmut.admin.bktrafficsystem.ui.voucher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.adapter.TestVoucherAdapter;
import com.hcmut.admin.bktrafficsystem.adapter.VoucherAdapter;
import com.hcmut.admin.bktrafficsystem.model.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.Voucher;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.InfoVoucher;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.LoginResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.SliderResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.VoucherResponse;
import com.hcmut.admin.bktrafficsystem.ui.account.AccountFragment;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.signin.SignInActivity;
import com.hcmut.admin.bktrafficsystem.ui.signup.SignUpActivity;
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;
import com.hcmut.admin.bktrafficsystem.util.Slide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoucherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherFragment extends Fragment implements MapActivity.OnBackPressCallback, TestVoucherAdapter.ProductAdapterOnClickHandler,View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ViewFlipper imageSlider;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView txtAllTop;
    private TextView txtAllTrending;
    private NavigationView navigationView;
    private CircleImageView userImage;
    private TextView userName;
    private TextView userPoint;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TestVoucherAdapter trendingAdapter;
    TestVoucherAdapter topAdapter;
    ArrayList<VoucherResponse> listTopVoucher;
    ArrayList<VoucherResponse> listTrendVoucher;
    RecyclerView listViewTop;
    RecyclerView listViewTrending;
    TextView txtSearch;
    Bundle bundle = new Bundle();
    AndroidExt androidExt = new AndroidExt();


    public VoucherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoucherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoucherFragment newInstance(String param1, String param2) {
        VoucherFragment fragment = new VoucherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        return inflater.inflate(R.layout.fragment_voucher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageSlider = view.findViewById(R.id.imageSlider);
        toolbar = view.findViewById(R.id.toolbar);
        drawer = view.findViewById(R.id.drawer_layout);
        txtAllTop = view.findViewById(R.id.txtSeeAllTop);
        txtAllTrending = view.findViewById(R.id.txtSeeAllTrending);
        navigationView = view.findViewById(R.id.nav_view);
        txtSearch = view.findViewById(R.id.txtSearch);
        View header = navigationView.getHeaderView(0);
        userImage = header.findViewById(R.id.userImage);
        userName = header.findViewById(R.id.userName);
        userPoint = header.findViewById(R.id.userPoint);





        txtAllTop.setOnClickListener(this);
        txtAllTrending.setOnClickListener(this);
        txtSearch.setOnClickListener(this);

        listTopVoucher = new ArrayList<>();
        listTrendVoucher = new ArrayList<>();
        getVoucherInfo();
        getTopVoucher();
        getTrendVoucher();

//        listVoucher.add(new VoucherResponse("1", "Giảm giá 1", 500,"sfsdf", new Date(),new Date(),"sdfsd",10));
//        listVoucher.add(new Voucher(2, "Giảm giá 2", 600,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(3, "Giảm giá 3", 700,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(4, "Giảm giá 4", 800,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));
//        listVoucher.add(new Voucher(5, "Giảm giá 5", 900,10,"samsung","thiết bị di động","https://mshoagiaotiep.com/theme/frontend/default/images/route-compact.jpg"));

        setUpViews();
        topAdapter = new TestVoucherAdapter(listTopVoucher,getContext(),VoucherFragment.this);
        trendingAdapter = new TestVoucherAdapter(listTrendVoucher,getContext(),VoucherFragment.this);
        listViewTop = view.findViewById(R.id.listOfTop);
        listViewTop.setAdapter(topAdapter);
        listViewTrending = view.findViewById(R.id.listOfTrending);
        listViewTrending.setAdapter(trendingAdapter);
        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        listViewTop.setLayoutManager(layoutManager1);
        listViewTrending.setLayoutManager(layoutManager2);


        addControls(view);
        addEvents();


    }

    private void addControls(View view) {
        try {
            ((MapActivity) view.getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEvents() {
        // TODO: implement voucher feature here
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(VoucherFragment.this).popBackStack();
    }
    private void flipImages(List<SliderResponse> images) {

        for(int i=0;i<images.size();i++) {
            ImageView image = new ImageView(getContext());
            Picasso.get().load(images.get(i).getImage()).into(image);
            imageSlider.addView(image);
        }

        imageSlider.setFlipInterval(2000);


        imageSlider.setAutoStart(true);
        // Set the animation for the view that enters the screen
        imageSlider.setInAnimation(getContext(), R.anim.slide_in_right);
        // Set the animation for the view leaving th screen
        imageSlider.setOutAnimation(getContext(), R.anim.slide_out_left);

        imageSlider.startFlipping();
    }
    private void setUpViews() {

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

//        binding.navView.setNavigationItemSelectedListener(this);
//
//        View headerContainer = binding.navView.getHeaderView(0);
//        circleImageView = headerContainer.findViewById(R.id.profile_image);
//        circleImageView.setOnClickListener(this);
//        TextView userName = headerContainer.findViewById(R.id.nameOfUser);
//        userName.setText(LoginUtils.getInstance(this).getUserInfo().getName());
//        TextView userEmail = headerContainer.findViewById(R.id.emailOfUser);
//        userEmail.setText(LoginUtils.getInstance(this).getUserInfo().getEmail());
//
//        binding.included.content.listOfMobiles.setHasFixedSize(true);
//        binding.included.content.listOfMobiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        binding.included.content.listOfMobiles.setItemAnimator(null);
//
//        binding.included.content.listOfLaptops.setHasFixedSize(true);
//        binding.included.content.listOfLaptops.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        binding.included.content.listOfLaptops.setItemAnimator(null);
//
//        binding.included.content.historyList.setHasFixedSize(true);
//        binding.included.content.historyList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        binding.included.content.historyList.setItemAnimator(null);
//



//        historyAdapter = new ProductAdapter(this, this);
//
//        if (historyIsDeleted) {
//            binding.included.content.textViewHistory.setVisibility(View.GONE);
//        }
    }


    @Override
    public void onClick(VoucherResponse voucher) {

        bundle.putString("idVoucher", voucher.getId());
        NavHostFragment.findNavController(VoucherFragment.this)
                .navigate(R.id.action_voucherFragment_to_detailVoucherFragment,bundle);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSeeAllTop:
                bundle.putInt("type", 0);
                NavHostFragment.findNavController(VoucherFragment.this)
                        .navigate(R.id.action_voucherFragment_to_allVoucherFragment,bundle);
                break;
            case R.id.txtSeeAllTrending:
                bundle.putInt("type", 1);
                NavHostFragment.findNavController(VoucherFragment.this)
                        .navigate(R.id.action_voucherFragment_to_allVoucherFragment,bundle);
                break;
            case R.id.txtSearch:
                NavHostFragment.findNavController(VoucherFragment.this)
                        .navigate(R.id.action_voucherFragment_to_searchFragment);

        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_trackOrder) {
            NavHostFragment.findNavController(VoucherFragment.this)
                    .navigate(R.id.action_voucherFragment_to_MyVoucherFragment);
        } else if (id == R.id.nav_myAccount) {
            NavHostFragment.findNavController(VoucherFragment.this)
                    .navigate(R.id.action_voucherFragment_to_ChooseTransferFragment);

        } else if (id == R.id.nav_newsFeed) {
            NavHostFragment.findNavController(VoucherFragment.this)
                    .navigate(R.id.action_voucherFragment_to_BuyPointFragment);
        } else if (id == R.id.nav_report) {
            NavHostFragment.findNavController(VoucherFragment.this)
                    .navigate(R.id.action_voucherFragment_to_reportFragment);
        } else if (id == R.id.nav_wishList) {
            NavHostFragment.findNavController(VoucherFragment.this)
                    .navigate(R.id.action_voucherFragment_to_HistoryFragment);
        }else if(id == R.id.nav_scan){
            NavHostFragment.findNavController(VoucherFragment.this)
                    .navigate(R.id.action_voucherFragment_to_ScanQRCode);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getTopVoucher(){
        RetrofitClient.getApiService().getTopVoucher()
                .enqueue(new Callback<BaseResponse<List<VoucherResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<VoucherResponse>>> call, Response<BaseResponse<List<VoucherResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                List<VoucherResponse> voucherResponse = response.body().getData();
                                listTopVoucher.clear();
                                listTopVoucher.addAll(voucherResponse);
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<VoucherResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
    private void getTrendVoucher(){
        RetrofitClient.getApiService().getTrendVoucher()
                .enqueue(new Callback<BaseResponse<List<VoucherResponse>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<VoucherResponse>>> call, Response<BaseResponse<List<VoucherResponse>>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                List<VoucherResponse> voucherResponse = response.body().getData();
                                listTrendVoucher.clear();
                                listTrendVoucher.addAll(voucherResponse);
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<List<VoucherResponse>>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }
    private void getVoucherInfo(){
        RetrofitClient.getApiService().getInfoVoucher(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse<InfoVoucher>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<InfoVoucher>> call, Response<BaseResponse<InfoVoucher>> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                InfoVoucher voucherResponse = response.body().getData();
                                userName.setText(voucherResponse.getName());
                                userPoint.setText("Điểm: "+voucherResponse.getPoint());

                                flipImages(voucherResponse.getSlider());

                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<InfoVoucher>> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }



}
