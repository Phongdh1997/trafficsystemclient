package com.hcmut.admin.bktrafficsystem.ui.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.maps.model.SnappedPoint;
import com.hcmut.admin.bktrafficsystem.MyApplication;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.ApiService;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.DirectionFinder;
import com.hcmut.admin.bktrafficsystem.model.DirectionFinderListener;
import com.hcmut.admin.bktrafficsystem.model.GoogleSignInData;
import com.hcmut.admin.bktrafficsystem.model.MapUtils;
import com.hcmut.admin.bktrafficsystem.model.PlaceAutoCompleteAdapter;
import com.hcmut.admin.bktrafficsystem.model.RoadFinder;
import com.hcmut.admin.bktrafficsystem.model.Route;
import com.hcmut.admin.bktrafficsystem.model.param.FastReport;
import com.hcmut.admin.bktrafficsystem.model.point.Point;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.Coord;
import com.hcmut.admin.bktrafficsystem.model.response.DirectRespose;
import com.hcmut.admin.bktrafficsystem.model.response.NearSegmentResponse;
import com.hcmut.admin.bktrafficsystem.model.response.PatchNotiResponse;
import com.hcmut.admin.bktrafficsystem.model.response.TrafficReportResponse;
import com.hcmut.admin.bktrafficsystem.model.response.TrafficStatusResponse;
import com.hcmut.admin.bktrafficsystem.model.user.User;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.CallPhone;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.model.ImageDownloader;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.service.AppForegroundService;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.GpsDataSettingSharedRefUtil;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.LocationCollectionManager;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.LocationServiceAlarmUtil;
import com.hcmut.admin.bktrafficsystem.ui.InformationActivity;
import com.hcmut.admin.bktrafficsystem.ui.LoginActivity;
import com.hcmut.admin.bktrafficsystem.ui.UserInputFormFragment;
import com.hcmut.admin.bktrafficsystem.ui.question.QuestionActivity;
import com.hcmut.admin.bktrafficsystem.ui.rating.RatingActivity;
import com.hcmut.admin.bktrafficsystem.ui.rating.detailReport.DetailReportActivity;
import com.hcmut.admin.bktrafficsystem.ui.report.ViewReportFragment;
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.bktrafficsystem.util.LocationRequire;
import com.hcmut.admin.bktrafficsystem.util.LocationUtil;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;
import com.hcmut.admin.bktrafficsystem.util.Sound;
import com.hcmut.admin.bktrafficsystem.util.TimeUtil;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hcmut.admin.bktrafficsystem.util.LocationUtil.getAddressByLatLng;

/**
 * Created by User on 10/2/2017.
 */

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        DirectionFinderListener,
        DatePickerDialog.OnDateSetListener,
        View.OnClickListener {

    private static final String TAG = "MapActivity";

    public static final int REQUEST_CODE_UPDATE_INFO = 105;
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final float DEFAULT_ZOOM = 15f;

    private static final float MAP_LOAD_RANGE = 500; // meter

    private LocationRequire locationRequire = null;
    public static User currentUser;
    //varsm
    private Boolean mLocationPermissionsGranted = false;
    public static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> allPolylineTraffic = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<Point> listPositionAround = new ArrayList<>();
    private ArrayList<Marker> markersAround = new ArrayList<>();
    private static int TYPING_MODE = 0;
    private static int VOICE_MODE = 1;
    private int currentMode = TYPING_MODE;
    private Marker infoOldMarker;

    private MapUtils mapUtils;
    private UserInputFormFragment inputFormFragment;
    boolean isInputFormOpen = false;

    //map interaction vars
    private int mapInteractionStep = 0;  //0 => NO INTERACTION
    private LatLng originPos = null;
    private LatLng autoOriginPos = null;
    private LatLng desPos = null;
    private LatLng autoDesPos = null;
    private Marker originMarker = null;
    private Marker destMarker = null;
    private Marker arrowHeadMarker = null;
    private Marker currentMaker = null;
    private String fullOriginAddress = "";
    private String fullNameAddress = "";
    private Date pressTime;
    AndroidExt androidExt = new AndroidExt();


    //widgets
    private AutoCompleteTextView mSearchEdt, mDestEdt, mStartLocationEditext;
    private TextView usnTxtView, tvDateTime, tv_speed, tv_travel_types;
    private ImageView avtImgView, clrOriginBtn, clrDestBtn, clrStartBtn;
    private CardView customToolbar;
    private String accountType;
    private CardView destinationBox;
    private ConstraintLayout ctlToolbar, interactBox, clReview;
    private AppCompatImageButton btnBackToSearch;
    //Draw and clear markers in interaction mode
    private Spinner travelTypesSpinner;

    //others
    private LatLng oldCameraPos;
    private Polyline polylineRating = null;
    private String userName, imgUrl;
    private ImageView mGuide;
    private FloatingActionButton mGps;

    private DatePickerDialog datePickerDialog = null;
    private TimePickerDialog timePickerDialog = null;
    private String date, resultDate, resultTime;
    private String segmentId = "0";
    private String velocity = "0";
    private boolean isRatingMode = false;

    private String accessToken;
    private Button agreeBtn, disagreeBtn, ratingBtn;
    private SwitchCompat switchCompat;

    private ArrayList<Marker> directsMaker = new ArrayList<>();

    private int avgVelo;
    private float mBearing = 0;
    private ApiService callApi = null;
    private Location currentLocation;
    private String pathId = null;
    private LatLng endFindRoad;
    private boolean isClickPolyline = false;

    private ImageView btnOption;
    private SupportMapFragment mapFragment;

    protected BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String body = intent.getStringExtra("BODY_NOTI");
            Sound.play(MapActivity.this);
            androidExt.showNotifyDialog(MapActivity.this, body, new ClickDialogListener.Yes() {
                @Override
                public void onCLickYes() {
                    Sound.stop(MapActivity.this);
                    LatLng currentPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    if (endFindRoad != null) {
                        clearDirectionMarkers();
                        finDirect(currentPos, endFindRoad);
                    }
                }
            }, new ClickDialogListener.No() {
                @Override
                public void onClickNo() {
                    Sound.stop(MapActivity.this);
                }
            });
        }
    };
    private long current, testmap;
    private MyApplication myapp;

    /*
       ----------------------------RETRIEVE PLACE INFO---------------------------
    */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard(MapActivity.this);
            mSearchEdt.setSelection(0);
            mDestEdt.setSelection(0);
            mStartLocationEditext.setSelection(0);
            mSearchEdt.clearFocus();
            mDestEdt.clearFocus();
            mStartLocationEditext.clearFocus();
            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(position);

//            mapUtils.getPlaceInfoById(placesClient, placeId);

            //Toast.makeText(MapActivity.this, item.getFullText(null), Toast.LENGTH_LONG).show();

            String searchText = item.getFullText(null).toString();
            if (destinationBox.getVisibility() != View.VISIBLE || mDestEdt.getText().toString().length() == 0) {
                //Execute searching method
                Address address = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, searchText);
                if (address != null) {
                    LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
                    if (mapInteractionStep == 1 || mapInteractionStep == 2) {
                        originPos = point;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 19));
                        if (originMarker != null) originMarker.remove();
                        originMarker = mMap.addMarker(new MarkerOptions().position(point).
                                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        originMarker.showInfoWindow();
                        mapInteractionStep = 2;
                        updateInteractionBox(mapInteractionStep);
                    } else if (mapInteractionStep == 3) {
                        RoadFinder roadFinder = new RoadFinder(MapActivity.this);
                        roadFinder.getSnappedPoint(point, false);
                    } else {
                        moveCameraAndAddMarker(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
                    }
                }
            } else if (mDestEdt.getText().toString().length() != 0) {
                Address startAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, mStartLocationEditext.getText().toString());
                Address endAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, mDestEdt.getText().toString());
                if (endAddress != null) {
                    LatLng sPosition = null;
                    if (startAddress != null) {
                        sPosition = new LatLng(startAddress.getLatitude(), startAddress.getLongitude());
                    }
                    LatLng ePosition = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());
                    current = Calendar.getInstance().getTimeInMillis();

                    finDirect(sPosition, ePosition);

                }
            }
        }
    };

    public void setUserReportMarkerListener(ViewReportFragment.OnReportMakerClick listener) {
        reportMakerClickListener = listener;
    }

    public void addMapReadyCallback(@NotNull OnMapReadyListener onMapReadyListener) {
        if (mMap != null) {
            onMapReadyListener.onMapReady(mMap);
        } else {
            onMapReadyListeners.add(onMapReadyListener);
        }
    }

    /**
     * ================================================
     *
     * Declare probe module variable
     *
     * ================================================
     */
    private ViewReportFragment.OnReportMakerClick reportMakerClickListener;
    private List<OnMapReadyListener> onMapReadyListeners = new ArrayList<>();

    private ProbeForgroundServiceManager appForgroundServiceManager;
    private AppFeaturePopup appFeaturePopup;
    private ProbeMapUi probeMapUi;

    private Switch btnGPSColectionSwitch;
    private Switch btnRatingModeSwitch;
    private Button btnCallPhoneReport;
    private Button btnCurrentLocationReport;
    private ImageButton btnMore;
    private LinearLayout layoutMoreFeature;


    private CompoundButton.OnCheckedChangeListener swithCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                initLocationService();
            } else {
                stopLoctionService();
            }
            LocationServiceAlarmUtil.cancelLocationAlarm(getApplicationContext());
        }
    };

    /**
     *
     * Init probe module variable to use
     */
    private void initProbeModuleVariable() {
        appForgroundServiceManager = new ProbeForgroundServiceManager(this);

        appFeaturePopup = new AppFeaturePopup(this);
        btnOption = findViewById(R.id.btnOption);
        btnCallPhoneReport = findViewById(R.id.btnCallPhoneReport);
        btnCurrentLocationReport = findViewById(R.id.btnCurrentLocationReport);
        btnGPSColectionSwitch = findViewById(R.id.btnGPSColectionSwitch);
        btnRatingModeSwitch = findViewById(R.id.btnRatingModeSwitch);
        btnMore = findViewById(R.id.btnMore);
        layoutMoreFeature = findViewById(R.id.layoutMoreFeature);
        boolean gpsDataSetting = GpsDataSettingSharedRefUtil.loadGpsDataSetting(getApplicationContext());
        btnGPSColectionSwitch.setChecked(gpsDataSetting);

        addEvents();
    }

    private void initProbeModuleVariableWhenMapLoaded() {
        probeMapUi = new ProbeMapUi(getApplicationContext(), mMap, mapFragment);
        probeMapUi.setupRenderStatus();
    }

    /**
     *
     * Add probe module Events
     */
    private void addEvents() {
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appFeaturePopup.showPopup();
            }
        });
        btnCallPhoneReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallPhone callPhone = new CallPhone(MapActivity.this);
                callPhone.checkCallPhonePermisstion();
            }
        });
        btnCurrentLocationReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postFastReport();
            }

        });
        btnGPSColectionSwitch.setOnCheckedChangeListener(swithCheckedChangedListener);
        AppForegroundService.getMovingStateLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isMoving) {
                if (isMoving != null) {
                    setGpsDataSetting(isMoving);
                }
            }
        });
        btnRatingModeSwitch.setChecked(isRatingMode);
        btnRatingModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                handleRatingMode(isChecked);
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMoreFeatureOption();
            }
        });

        // show dialog ask user to turn on collect GPS data
        boolean gpsDataSetting = GpsDataSettingSharedRefUtil.loadGpsDataSetting(getApplicationContext());
        if (gpsDataSetting) {
            initLocationService();
        }

        FrameLayout flFragment = findViewById(R.id.flFragment);
        BottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        flFragment.setPadding(0, 0, 0, bottomNavigation.getNavigationHeight());
        bottomNavigation.setMenuItemSelectionListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int menuItemId, int i1, boolean b) {
                switch (menuItemId) {
                    case R.id.homeTab:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(MapActivity.this, R.id.mapFeature).navigate(R.id.mapFeatureFragment);
                        break;
                    case R.id.reportTab:
                        Toast.makeText(getApplicationContext(), "Report", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.viewReportTab:
                        Navigation.findNavController(MapActivity.this, R.id.mapFeature).navigate(R.id.reportView);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
    }

    private void postFastReport() {
        if (checkGPSTurnOn()) {
            LocationCollectionManager.getInstance(getApplicationContext())
                    .getCurrentLocation(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        final String address = LocationUtil.getAddressByLatLng(MapActivity.this,
                                new LatLng(location.getLatitude(), location.getLongitude()));
                        if (address.length() < 1) {
                            androidExt.showErrorDialog(
                                    MapActivity.this,
                                    "Không thể lấy được địa chỉ người dùng, Vui lòng kiểm tra lại đường truyền!");
                            return;
                        }
                        androidExt.comfirmPostFastReport(
                                MapActivity.this,
                                address,
                                new ClickDialogListener.Yes() {
                                    @Override
                                    public void onCLickYes() {
                                        FastReport fastReport = new FastReport();
                                        fastReport.address = address;
                                        fastReport.distance = 200;
                                        fastReport.personSharing = new FastReport.PersonSharing();
                                        fastReport.speed = 15;
                                        fastReport.latitude = location.getLatitude();
                                        fastReport.longitude = location.getLongitude();
                                        CallApi.getBkTrafficService().postFastRecord(fastReport)
                                                .enqueue(new Callback<BaseResponse<Object>>() {
                                                    @Override
                                                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                                                        try {
                                                            if (response.body().getCode() == 201) {
                                                                androidExt.showSuccess(
                                                                        MapActivity.this,
                                                                        "Gửi cảnh báo thành công");
                                                            }
                                                        } catch (Exception e) {
                                                            androidExt.showErrorDialog(
                                                                    MapActivity.this,
                                                                    "Gửi cảnh báo Thất bại, Xin kiểm tra lại kết nối mạng");
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                                                        androidExt.showErrorDialog(
                                                                MapActivity.this,
                                                                "Gửi cảnh báo Thất bại, Xin kiểm tra lại kết nối mạng");
                                                    }
                                                });
                                    }
                                });
                    } else {
                        androidExt.showErrorDialog(MapActivity.this,"Không thể lấy được vị trí người dùng, Vui lòng thử lại!");
                    }
                }
            });
        }
    }

    private void toggleMoreFeatureOption() {
        if (layoutMoreFeature.getVisibility() == View.VISIBLE) {
            layoutMoreFeature.setVisibility(View.GONE);
        } else {
            layoutMoreFeature.setVisibility(View.VISIBLE);
        }
    }

    public void setGpsDataSetting(boolean value) {
        btnGPSColectionSwitch.setOnCheckedChangeListener(null);
        btnGPSColectionSwitch.setChecked(value);
        GpsDataSettingSharedRefUtil.saveGpsDataSetting(getApplicationContext(), value);
        btnGPSColectionSwitch.setOnCheckedChangeListener(swithCheckedChangedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (probeMapUi != null) {
            probeMapUi.startStatusRenderTimer();
        }
    }

    public GoogleMap getGoogleMap() {
        return mMap;
    }

    public void initLocationService() {
        appForgroundServiceManager.initLocationService();
    }

    public void stopLoctionService() {
        ProbeForgroundServiceManager.stopLocationService(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (probeMapUi != null) {
            probeMapUi.stopStatusRenderTimer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ProbeForgroundServiceManager.MUTILE_PERMISSION_REQUEST) {
            if (!appForgroundServiceManager.handleAppForgroundPermission(requestCode, permissions, grantResults)) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else if (requestCode == CallPhone.CALL_PHONE_CODE) {
            boolean isHavePermission = CallPhone.handleCallPhonePermission(grantResults);
            if (isHavePermission) {
                CallPhone.makeAPhoneCall(MapActivity.this);
            }
        }
    }

    ///Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myapp = (MyApplication) this.getApplicationContext();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        SharedPrefUtils.saveNotiToken(MapActivity.this, instanceIdResult.getToken());
                    }
                });

        isRatingMode = SharedPrefUtils.getRatingMode(MapActivity.this);
        User user = SharedPrefUtils.getUser(MapActivity.this);
        mLocationPermissionsGranted = user.getmLocationPermissionsGranted();
        accessToken = user.getAccessToken();
        accountType = user.getAccountType();
        userName = user.getUserName();
        imgUrl = user.getImgUrl();
        currentUser = user;
        Log.e("userID", user.getUserId());

        initView();
        //Init map
        initMap();
        addEventTextChange();

        datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        showTimePickerDialog();

        //register broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationReceiver, new IntentFilter("some_custom_id"));
    }

    @Override
    protected void onResume() {
        myapp.setCurrentActivity(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        clearActivity();
        super.onPause();
    }

    private void clearActivity() {
        Activity currActivity = myapp.getCurrentActivity();
        if (this.equals(currActivity))
            myapp.setCurrentActivity(null);
    }

    private void initView() {
        //Map views
        mSearchEdt = findViewById(R.id.search_edt);
        mStartLocationEditext = findViewById(R.id.start_location_edt);
        mDestEdt = findViewById(R.id.destination_edt);
        mGuide = findViewById(R.id.ic_direction);
        mGps = findViewById(R.id.btnCurrentLocation);
        customToolbar = findViewById(R.id.custom_toolbar);
        clrOriginBtn = customToolbar.findViewById(R.id.ic_origin_clear);
        clrDestBtn = findViewById(R.id.ic_clear_end);
        clrStartBtn = findViewById(R.id.ic_clear_start);
        travelTypesSpinner = findViewById(R.id.travel_types_spinner);
        destinationBox = findViewById(R.id.ctlDestination);
        interactBox = findViewById(R.id.interact_box);
        agreeBtn = findViewById(R.id.agreeBtn);
        disagreeBtn = findViewById(R.id.disagreeBtn);
        ratingBtn = findViewById(R.id.rating_btn);
        clReview = findViewById(R.id.clReview);
        tvDateTime = findViewById(R.id.tv_date_time);
        tv_speed = clReview.findViewById(R.id.tv_speed);
        tv_travel_types = findViewById(R.id.tv_travel_types);
        btnBackToSearch = findViewById(R.id.btnBack);
        ctlToolbar = findViewById(R.id.ctlToolbar);
        interactBox.setOnClickListener(this);
        ctlToolbar.setOnClickListener(this);
        destinationBox.setOnClickListener(this);
        btnBackToSearch.setOnClickListener(this);
        clrOriginBtn.setOnClickListener(this);
        clrStartBtn.setOnClickListener(this);
        clrDestBtn.setOnClickListener(this);
        mGuide.setOnClickListener(this);
        tv_travel_types.setOnClickListener(this);
        clReview.setOnClickListener(this);
        tvDateTime.setOnClickListener(this);

        this.locationRequire = new LocationRequire(this);
        this.locationRequire.start();
        this.locationRequire.setLocationUpdateListener(new LocationRequire.LocationUpdateListener() {
            @Override
            public void onUpdate(LatLng startPos, int avgVelocity) {
                autoOriginPos = startPos;
//                autoDesPos = destinationPos;
                fullNameAddress = getAddressByLatLng(MapActivity.this, autoOriginPos);
                avgVelo = avgVelocity;
            }
        });
        //Init other var
        callApi = CallApi.createService();
        mapUtils = new MapUtils();
        inputFormFragment = new UserInputFormFragment();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.gg_api_key));
        }
        mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, Places.createClient(this), null);

        mSearchEdt.setAdapter(mPlaceAutoCompleteAdapter);
        mSearchEdt.setOnItemClickListener(mAutocompleteClickListener);
        mSearchEdt.setThreshold(1);

        initProbeModuleVariable();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        resultTime = TimeUtil.formatHour(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + TimeUtil.formatMinute(calendar.get(Calendar.MINUTE));
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String parsedDate = df.format(date);
        tvDateTime.setText(parsedDate);
    }

    /**
     * Thực hiện chức năng report của nhóm Minh - Phương
     * Hàm này sẽ được gọi khi có sự kiện click vào button repord trong popup Feature
     */
    public void doReport() {
        if (!isInputFormOpen) {
            isInputFormOpen = true;
            Bundle bundle = new Bundle();
            bundle.putString("accessToken", accessToken);
            inputFormFragment.setArguments(bundle);
            if (mapInteractionStep == 0) {
                FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.inputFormFragmentContainer, inputFormFragment, "FragInputForm").commit();
            } else {
                interactBox.setVisibility(View.GONE);
                clearMapInteractionVars();
                inputFormFragment.uncheckCBoxes();
                showInputForm(inputFormFragment);
            }
            if (destinationBox.getVisibility() == View.VISIBLE) {
                btnBackToSearch.performClick();
            }
            if (currentMaker != null) currentMaker.remove();
            btnOption.setVisibility(View.GONE);
            clReview.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.setMaxZoomPreference(ProbeMapUi.MAX_ZOOM_LEVEL);
        initProbeModuleVariableWhenMapLoaded();

        for (OnMapReadyListener listener : onMapReadyListeners) {
            listener.onMapReady(googleMap);
        }
        onMapReadyListeners.clear();

        updateLocationUI();
        oldCameraPos = mMap.getCameraPosition().target;
        moveToDevicePosition();
        Log.d(TAG, "init: initializing");

        mSearchEdt.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                                || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                            String searchText = mSearchEdt.getText().toString();
                            if (searchText.length() != 0) {
                                clearDirectionMarkers();
                                if (destinationBox.getVisibility() != View.VISIBLE || mDestEdt.getText().toString().length() == 0) {
                                    //Execute searching method
                                    Address address = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, searchText);
                                    if (address != null) {
                                        LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                                        if (mapInteractionStep == 1 || mapInteractionStep == 2) {
                                            originPos = position;
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 19));
                                            if (originMarker != null) originMarker.remove();
                                            originMarker = mMap.addMarker(new MarkerOptions().position(position).
                                                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            originMarker.showInfoWindow();
                                            mapInteractionStep = 2;
                                            updateInteractionBox(mapInteractionStep);
                                        } else {
                                            moveCameraAndAddMarker(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
                                        }
                                    }
                                } else if (mDestEdt.getText().toString().length() != 0) {
                                    Address startAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, searchText);
                                    Address endAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, searchText);
                                    LatLng sPosition = null;
                                    if (startAddress != null) {
                                        sPosition = new LatLng(startAddress.getLatitude(), startAddress.getLongitude());
                                    }
                                    LatLng ePosition = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());
                                    finDirect(sPosition, ePosition);
                                }
                            }
                        }
                        return false;
                    }
                });

        mStartLocationEditext.setAdapter(mPlaceAutoCompleteAdapter);
        mStartLocationEditext.setOnItemClickListener(mAutocompleteClickListener);
        mStartLocationEditext.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH/*|| actionId == EditorInfo.IME_ACTION_DONE
                                || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER*/) {
                            String destination = mDestEdt.getText().toString();
                            if (!destination.isEmpty()) {
                                Address startAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, mStartLocationEditext.getText().toString());
                                Address endAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, destination);
//                                if (startAddress == null) {
//                                    Toast.makeText(MapActivity.this, "Địa điểm bắt đầu không tồn tại", Toast.LENGTH_LONG).show();
//                                    return false;
                                if (endAddress == null) {
                                    Toast.makeText(MapActivity.this, "Địa điểm đến không tồn tại", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                                LatLng sPosition = null;
                                if (startAddress != null) {
                                    sPosition = new LatLng(startAddress.getLatitude(), startAddress.getLongitude());
                                }
                                LatLng ePosition = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());
                                finDirect(sPosition, ePosition);
                            } else {
                                Toast.makeText(MapActivity.this, "Vui lòng nhập điểm đến", Toast.LENGTH_LONG).show();
                            }
                        }
                        return false;
                    }
                });

        mDestEdt.setAdapter(mPlaceAutoCompleteAdapter);
        mDestEdt.setOnItemClickListener(mAutocompleteClickListener);
        mDestEdt.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH/*|| actionId == EditorInfo.IME_ACTION_DONE
                                || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER*/) {
                            String destination = mDestEdt.getText().toString();
                            if (!destination.isEmpty()) {
                                Address startAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, mStartLocationEditext.getText().toString());
                                Address endAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, destination);
//                                if (startAddress == null) {
//                                    Toast.makeText(MapActivity.this, "Địa điểm bắt đầu không tồn tại", Toast.LENGTH_LONG).show();
//                                    return false;
                                if (endAddress == null) {
                                    Toast.makeText(MapActivity.this, "Địa điểm đến không tồn tại", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                                LatLng sPosition = null;
                                if (startAddress != null) {
                                    sPosition = new LatLng(startAddress.getLatitude(), startAddress.getLongitude());
                                }
                                LatLng ePosition = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());
                                finDirect(sPosition, ePosition);
                            } else {
                                Toast.makeText(MapActivity.this, "Vui lòng nhập điểm đến", Toast.LENGTH_LONG).show();
                            }
                        }
                        return false;
                    }
                });

//        mGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                moveToDevicePosition();
//            }
//        });

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mapInteractionStep) {
                    case 2: {
                        mapInteractionStep = 3;
                        updateInteractionBox(mapInteractionStep);
                        break;
                    }
                    case 4: {
                        //send data
                        if (originMarker != null) {
                            if (inputFormFragment != null) {
                                interactBox.setVisibility(View.GONE);
                                btnOption.setVisibility(View.VISIBLE);
                                inputFormFragment.setCurAndDesPos(originPos, fullOriginAddress, desPos);
                                if (currentMaker != null) currentMaker.remove();
                                showInputForm(inputFormFragment);
                            }
                            clearMapInteractionVars();
                        }
                        break;
                    }
                }
            }
        });

        disagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mapInteractionStep) {
                    case 2: {
                        mapInteractionStep = 1;
                        updateInteractionBox(mapInteractionStep);
                        break;
                    }
                    case 4: {
                        mapInteractionStep = 3;
                        updateInteractionBox(mapInteractionStep);
                        break;
                    }
                }
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                isClickPolyline = true;
                mSearchEdt.clearFocus();
                ratingBtn.setEnabled(true);
                if (currentMaker != null) currentMaker.remove();
                if (polylineRating != null) {
                    polylineRating.setWidth(6);
                }
                polylineRating = polyline;
                polyline.setWidth(15);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(polyline.getPoints().get(0));
                builder.include(polyline.getPoints().get(polyline.getPoints().size() - 1));
                LatLngBounds bounds = builder.build();
                int padding = 240; // padding around start and end marker
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
                clReview.setVisibility(View.VISIBLE);
                final TrafficStatusResponse trafficStatus = (TrafficStatusResponse) polyline.getTag();
                if (trafficStatus != null) {
                    velocity = trafficStatus.getVelocity();
                    tv_speed.setText("Vận tốc trung bình: " + trafficStatus.getVelocity() + "km/h");
                    segmentId = trafficStatus.getSegmentId();
                }
            }
        });

        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, RatingActivity.class);
//                        intent.putExtra("LAT", mMap.getCameraPosition().target.latitude);
                intent.putExtra("DATE", tvDateTime.getText().toString());
                intent.putExtra("VELOCITY", velocity);
                intent.putExtra("SEGMENT_ID", segmentId);
                startActivity(intent);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mSearchEdt.clearFocus();
                try {
                    switch (marker.getTag().toString()) {
                        case ViewReportFragment.REPORT_RATING: {
                            if (reportMakerClickListener != null) {
                                reportMakerClickListener.onClick(marker);
                            }
                        }
                    }
                } catch (Exception e) {}
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                mSearchEdt.clearFocus();
//                int index = getValue(marker.getSnippet());
                String reportId = String.valueOf(marker.getTitle());//.substring(index, marker.getTitle().length()));
                Intent intent = new Intent(MapActivity.this, DetailReportActivity.class);
                intent.putExtra("REPORT_ID", reportId);
                startActivity(intent);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng position) {
                mSearchEdt.clearFocus();
                if (infoOldMarker != null) {
                    infoOldMarker.hideInfoWindow();
                    infoOldMarker = null;
                }

                if (currentMaker != null) currentMaker.remove();
                RoadFinder roadFinder = new RoadFinder(MapActivity.this);
                if (polylineRating != null) {
                    polylineRating.setWidth(6);
                }
                if (mapInteractionStep == 0) {
                    roadFinder.getSnappedPoint(position, true);
                }

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng position) {
                mSearchEdt.clearFocus();
                if (infoOldMarker != null) {
                    infoOldMarker.hideInfoWindow();
                    infoOldMarker = null;
                }
                clReview.setVisibility(View.GONE);
//                progressDialog = ProgressDialog.show(MapActivity.this, "", "", true);
                if (currentMaker != null) currentMaker.remove();
                RoadFinder roadFinder = new RoadFinder(MapActivity.this);
                if (polylineRating != null) {
                    polylineRating.setWidth(6);
                }
                if (mapInteractionStep > 0) {
                    roadFinder.getSnappedPoint(position, false);
                }
                if (layoutMoreFeature.getVisibility() == View.VISIBLE) {
                    layoutMoreFeature.setVisibility(View.GONE);
                }
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, R.id.vehicle_txv, getResources().getStringArray(R.array.travel_types_array));
        travelTypesSpinner.setAdapter(spinnerArrayAdapter);

        travelTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tv_travel_types.setText(travelTypesSpinner.getSelectedItem().toString());
                String origin = mStartLocationEditext.getText().toString();
                String destination = mDestEdt.getText().toString();
                if (!origin.isEmpty() && !destination.isEmpty()) {
                    Address startAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, origin);
                    Address endAddress = mapUtils.getLatLngByAddressOrPlaceName(MapActivity.this, destination);
                    if (endAddress == null) return;
                    LatLng sPosition = null;
                    if (startAddress != null) {
                        sPosition = new LatLng(startAddress.getLatitude(), startAddress.getLongitude());
                    }
                    LatLng ePosition = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());
                    finDirect(sPosition, ePosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //Firebase NpatchNotifyotify
//        MyFirebaseMessagingService.setActivity(MapActivity.this);
        patchNotify();
    }

    public void viewUserGuide() {
        startActivity(new Intent(MapActivity.this, QuestionActivity.class));
    }

    public void logout() {
        new AlertDialog.Builder(MapActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn chắc chắn muốn đăng xuất?")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (accountType) {
                            case "facebook": {
                                LoginManager.getInstance().logOut();
                                break;
                            }
                            case "google": {
                                GoogleSignInClient mGoogleSignInClient = GoogleSignInData.getValue();
                                if (mGoogleSignInClient != null) {
                                    mGoogleSignInClient.signOut();
                                }
                                break;
                            }
                        }
                        SharedPrefUtils.saveUser(MapActivity.this, null);
                        Intent intent = new Intent(MapActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }

    public void viewInfo() {
        startActivityForResult(new Intent(MapActivity.this, InformationActivity.class), REQUEST_CODE_UPDATE_INFO);
    }

    private void addEventTextChange() {
        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    clrOriginBtn.setVisibility(View.VISIBLE);
                } else {
                    clrOriginBtn.setVisibility(View.INVISIBLE);
                    if (directsMaker.size() == 2) {
                        directsMaker.get(0).remove();
                        directsMaker.get(1).remove();
                        directsMaker.clear();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }
            }
        });

        mStartLocationEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearDirectionMarkers();
                if (s.length() != 0) {
                    clrStartBtn.setVisibility(View.VISIBLE);
                } else {
                    clrStartBtn.setVisibility(View.INVISIBLE);
                    if (directsMaker.size() == 2) {
                        directsMaker.get(0).remove();
                        directsMaker.get(1).remove();
                        directsMaker.clear();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }
            }
        });

        mDestEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearDirectionMarkers();
                if (s.length() != 0) {
                    clrDestBtn.setVisibility(View.VISIBLE);
                } else {
                    clrDestBtn.setVisibility(View.INVISIBLE);
                    if (directsMaker.size() == 2) {
                        directsMaker.get(0).remove();
                        directsMaker.get(1).remove();
                        directsMaker.clear();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                }
            }
        });
    }

    public void handleRatingMode(boolean isChecked) {
        SharedPrefUtils.saveRatingMode(MapActivity.this, isChecked);
        isRatingMode = isChecked;
        if (isChecked) {
            showReportStatus();
        } else {
            hideReportStatus();
        }
        patchNotify();
    }

    public void updateCurDesLocation() {
        if (autoOriginPos != null) {
            autoDesPos = LocationUtil.getDestinationLocation(autoOriginPos, mBearing);
        }
        inputFormFragment.applyView(autoOriginPos, fullNameAddress, autoDesPos, avgVelo);
    }

    public void updateCurDesLocationFromSensor() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (true) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //get location -> latlng
                            currentLocation = (Location) task.getResult();
                            if (currentLocation == null) {
                                androidExt.showSuccessDialog(MapActivity.this, "Vui lòng bật định vị vị trí", new ClickDialogListener.OK() {
                                    @Override
                                    public void onCLickOK() {
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                });
                                return;
                            }
                            autoOriginPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            fullNameAddress = getAddressByLatLng(MapActivity.this, autoOriginPos);
                            autoDesPos = LocationUtil.getDestinationLocation(autoOriginPos, mBearing);
                            inputFormFragment.setCurAndDesPos(autoOriginPos, fullNameAddress, autoDesPos);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    public void patchNotify() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: location found!");
                        //get location -> latlng
                        currentLocation = (Location) task.getResult();
                        if (currentLocation == null) {
                            return;
                        }
                        String notiAllow = "false";
                        if (isRatingMode)
                            notiAllow = "true";
                        callApi.patchUserNoti(accessToken, SharedPrefUtils.getNotiToken(MapActivity.this), currentLocation.getLatitude(),
                                currentLocation.getLongitude(), notiAllow, null ).enqueue(new Callback<BaseResponse<PatchNotiResponse>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<PatchNotiResponse>> call, Response<BaseResponse<PatchNotiResponse>> response) {
                                if (response.body() != null) {
                                    if (response.body().getData() != null) {
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<PatchNotiResponse>> call, Throwable t) {
                                Toast.makeText(MapActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    private void clearDirectionMarkers() {
        if (originMarkers != null && originMarkers.size() > 0) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null && destinationMarkers.size() > 0) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null && polylinePaths.size() > 0) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "", "Đang tìm đường..!", true);
        if (originMarker != null) originMarker.remove();
        clearDirectionMarkers();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        //Consider first route only
        Route firstRoute = routes.get(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstRoute.startLocation, 16));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(firstRoute.startAddress)
                .position(firstRoute.startLocation)));
        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(firstRoute.endAddress)
                .position(firstRoute.endLocation)));

        PolylineOptions polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);

        for (int i = 0; i < firstRoute.points.size(); i++)
            polylineOptions.add(firstRoute.points.get(i));

        polylinePaths.add(mMap.addPolyline(polylineOptions));
    }

    public String getSelectedVehicle() {
        String vehicle = travelTypesSpinner.getSelectedItem().toString();
        switch (vehicle) {
            case "Đi xe": {
                vehicle = "driving";
                break;
            }
            case "Đi bộ": {
                vehicle = "walking";
                break;
            }
            case "Đi buýt": {
                vehicle = "transit&transit_mode=bus";
                break;
            }
            default: {
                vehicle = "";
                break;
            }
        }
        return vehicle;
    }

    public void findDirection(String origin, String destination) {
        if (originMarker != null) originMarker.remove();
        String vehicle = "driving";
        if (!origin.isEmpty()) {
            if (!destination.isEmpty()) {
                try {
                    new DirectionFinder(MapActivity.this, origin, destination, vehicle).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else
                Toast.makeText(MapActivity.this, "Please enter origin", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(MapActivity.this, "Please enter origin", Toast.LENGTH_LONG).show();
    }

    private void moveCameraAndAddMarker(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (originMarker != null) {
            originMarker.remove();
        }
        //Add a marker
        originMarker = mMap.addMarker(new MarkerOptions().position(latLng).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        originMarker.showInfoWindow();
        hideKeyboard(MapActivity.this);
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionsGranted) mMap.setMyLocationEnabled(true);
            else mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //Get device location and move camera to the location
    private void moveToDevicePosition() {
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            /*   if (mLocationPermissionsGranted) {*/
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));

                        // render user report
                        setupInfoWindow();
                        current = Calendar.getInstance().getTimeInMillis();
                        renderCurrentPosition(oldCameraPos);
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.790063, 106.652666), 13));
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public boolean checkGPSTurnOn () {
        LocationCollectionManager locationCollectionManager = LocationCollectionManager.getInstance(getApplicationContext());
        if (!locationCollectionManager.isGPSEnabled()) {
            androidExt.showDialog(
                    MapActivity.this,
                    "Yêu cầu truy cập vị trí",
                    "Vui lòng bật định vị vị trí để sử dụng chức năng này!",
                    new ClickDialogListener.Yes() {
                        @Override
                        public void onCLickYes() {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            return false;
        }
        return true;
    }

  /*  public void showKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }*/

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*-------------------------Map interaction-------------------------*/
    public void confirmDirection(Fragment fragment, LatLng originPos, LatLng desPos, int currentMode) {
        this.currentMode = currentMode;
        if (this.currentMode == TYPING_MODE) hideInputForm(fragment);
        if (originMarker != null) originMarker.remove();
        if (destMarker != null) destMarker.remove();
        if (arrowHeadMarker != null) arrowHeadMarker.remove();

        this.originPos = originPos;
//        this.desPos = desPos;
        mapInteractionStep = 6;

        RoadFinder roadFinder = new RoadFinder(MapActivity.this);
        roadFinder.getSnappedPoint(originPos, false);
    }

    public void startMapInteraction(Fragment fragment, boolean isCurrPos) {
        //map interacting mode -> typing mode
        currentMode = TYPING_MODE;
        //set up
        hideInputForm(fragment);
        if (isCurrPos) {
            mapInteractionStep = 2;
            updateInteractionBox(mapInteractionStep);
        } else {
            mapInteractionStep = 1;
            updateInteractionBox(mapInteractionStep);
        }
    }

    public void clearMapInteractionVars() {
        mapInteractionStep = 0;
        if (originMarker != null) originMarker.remove();
        if (destMarker != null) destMarker.remove();
        if (arrowMarker != null) arrowMarker.remove();
        originPos = null;
        desPos = null;
    }

    //Input form functions
    public void showInputForm(Fragment inputForm) {
        setVisibilitySearchBar(true);
        isInputFormOpen = true;
        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.show(inputForm).commit();
    }

    public void hideInputForm(Fragment inputForm) {
        isInputFormOpen = false;
        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.hide(inputForm).commit();
    }

    public void removeInputForm(Fragment inputForm) {
        inputFormFragment = new UserInputFormFragment();
        clearMapInteractionVars();
        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.remove(inputForm).commit();
        isInputFormOpen = false;
    }

    public void updateInteractionBox(int step) {
        if (currentMode == TYPING_MODE) {
            if (mapInteractionStep % 2 == 0 && mapInteractionStep != 6) {
                agreeBtn.setVisibility(View.VISIBLE);
                disagreeBtn.setVisibility(View.VISIBLE);
            } else {
                agreeBtn.setVisibility(View.GONE);
                disagreeBtn.setVisibility(View.GONE);
            }
            TextView interactTxv = interactBox.findViewById(R.id.interact_txv);
            switch (step) {
                case 1: {
                    interactTxv.setText("Nhấp lên bản đồ để chọn vị trí báo cáo");
                    break;
                }
                case 2: {
                    interactTxv.setText("Xác nhận vị trí báo cáo?");
                    break;
                }
                case 3:
                    //Exception cases:
                case 5: {
                    interactTxv.setText("Nhấp lên bản đồ để xác định chiều của dòng kẹt");
                    break;
                }
                case 4: {
                    interactTxv.setText("Xác nhận chiều của dòng kẹt?");
                    break;
                }
                case 6: {
                    interactTxv.setText("Không thể xác định hướng, nhấp lên bản đồ để xác định chiều của dòng kẹt");
                    break;
                }
                case 7: {
                    interactTxv.setText("Chọn lại điểm thuộc cùng đường với điểm báo cáo để xác định chiều dòng kẹt");
                    break;
                }
            }
            interactBox.setVisibility(View.GONE);
            btnOption.setVisibility(View.GONE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    interactBox.setVisibility(View.VISIBLE);
                }
            }, 100);
        }
    }

    //Draw and clear markers in interaction mode
    Marker arrowMarker = null;

    private void drawArrowPoly(LatLng originPos, LatLng destPos) {
        double bearing = mapUtils.GetBearing(originPos, destPos);
        Float cameraRotation = mMap.getCameraPosition().bearing;
        //Toast.makeText(MapActivity.this, Float.toString(cameraRotation), Toast.LENGTH_LONG).show();
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_arrow);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 240, 180, false);
        if (arrowMarker != null) arrowMarker.remove();
        arrowMarker = mMap.addMarker(new MarkerOptions()
                .position(originPos)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .anchor(0.9f, 0.5f)
                .alpha(3)
                .rotation((float) bearing + 90 - cameraRotation)
        );
    }

    public void addMarker(SnappedPoint point) {
        if (point == null) {
            Toast.makeText(this, "Vui lòng chọn vị trí trên đường", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng marker = new LatLng(point.location.lat, point.location.lng);
        currentMaker = mMap.addMarker(new MarkerOptions().position(marker)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_point_location)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker));

        progressDialog = ProgressDialog.show(MapActivity.this, "", getString(R.string.loading), true);
        callApi.getNearSegment(point.location.lat, point.location.lng).enqueue(new Callback<BaseResponse<List<NearSegmentResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<NearSegmentResponse>>> call, Response<BaseResponse<List<NearSegmentResponse>>> response) {
                if (response.body() != null && response.body().getData() != null && response.body().getData() != null) {
                    if (response.body().getData().size() > 0)
                        segmentId = String.valueOf(response.body().getData().get(0).getSegmentId());
                    tv_speed.setText(getString(R.string.text_no_report));
                    ratingBtn.setEnabled(false);
                    clReview.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse<List<NearSegmentResponse>>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void onSnappedRoadSuccess(SnappedPoint point) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (point != null) {
            if (mapInteractionStep == 1 || mapInteractionStep == 2) {
                //1: get curPos, 2: confirm curPos
                if (originMarker != null) originMarker.remove();
                originPos = new LatLng(point.location.lat, point.location.lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originPos, 19.0f));
                fullOriginAddress = getAddressByLatLng(this, originPos);
                originMarker = mMap.addMarker(new MarkerOptions().position(originPos).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                originMarker.showInfoWindow();
                mapInteractionStep = 2;
                updateInteractionBox(mapInteractionStep);
            } else if (mapInteractionStep == 3 || mapInteractionStep == 4) {
                //3: get desPos, 4: confirm desPos
                if (destMarker != null) destMarker.remove();
                if (arrowHeadMarker != null) arrowHeadMarker.remove();
                desPos = new LatLng(point.location.lat, point.location.lng);
                destMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(point.location.lat, point.location.lng)).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                if (originMarker != null) {
                    //Toast.makeText(MapActivity.this, Double.toString(mapUtils.getDistBetween2LatLngs(originMarker.getPosition(), destMarker.getPosition())), Toast.LENGTH_LONG).show();
                    //Check if 2 points belongs to same road AND distance between 2 pos long enough to get direction
                    /*String originRoad,destRoad;
                    if(fullOriginAddress.indexOf(",")<fullOriginAddress.length())originRoad = fullOriginAddress.substring(fullOriginAddress.indexOf(" "), fullOriginAddress.indexOf(","));
                    else originRoad = fullOriginAddress.substring(fullOriginAddress.indexOf(" "));
                    if(fullDesAddress.indexOf(",")<fullDesAddress.length())destRoad = fullDesAddress.substring(fullDesAddress.indexOf(" "),fullDesAddress.indexOf(","));
                    else destRoad = fullDesAddress.substring(fullDesAddress.indexOf(" "));*/
                    if (mapUtils.getDistBetween2LatLngs(originMarker.getPosition(), destMarker.getPosition()) >= 5 || currentMode == VOICE_MODE) {
                        drawArrowPoly(originMarker.getPosition(), destMarker.getPosition());
                        mapInteractionStep = 4;
                        if (currentMode == TYPING_MODE) updateInteractionBox(mapInteractionStep);
                        else {
                            //send data
                            if (originMarker != null) {
                                if (inputFormFragment != null) {
                                    interactBox.setVisibility(View.GONE);
                                    btnOption.setVisibility(View.VISIBLE);
                                    inputFormFragment.setCurAndDesPos(originPos, fullOriginAddress, desPos);
                                    if (currentMaker != null) currentMaker.remove();
                                    showInputForm(inputFormFragment);
                                }
                                clearMapInteractionVars();
                            }
                        }
                    } else {
                        destMarker.remove();
                        mapInteractionStep = 3;
                        updateInteractionBox(5);//distance not long enough
                    }
                }
            } else if (mapInteractionStep == 6) {
                if (originMarker != null) originMarker.remove();
                originPos = new LatLng(point.location.lat, point.location.lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originPos, 19.0f));
                fullOriginAddress = getAddressByLatLng(this, originPos);
                originMarker = mMap.addMarker(new MarkerOptions().position(originPos).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                originMarker.showInfoWindow();
                mapInteractionStep = 4;
                RoadFinder roadFinder = new RoadFinder(MapActivity.this);
                roadFinder.getSnappedPoint(originPos, false);
            } else {
                mapInteractionStep = 1;
            }
        }
    }

    void renderCurrentPosition(LatLng position) {
        if (progressDialog != null) progressDialog.dismiss();
        int zoom = (int) mMap.getCameraPosition().zoom;
        if (zoom > 13 && zoom < 22) {
            setDate();
            callApi.getTrafficReport(position.latitude, position.longitude).enqueue(new Callback<BaseResponse<List<TrafficReportResponse>>>() {
                @Override
                public void onResponse(Call<BaseResponse<List<TrafficReportResponse>>> call, Response<BaseResponse<List<TrafficReportResponse>>> response) {
                    hideReportStatus();
                    if (listPositionAround.size() > 0) {
                        listPositionAround.clear();
                    }
                    try {
                        // TODO: sai TrafficReportResponse => get ra list rỗng
                        Log.e("report", "size " + response.body().getData().size());
                        for (TrafficReportResponse item : response.body().getData()) {
                            Point point = new Point(item.getId(),
                                    new LatLng(item.getCenterPoint().getCoordinatesList().get(1),
                                            item.getCenterPoint().getCoordinatesList().get(0)), item.getVelocity());
                            listPositionAround.add(point);
                        }
                        showReportStatus();
                    } catch (Exception e) {}
                }

                @Override
                public void onFailure(Call<BaseResponse<List<TrafficReportResponse>>> call, Throwable t) {
                    Log.d("Leo: ", t.getLocalizedMessage());
                }
            });
        }

    }

    private void removeOldPolyline() {
        if (allPolylineTraffic != null) {
            for (Polyline polyline : allPolylineTraffic) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date_time: {
                datePickerDialog.show();
                break;
            }
            case R.id.btnBack:
                mSearchEdt.setText("");
                clearDirectionMarkers();
                mStartLocationEditext.setText("");
                mDestEdt.setText("");
                if (pathId != null) {
                    toggleNotify(pathId, "false");
                }
            case R.id.ic_direction: {
                if (originMarker != null) {
                    originMarker.remove();
                }
                if (!mSearchEdt.getText().toString().isEmpty()) {
                    mStartLocationEditext.setText(mSearchEdt.getText());
                } else {
                    mStartLocationEditext.setText(getString(R.string.text_current_location));
                }
                if (destinationBox.getVisibility() == View.VISIBLE) {
                    ctlToolbar.setVisibility(View.VISIBLE);
                    customToolbar.setVisibility(View.VISIBLE);
                    destinationBox.setVisibility(View.GONE);
                    travelTypesSpinner.setVisibility(View.GONE);
                    if (directsMaker.size() == 2) {
                        directsMaker.get(0).remove();
                        directsMaker.get(1).remove();
                        directsMaker.clear();
                    }
                    if (polylinePaths != null) {
                        for (Polyline polyline : polylinePaths) {
                            polyline.remove();
                        }
                    }
                } else {
                    ctlToolbar.setVisibility(View.GONE);
                    customToolbar.setVisibility(View.GONE);
                    destinationBox.setVisibility(View.VISIBLE);
                    travelTypesSpinner.setVisibility(View.VISIBLE);

                }
                break;
            }
            case R.id.ic_origin_clear:
                mSearchEdt.setText("");
                break;
            case R.id.ic_clear_end:
                mDestEdt.setText("");
                break;
            case R.id.ic_clear_start:
                mStartLocationEditext.setText("");
                break;
            case R.id.clReview:
            case R.id.ctlToolbar:
            case R.id.interact_box:
            case R.id.ctlDestination: {
                break;
            }
            case R.id.tv_travel_types: {
                travelTypesSpinner.performClick();
                break;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        timePickerDialog.show();
        resultDate = TimeUtil.formatDay(dayOfMonth) + "-" + TimeUtil.formatMonth(month + 1) + "-" + year + " ";
    }

    private void showTimePickerDialog() {
        timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        resultTime = TimeUtil.formatHour(hourOfDay) + ":" + TimeUtil.formatMinute(minute);
                        date = resultDate + resultTime;
                        tvDateTime.setText(date);
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        long dateValue = 0;
                        try {
                            Date parsedDate = df.parse(date);
                            dateValue = parsedDate.getTime();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        progressDialog = ProgressDialog.show(MapActivity.this, "", getString(R.string.loading), true);
                        callApi.getVelocity(dateValue, Integer.parseInt(segmentId)).enqueue(new Callback<BaseResponse<List<TrafficStatusResponse>>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<List<TrafficStatusResponse>>> call, Response<BaseResponse<List<TrafficStatusResponse>>> response) {
                                if (response.body() != null) {
                                    if (response.body().getData() != null && response.body().getData().size() > 0) {
                                        velocity = response.body().getData().get(0).getVelocity();
                                        tv_speed.setText("Vận tốc trung bình: " + response.body().getData().get(0).getVelocity() + " km/h");
                                        ratingBtn.setEnabled(true);
                                    } else {
                                        tv_speed.setText(getString(R.string.text_no_report));
                                        ratingBtn.setEnabled(false);
                                    }
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<List<TrafficStatusResponse>>> call, Throwable t) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
    }

    private void showReportStatus() {
        if (isRatingMode) {
            for (Point item : listPositionAround) {
                LatLng position = item.getPosition();
                markersAround.add(
                        mMap.addMarker(new MarkerOptions().position(position).title(String.valueOf(item.getReportId())).snippet(String.valueOf(item.getVelocity()))
                                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_position_rating))));
            }
        }
    }

    private void hideReportStatus() {
        for (Marker marker : markersAround) {
            marker.remove();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int bgDrawableId) {
        Drawable background = ContextCompat.getDrawable(context, bgDrawableId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, drawableId);
//        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
//        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setupInfoWindow() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                if (markersAround.contains(marker)) {
                    View v = getLayoutInflater().inflate(R.layout.layout_info_window, null);
                    TextView tvInfo = v.findViewById(R.id.tvInfo);
                    tvInfo.setText(marker.getSnippet() + " km/h");
                    return v;
                }
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(MapActivity.this,"On Back Press!!!",Toast.LENGTH_LONG).show();
        //Navigation.findNavController(MapActivity.this, R.id.mapFeature)
        androidx.fragment.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mapFeature);
        try {
            OnBackPressCallback callback = (OnBackPressCallback) fragment.getChildFragmentManager().getFragments().get(0);
            callback.onBackPress();
            return;
        } catch (Exception e) {}

        if (isInputFormOpen) {
            removeInputForm(inputFormFragment);
            btnOption.setVisibility(View.VISIBLE);
        } else if (mapInteractionStep != 0) {
            interactBox.setVisibility(View.GONE);
            clearMapInteractionVars();
            inputFormFragment.uncheckCBoxes();
            showInputForm(inputFormFragment);
            btnOption.setVisibility(View.GONE);
        } else {
            Date currentTime = new Date();
            if (pressTime == null) {
                pressTime = currentTime;
                Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
                return;
            }
            long alpha = TimeUnit.MILLISECONDS.toMillis(currentTime.getTime() - pressTime.getTime());
            if (alpha < 8000) {
                super.onBackPressed();
            } else {
                pressTime = currentTime;
                Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finDirect(LatLng start, LatLng end) {
        if (currentMaker != null) currentMaker.remove();

        String type = "time";
        if (travelTypesSpinner.getSelectedItemPosition() == 0)
            type = "distance";
        if (currentLocation != null && mStartLocationEditext.getText().toString().equals(getString(R.string.text_current_location))) {
            start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        if (mStartLocationEditext.getText().toString().isEmpty()) {
            Toast.makeText(MapActivity.this, "Vui lòng nhập điểm đến", Toast.LENGTH_LONG).show();
            return;
        } else if (start == null && !mStartLocationEditext.getText().toString().equals(getString(R.string.text_current_location))) {
            Toast.makeText(MapActivity.this, "Địa điểm bắt đầu không tồn tại", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog = ProgressDialog.show(this, "", "Đang tìm đường..!", true);
//        10.831717,106.622377,10.828725,106.618397,2)//
        endFindRoad = end;
        callApi.getFindDirect(start.latitude, start.longitude, end.latitude, end.longitude, type)
                .enqueue(new Callback<BaseResponse<List<DirectRespose>>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<List<DirectRespose>>> call, Response<BaseResponse<List<DirectRespose>>> response) {
                        progressDialog.dismiss();
                        Log.e("fdsaf", "code" + response.code());
                        try {
                            List<Coord> directs = response.body().getData().get(0).getCoords();
                            if (directs.size() > 1) {
                                applyPolyLine(directs);
                                pathId = response.body().getData().get(0).getPathId();
                                toggleNotify(pathId, "true");
                            } else {
                                new AlertDialog.Builder(MapActivity.this)
                                        .setTitle("tìm đường thất bại")
                                        .setMessage("Không tìm được đường đi ngắn nhất")
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();
                            }
                        } catch (Exception e) {
                            new AlertDialog.Builder(MapActivity.this)
                                    .setTitle("tìm đường thất bại")
                                    .setMessage("Vị trí tìm đường chưa được hỗ trợ")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                    }


                    @Override
                    public void onFailure(Call<BaseResponse<List<DirectRespose>>> call, Throwable t) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(MapActivity.this)
                                .setTitle("tìm đường thất bại")
                                .setMessage("Không tìm được đường đi ngắn nhất")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });

    }

    private void toggleNotify(String pathId, String isActive) {
        callApi.patchUserNoti(accessToken, SharedPrefUtils.getNotiToken(MapActivity.this), currentLocation.getLatitude(),
                currentLocation.getLongitude(), isActive, pathId).enqueue(new Callback<BaseResponse<PatchNotiResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<PatchNotiResponse>> call, Response<BaseResponse<PatchNotiResponse>> response) {
                if (response.body() != null) {
                    if (response.body().getData() != null) {
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<PatchNotiResponse>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyPolyLine(List<Coord> directs) {
        if (directsMaker.size() == 2) {
            directsMaker.get(0).remove();
            directsMaker.get(1).remove();
            directsMaker.clear();
        }

        directsMaker.add(mMap.addMarker(new MarkerOptions().position(new LatLng(directs.get(0).getLat(), directs.get(0).getLng()))
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_start_position))));
        directsMaker.add(mMap.addMarker(new MarkerOptions().position(new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng()))
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_end_position))));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(directs.get(0).getLat(), directs.get(0).getLng()));
        builder.include(new LatLng(directs.get(directs.size() - 1).getLat(), directs.get(directs.size() - 1).getLng()));
        LatLngBounds bounds = builder.build();
        int padding = 200; // padding around start and end marker
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

        for (int i = 0; i < directs.size() - 1; i++) {
            LatLng start = new LatLng(directs.get(i).getLat(), directs.get(i).getLng());
            LatLng end = new LatLng(directs.get(i + 1).getLat(), directs.get(i + 1).getLng());

            polylinePaths.add(mMap.addPolyline(
                    new PolylineOptions().add(
                            start,
                            end
                    ).width(5).geodesic(true)
                            .clickable(true)
                            .color(Color.BLUE)
            ));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_INFO) {

            // resultCode được set bởi DetailActivity
            // RESULT_OK chỉ ra rằng kết quả này đã thành công
            if (resultCode == Activity.RESULT_OK) {
                String img = SharedPrefUtils.getUser(this).getImgUrl();
                String name = SharedPrefUtils.getUser(this).getUserName();
                if (img != null) {
                    new ImageDownloader(avtImgView).execute(img);
                }
                if (name != null) usnTxtView.setText(name);
            } else {
                // DetailActivity không thành công, không có data trả về.
            }
        }
    }

    @Override
    protected void onDestroy() {
        clearActivity();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationReceiver);
        if (pathId != null) {
            toggleNotify(pathId, "false");
        }
        mMap = null;
        super.onDestroy();
    }

    public void setVisibilitySearchBar(boolean isVisible) {
        if (!isVisible) {
            if (destinationBox.getVisibility() == View.VISIBLE) {
                btnBackToSearch.performClick();
            }
            ctlToolbar.setVisibility(View.GONE);
            customToolbar.setVisibility(View.GONE);
        } else {
            ctlToolbar.setVisibility(View.VISIBLE);
            customToolbar.setVisibility(View.VISIBLE);
        }
    }

    public interface OnMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }

    public interface OnBackPressCallback {
        void onBackPress();
    }
}






