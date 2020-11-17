package com.hcmut.admin.bktrafficsystem.ui.voucher.scanqrcode;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.vision.barcode.Barcode;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.AndroidExt;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.voucher.myvoucher.DetailMyVoucherFragment;
import com.hcmut.admin.bktrafficsystem.ui.voucher.transferpoint.TransferOTPFragment;
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRCode extends Fragment implements BarcodeReader.BarcodeReaderListener, MapActivity.OnBackPressCallback {
    private static final String TAG = ScanQRCode.class.getSimpleName();
    private BarcodeReader barcodeReader;
    AndroidExt androidExt = new AndroidExt();
    Toolbar toolbar;
    public ScanQRCode() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barcode, container, false);

        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.barcode_fragment);
        barcodeReader.setListener(this);

        return view;
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
    }

    @Override
    public void onScanned(Barcode barcode) {
        if(barcode.displayValue!=null){
            barcodeReader.pauseScanning();
            confirmQRCode(barcode.displayValue);
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            ((MapActivity) getContext()).hideBottomNav();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Log.e(TAG, "onScanError: " + errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getActivity(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(ScanQRCode.this).popBackStack();
    }
    private void confirmQRCode(String code) {

        RetrofitClient.getApiService().confirmQRCode(SharedPrefUtils.getUser(getContext()).getAccessToken(),code)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                if (response.body().getData() != null) {
                                    androidExt.confirmQRScan(getContext(), "Sử dụng Voucher thành công", new ClickDialogListener.Yes() {
                                        @Override
                                        public void onCLickYes() {
                                            barcodeReader.resumeScanning();
                                            return;
                                        }
                                    }, new ClickDialogListener.No() {
                                        @Override
                                        public void onClickNo() {
                                            onBackPress();
                                        }
                                    });
                                }
                                 else {
                                    androidExt.confirmQRScan(getContext(), "Voucher không tồn tại hoặc đã được sử dụng", new ClickDialogListener.Yes() {
                                        @Override
                                        public void onCLickYes() {
                                            barcodeReader.resumeScanning();
                                            return;
                                        }
                                    }, new ClickDialogListener.No() {
                                        @Override
                                        public void onClickNo() {
                                                onBackPress();
                                        }
                                    });
                                }
                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");
                    }
                });
    }
}
