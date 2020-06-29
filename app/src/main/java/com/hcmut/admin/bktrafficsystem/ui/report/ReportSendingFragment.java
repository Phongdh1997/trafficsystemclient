package com.hcmut.admin.bktrafficsystem.ui.report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.SearchDirectionHandler;
import com.hcmut.admin.bktrafficsystem.model.SearchPlaceHandler;
import com.hcmut.admin.bktrafficsystem.model.param.FastReport;
import com.hcmut.admin.bktrafficsystem.model.param.ReportRequest;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.LocationCollectionManager;
import com.hcmut.admin.bktrafficsystem.ui.SearchInputView;
import com.hcmut.admin.bktrafficsystem.ui.home.HomeFragment;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.callback.SearchPlaceResultHandler;
import com.hcmut.admin.bktrafficsystem.ui.searchplace.callback.SearchResultCallback;
import com.hcmut.admin.bktrafficsystem.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportSendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportSendingFragment extends Fragment implements SearchResultCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final int MAX_PHOTO_TOTAL = 4;

    private AutocompletePrediction searchPlaceResult;
    private LatLng selectedMapPoint;
    private LatLng reportLatLng;
    private boolean isHaveSearchResult = false;

    private TextView btnYourLocation;
    private TextView btnChooseOnMap;
    private TextView btnFastReport;
    private SearchInputView searchInputView;

    private EditText txtNote;
    private SeekBar sbSpeed;
    private TextView txtSpeed;
    private TextView txtAddImage;
    private LinearLayout llImageContainer;
    private TextView txtReview;
    private Spinner snReason;

    private ReportRequest reportRequest;
    private int photoCount = 0;

    private GoogleMap map;

    public ReportSendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportSendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportSendingFragment newInstance(String param1, String param2) {
        ReportSendingFragment fragment = new ReportSendingFragment();
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
    public void onResume() {
        super.onResume();
        if (isHaveSearchResult) {
            handleSearchResult();
        } else {
            searchInputView.updateView();
        }
    }

    private void handleSearchResult() {
        if (searchPlaceResult != null) {
            String addressString = searchPlaceResult.getSecondaryText(null).toString();
            searchInputView.setTxtSearchInputText(addressString);
            reportLatLng = SearchDirectionHandler.addressStringToLatLng(getContext(), addressString);
        }
        if (selectedMapPoint != null) {
            searchInputView.setTxtSearchInputText("Ghim vị trí");
            reportLatLng = selectedMapPoint;
        }
        searchInputView.handleBackAndClearView(true);
        postReport(reportLatLng);
        selectedMapPoint = null;
        searchPlaceResult = null;
        isHaveSearchResult = false;
    }

    private void postReport(LatLng latLng) {
        // search place and set marker
        if (latLng != null) {
            // createMarker(latLng);
            Toast.makeText(getContext(), "Ok, " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Kết nối thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapActivity) {
            MapActivity mapActivity = (MapActivity) context;
            mapActivity.addMapReadyCallback(new MapActivity.OnMapReadyListener() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CameraPhoto.unRegisterPhotoUploadCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_sending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportRequest = new ReportRequest();
        btnYourLocation = view.findViewById(R.id.btnYourLocation);
        btnChooseOnMap = view.findViewById(R.id.btnChooseOnMap);
        btnFastReport = view.findViewById(R.id.btnFastReport);
        searchInputView = view.findViewById(R.id.searchInputView);

        sbSpeed = view.findViewById(R.id.sbSpeed);
        txtNote = view.findViewById(R.id.txtNote);
        txtSpeed = view.findViewById(R.id.txtSpeed);
        txtAddImage = view.findViewById(R.id.txtAddImage);
        llImageContainer = view.findViewById(R.id.llImageContainer);
        txtReview = view.findViewById(R.id.txtReview);
        snReason = view.findViewById(R.id.snReason);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.reason_spinner_item,
                ReportRequest.reasons);
        snReason.setAdapter(adapter);
        snReason.setSelection(0);

        addEvents(view);
    }

    private void addEvents(final View view) {
        searchInputView.setTxtSearchInputEvent(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SearchPlaceResultHandler.getInstance()
                            .addSearchPlaceResultListener(ReportSendingFragment.this);
                    Bundle bundle = new Bundle();
                    bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.NORMAL_SEARCH);
                    NavHostFragment.findNavController(ReportSendingFragment.this)
                            .navigate(R.id.action_reportSendingFragment_to_searchPlaceFragment2, bundle);
                }
            }
        });
        searchInputView.setImgClearTextEvent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInputView.handleBackAndClearView(false);
            }
        });
        searchInputView.setImgBackEvent(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInputView.handleBackAndClearView(false);
            }
        });
        btnYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapUtil.checkGPSTurnOn(getActivity(), MapActivity.androidExt)) {
                    LocationCollectionManager.getInstance(getContext())
                            .getCurrentLocation(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        selectedMapPoint = new LatLng(location.getLatitude(), location.getLongitude());
                                        postReport(selectedMapPoint);
                                    } else {
                                        postReport(null);
                                    }
                                }
                            });
                }
            }
        });
        btnChooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchPlaceResultHandler.getInstance().addSearchPlaceResultListener(ReportSendingFragment.this);
                Bundle bundle = new Bundle();
                bundle.putInt(SearchPlaceResultHandler.SEARCH_TYPE, SearchPlaceResultHandler.SELECTED_BEGIN_SEARCH);
                NavHostFragment.findNavController(ReportSendingFragment.this)
                        .navigate(R.id.action_reportSendingFragment_to_pickPointOnMapFragment2, bundle);
            }
        });
        btnFastReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastReport.postFastReport(getActivity(), MapActivity.androidExt);
            }
        });

        sbSpeed.setMax(100);
        sbSpeed.setProgress(20);
        txtSpeed.setText(String.valueOf(20));
        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
                txtSpeed.setText(String.valueOf(progressValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        txtAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoCount < MAX_PHOTO_TOTAL) {
                    CameraPhoto.checkPermission(getActivity());
                } else {
                    Toast.makeText(getContext(),
                            "Bạn chỉ có thể thêm tối đa " + MAX_PHOTO_TOTAL + " hình ảnh",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        CameraPhoto.registerPhotoUploadCallback(new CameraPhoto.PhotoUploadCallback() {
            @Override
            public void onUpLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                    imageView.setImageBitmap(bitmap);
                    imageView.setLayoutParams(params);
                    llImageContainer.addView(imageView);
                    photoCount++;
                }
            }

            @Override
            public void onUpLoadFail() {
                Toast.makeText(getContext(),
                        "Không thể tải ảnh lên, vui lòng thử lại",
                        Toast.LENGTH_SHORT).show();
            }
        });
        txtReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectReportData();
            }
        });
    }

    private void collectReportData() {
        reportRequest.setDescription(txtNote.getText().toString());
        reportRequest.setVelocity(sbSpeed.getProgress());
        reportRequest.setCauses(Arrays.asList(ReportRequest.reasons[snReason.getSelectedItemPosition()]));
        //reportRequest.setNextLat(reportLatLng.latitude);
        //reportRequest.setNextLng(reportLatLng.longitude);
        //reportRequest.setCurrentLat();
        //reportRequest.setCurrentLng();
        //reportRequest.setImages();
    }

    @Override
    public void onSearchResultReady(AutocompletePrediction result) {
        searchPlaceResult = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onBeginSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onEndSearchPlaceResultReady(AutocompletePrediction result) {

    }

    @Override
    public void onSelectedBeginSearchPlaceResultReady(LatLng result) {
        selectedMapPoint = result;
        isHaveSearchResult = true;
    }

    @Override
    public void onSelectedEndSearchPlaceResultReady(LatLng result) {

    }
}
