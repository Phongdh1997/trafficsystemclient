package com.hcmut.admin.bktrafficsystem.ui.voucher.transferpoint;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.AndroidExt;
import com.hcmut.admin.bktrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.bktrafficsystem.repository.remote.model.response.VoucherResponse;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.voucher.ResultFragment;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferPointFragment extends Fragment implements View.OnClickListener, MapActivity.OnBackPressCallback {
    Button btnConfirm;
    EditText numberPoint;
    Toolbar toolbar;
    CircleImageView avatar;
    TextView name;
    TextView phone;
    EditText point;
    EditText content;
    Bundle bundle = new Bundle();

    AndroidExt androidExt = new AndroidExt();
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                getMessageAuthentication();
                break;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        numberPoint = view.findViewById(R.id.numberPoint);
        avatar = view.findViewById(R.id.avatarTransfer);
        name = view.findViewById(R.id.nameUser);
        phone = view.findViewById(R.id.phoneUser);
        point = view.findViewById(R.id.numberPoint);
        content = view.findViewById(R.id.contentTransfer);
        name.setText(getArguments().getString("name"));
        phone.setText(getArguments().getString("phone"));
        point.setHint("Số dư có thể chuyển: "+getArguments().getInt("point")+ " điểm");

        btnConfirm.setEnabled(false);
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
        setButtonChange();
    }

    @Override
    public void onBackPress() {
        NavHostFragment.findNavController(TransferPointFragment.this).popBackStack();
    }
    public void setButtonChange(){
        numberPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    btnConfirm.setEnabled(true);
                }
                else{
                    btnConfirm.setEnabled(false);
                }

            }
        });
    }
    private void getMessageAuthentication(){
        RetrofitClient.getApiService().getMessageAuthentication(SharedPrefUtils.getUser(getContext()).getAccessToken())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                bundle.putString("point",point.getText().toString());
                                bundle.putString("message",content.getText().toString());
                                bundle.putString("receive",getArguments().getString("id"));
                                NavHostFragment.findNavController(TransferPointFragment.this)
                                        .navigate(R.id.action_transferPointFragment_to_transferOTPFragment,bundle);

                            } else {
                                androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                            }
                        } else {
                            androidExt.showErrorDialog(getContext(), "Có lỗi, vui lòng thông báo cho admin");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        androidExt.showErrorDialog(getContext(), "Kết nối thất bại, vui lòng kiểm tra lại");                    }
                });
    }
}
