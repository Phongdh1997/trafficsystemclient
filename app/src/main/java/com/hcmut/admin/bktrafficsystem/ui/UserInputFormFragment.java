package com.hcmut.admin.bktrafficsystem.ui;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.api.CallApi;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.model.UserInput;
import com.hcmut.admin.bktrafficsystem.model.param.ReportRequest;
import com.hcmut.admin.bktrafficsystem.model.response.BaseResponse;
import com.hcmut.admin.bktrafficsystem.model.response.ReportResponse;
import com.hcmut.admin.bktrafficsystem.util.Async_ReSizeImageFromBitmap;
import com.hcmut.admin.bktrafficsystem.util.Async_ResizeImageFromUri;
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.bktrafficsystem.util.EditTextCustom;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.hcmut.admin.bktrafficsystem.util.ImageUtils.getGalleryIntents;
import static com.hcmut.admin.bktrafficsystem.util.ImageUtils.getRealPathFromURI;
import static com.hcmut.admin.bktrafficsystem.util.LocationUtil.getAddressByLatLng;

/**
 * Created by Admin on 10/7/2018.
 */

public class UserInputFormFragment extends Fragment {

    private static final int IMAGE_PERMISSION_CODE = 888;
    private static final int PAGE_SIZE = 20;
    private static final int IMAGE_REQUEST = 2;

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";

    private static int GET_GPS_DELAY = 3000;
    private static int GET_INPUT_DELAY = 700;
    private static int MAX_IMAGE = 4;
    private UserInput userInput;
    private SpeechRecognizer mSpeechRecognizer;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Intent mSpeechRecognizerIntent;
    private TextToSpeech mTTS;
    private EditTextCustom edtSpeed, edtDesc, edtAddress, edtReason;
    private Spinner spnReason;
    private ImageView ivMode;
    private Button sendBtn;
    private CheckBox curPosCBox;
    private CheckBox otherPosCBox;
    private LatLng originPos, desPos;
    private long currentTimeMillis;
    private ProgressDialog progressDialog;
    int currentStep = 1;
    private static int TYPING_MODE = 0;
    private static int VOICE_MODE = 1;
    private int currentMode = TYPING_MODE;
    private boolean isSpeedByGPS = false; //if true -> get speed by GPS (not by user)
    private boolean haveDataOtherLocation = false;
    private boolean isAddDirection = false;
    private MapActivity mActivity;

    private ImageView ivClose;
    //Image vars
    private static Uri capturedImageUri = null;
    private static final int NORMAL_CAPTURE_REQUEST = 1;
    private static final int EDIT_CAPTURE_REQUEST = 2;
    private int curImgNum = 1;
    private int totalImgCount = 1;
    private ImageView imgView;
    private ImageView imgView1, imgView2, imgView3, imgView4;
    private ImageView delImgOne, delImgTwo, delImgThree, delImgFour;
    private View view1, view2, view3, view4;
    private String accessToken;
    private ArrayList<String> images = new ArrayList<>();

    private float currentVelocity = -1;
    AndroidExt androidExt = new AndroidExt();
    private Context mContext;
    File file;
    private int requestCode;
    private boolean isInitView = true;
    private int imagePositionClick = 0;
//    private int sumImage = 0;

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        accessToken = getArguments().getString("accessToken");
        View rootView = inflater.inflate(R.layout.user_input_form_new, container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = (MapActivity) getActivity();

        currentTimeMillis = System.currentTimeMillis();

        userInput = new UserInput();
        userInput.setCurPos(null);
        userInput.setDesPos(null);
        userInput.setVelocity(-1);
        userInput.setAfter(-1);
        userInput.setBefore(-1);

        initView();
        bindEvent();

        initTextToSpeech();

        //GET SPEED BY GPS
        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

//        //get origin position
//        getDevicePosition(true);
//
        curPosCBox.setChecked(true);
        initWithCurrent();
    }

    private void initWithCurrent() {
        mActivity.updateCurDesLocation();
    }

    public void applyView(LatLng curPos, String curPosAddress, LatLng desPos, int currentVelocity) {
        if (curPos != null && desPos != null && !curPosAddress.isEmpty()) {
            androidExt.showAutoDetectDialog(UserInputFormFragment.this.getContext(), "Vận tốc được ghi nhận: " + currentVelocity + "km/h", new ClickDialogListener.OK() {
                @Override
                public void onCLickOK() {
                }
            });
            setCurAndDesPos(curPos, curPosAddress, desPos);
            userInput.setVelocity(currentVelocity);
            this.currentVelocity = currentVelocity;
            edtSpeed.setText(String.valueOf(currentVelocity));
        } else {
            edtSpeed.requestFocus();
            mActivity.updateCurDesLocationFromSensor();
//            androidExt.showDialog(UserInputFormFragment.this.getContext(),
//                    "Không xác định được vận tốc hiện tại và hướng di chuyển\n Vui lòng chọn hướng và nhập vận tốc",
//                    new ClickDialogListener.OK() {
//                        @Override
//                        public void onCLickOK() {
//                            reportCurrentLocation();
//                        }
//                    });
        }
    }

    private void initView() {
        edtAddress = getView().findViewById(R.id.edtAddress);
        edtSpeed = getView().findViewById(R.id.edtSpeed);
        edtDesc = getView().findViewById(R.id.edtDesc);
        edtReason = getView().findViewById(R.id.edtReason);

        spnReason = getView().findViewById(R.id.spnReason);

        sendBtn = getView().findViewById(R.id.btnSend);

        ivMode = getView().findViewById(R.id.ivMode);
        ivClose = getView().findViewById(R.id.ivClose);

        curPosCBox = getView().findViewById(R.id.cbCurrent);
        otherPosCBox = getView().findViewById(R.id.cbOther);

        imgView1 = getView().findViewById(R.id.imageView1);
        imgView2 = getView().findViewById(R.id.imageView2);
        imgView3 = getView().findViewById(R.id.imageView3);
        imgView4 = getView().findViewById(R.id.imageView4);


        delImgOne = getView().findViewById(R.id.delImg1);
        delImgTwo = getView().findViewById(R.id.delImg2);
        delImgThree = getView().findViewById(R.id.delImg3);
        delImgFour = getView().findViewById(R.id.delImg4);

        view1 = getView().findViewById(R.id.view1);
        view2 = getView().findViewById(R.id.view2);
        view3 = getView().findViewById(R.id.view3);
        view4 = getView().findViewById(R.id.view4);
    }

    private void initTextToSpeech() {
        mTTS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale loc = new Locale("vi", "VN");
                    int result = mTTS.setLanguage(loc);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported!");
                    } else {
                        Log.e("TTS", "Language supported!");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onDone(String utteranceId) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {

            }

            @Override
            public void onStart(String utteranceId) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getActivity(),"START!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mTTS.setSpeechRate(1.5f);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                //askForInput();
            }

            @Override
            public void onError(int error) {
                String message = "";

                if (error == SpeechRecognizer.ERROR_AUDIO) message = "audio";
                else if (error == SpeechRecognizer.ERROR_CLIENT) message = "client";
                else if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
                    message = "insufficient permissions";
                else if (error == SpeechRecognizer.ERROR_NETWORK) message = "network";
                else if (error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT)
                    message = "network timeout";
                else if (error == SpeechRecognizer.ERROR_NO_MATCH) message = "no match found";
                else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
                    message = "recognizer busy";
                else if (error == SpeechRecognizer.ERROR_SERVER) message = "server";
                else if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) message = "speech timeout";

                askForInput();
                //Note: Enter error => not enter end of speech

                Log.e("TTS", message);
            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null) {
                    String valueInput = matches.get(0);
                    //Check if confirmation question or not
                    //Case 2: ask user if velocity is correct or not (automatic get)
                    switch (currentStep) {
                        //1:check position
                        //String speech ="Bạn đang báo cáo tình trạng giao thông ở vị trí hiện tại đúng không?";
                        case 1: {
                            Log.d("Case 1:", valueInput);
                            switch (valueInput) {
                                case "Đúng":
                                case "đúng":
                                    curPosCBox.setChecked(true);
                                    currentStep = 3;
                                    askForInput();
                                    break;
                                case "Không":
                                case "không":
                                    curPosCBox.setChecked(false);
                                    currentStep = 2;
                                    askForInput();
                                    break;
                                default:
                                    //Handle if answer wrong format!
                                    askForInput();
                                    break;
                            }
                            break;
                        }
                        //2:get position
                        //String speech="Địa chỉ mà bạn muốn báo cáo là ở đâu?";
                        case 2: {
                            //send address to api to check ??

                            //if valid
                            currentStep = 3;
                            askForInput();
                            break;
                        }
                        //4:get speed
                        //String speech ="Vận tốc ước lượng của dòng kẹt là bao nhiêu?";
                        case 3: {
                            if (isNumeric(valueInput)) {
                                isSpeedByGPS = false;
                                Double value = Double.parseDouble(valueInput);
                                //round up value
                                int roundValue = (int) Math.round(value);
                                edtSpeed.setText(String.valueOf(roundValue));
                                userInput.setVelocity(roundValue);
                                currentStep = 4;
                                askForInput();
                            } else {
                                //Handle if answer wrong format!
                                askForInput();
                            }
                            break;
                        }
                        //5:get before
                        //String speech ="Độ dài ước lượng của dòng kẹt đằng trước là bao nhiêu";
                        case 4: {
                            if (isNumeric(valueInput)) {
                                Double value = Double.parseDouble(valueInput);
                                //round up value
                                int roundValue = (int) Math.round(value);
                                userInput.setBefore(roundValue);
                                currentStep = 5;
                                askForInput();
                            } else {
                                //Handle if answer wrong format!
                                askForInput();
                            }
                            break;
                        }
                        //6:get after
                        //String speech = "Độ dài ước lượng của dòng kẹt đằng sau là bao nhiêu";
                        case 5: {
                            if (isNumeric(valueInput)) {
                                Double value = Double.parseDouble(valueInput);
                                //round up value
                                int roundValue = (int) Math.round(value);
                                userInput.setAfter(roundValue);
                                currentStep = 6;
                                askForInput();
                            } else {
                                //Handle if answer wrong format!
                                askForInput();
                            }
                            break;
                        }
                        //7: get description
                        //String speech = "Mô tả thêm là gì";
                        case 6: {
                            edtDesc.setText(valueInput);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
    }

    private void bindEvent() {
        edtReason.findViewById(R.id.edtMess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnReason.setVisibility(View.VISIBLE);
                spnReason.performClick();
            }
        });
        edtReason.findViewById(R.id.ic_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnReason.performClick();
            }
        });
        spnReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    edtReason.setText("");
                    edtReason.setHint(spnReason.getSelectedItem().toString());
                } else
                    edtReason.setText(spnReason.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        curPosCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isInitView) {
                    isInitView = false;
                    return;
                }
                if (!curPosCBox.isChecked()) {
                    return;
                }
                androidExt.showConfirmDialog(UserInputFormFragment.this.getContext(), "vị trí hiện tại?",
                        new ClickDialogListener.Yes() {
                            @Override
                            public void onCLickYes() {
                                haveDataOtherLocation = false;
                                if ((curPosCBox.isChecked() && otherPosCBox.isChecked())
                                        || (!curPosCBox.isChecked() && otherPosCBox.isChecked())) {
                                    if (curPosCBox.isChecked()) {
                                        otherPosCBox.setChecked(false);
                                        initWithCurrent();
                                    }
                                } else {
                                    curPosCBox.setChecked(true);
                                }
                            }
                        },
                        new ClickDialogListener.No() {
                            @Override
                            public void onClickNo() {
                                curPosCBox.setChecked(false);
                            }
                        });
            }
        });

        otherPosCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!otherPosCBox.isChecked()) {
                    return;
                }
                androidExt.showConfirmDialog(UserInputFormFragment.this.getContext(), "vị trí khác?",
                        new ClickDialogListener.Yes() {
                            @Override
                            public void onCLickYes() {
                                if (!edtSpeed.getText().isEmpty() && isNumeric(edtSpeed.getText()))
                                    currentVelocity = Integer.parseInt(edtSpeed.getText());
                                if ((!curPosCBox.isChecked() && otherPosCBox.isChecked())
                                        || (curPosCBox.isChecked() && otherPosCBox.isChecked())
                                        || (curPosCBox.isChecked() && !otherPosCBox.isChecked())) {
                                    reportOtherLocation();
                                    edtSpeed.setText("");
                                } else {
                                    otherPosCBox.setChecked(true);
                                }
                            }
                        },
                        new ClickDialogListener.No() {
                            @Override
                            public void onClickNo() {
                                otherPosCBox.setChecked(false);
                            }
                        });
            }
        });

        edtAddress.findViewById(R.id.edtMess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: get current location -> chuyen qua text -> hien thi
                //TODO: current thi ko cho chon location
                if (curPosCBox.isChecked()) reportCurrentLocation();
                if (otherPosCBox.isChecked()) {
                    reportOtherLocation();
                    haveDataOtherLocation = true;
                }
            }
        });

        ivMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Typing -> Voice
                if (currentMode == TYPING_MODE) {
                    currentMode = VOICE_MODE;
                    curPosCBox.setChecked(true);
                    ivMode.setImageResource(R.drawable.ic_microphone);
                    currentStep = 1;
                    askForInput();
                }
                //Voice -> Typing
                else {
                    currentMode = TYPING_MODE;
                    if (mTTS.isSpeaking()) mTTS.stop();
                    ivMode.setImageResource(R.drawable.ic_keyboard);
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
//                mActivity.removeInputForm(UserInputFormFragment.this);
            }
        });

        imgView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePositionClick = 1;
                handleGetImage();
            }
        });

        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePositionClick = 2;
                handleGetImage();
            }
        });

        imgView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePositionClick = 3;
                handleGetImage();
            }
        });

        imgView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePositionClick = 4;
                handleGetImage();
            }
        });

        delImgOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                images.remove(0);
                if (images.size() == 3) {
                    Glide.with(getContext()).load(images.get(0)).into(imgView1);
                    Glide.with(getContext()).load(images.get(1)).into(imgView2);
                    Glide.with(getContext()).load(images.get(2)).into(imgView3);
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView4);
                    delImgFour.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                }
                if (images.size() == 2) {
                    Glide.with(getContext()).load(images.get(0)).into(imgView1);
                    Glide.with(getContext()).load(images.get(1)).into(imgView2);
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView3);
                    delImgThree.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    imgView4.setVisibility(View.GONE);
                }
                if (images.size() == 1) {
                    Glide.with(getContext()).load(images.get(0)).into(imgView1);
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView2);
                    delImgTwo.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    imgView3.setVisibility(View.GONE);
                }
                if (images.size() == 0) {
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView1);
                    delImgOne.setVisibility(View.GONE);
                    view1.setVisibility(View.GONE);
                    imgView2.setVisibility(View.GONE);
                }
            }
        });

        delImgTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                images.remove(1);
                if (images.size() == 3) {
                    Glide.with(getContext()).load(images.get(1)).into(imgView2);
                    Glide.with(getContext()).load(images.get(2)).into(imgView3);
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView4);
                    delImgFour.setVisibility(View.GONE);
                }
                if (images.size() == 2) {
                    Glide.with(getContext()).load(images.get(1)).into(imgView2);
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView3);
                    delImgThree.setVisibility(View.GONE);
                    imgView4.setVisibility(View.GONE);
                }
                if (images.size() == 1) {
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView2);
                    delImgTwo.setVisibility(View.GONE);
                    imgView3.setVisibility(View.GONE);
                }
            }
        });

        delImgThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                images.remove(2);
                if (images.size() == 3) {
                    Glide.with(getContext()).load(images.get(2)).into(imgView3);
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView4);
                    delImgFour.setVisibility(View.GONE);
                }
                if (images.size() == 2) {
                    Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView3);
                    delImgThree.setVisibility(View.GONE);
                    imgView4.setVisibility(View.GONE);
                }
            }
        });

        delImgFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                images.remove(3);
                Glide.with(getContext()).load(R.drawable.ic_add_img).into(imgView4);
                delImgFour.setVisibility(View.GONE);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilledInput() && isValidInput(edtSpeed)) {
                    //Prepare data
                    LatLng curPos = userInput.getCurPos();
                    LatLng desPos = userInput.getDesPos();
                    int velocity = Integer.parseInt(edtSpeed.getText());
                    int causeId = spnReason.getSelectedItemPosition();
                    String description = edtDesc.getText();
                    //Post

                    if (description.isEmpty())
                        description = null;

                    if (curPosCBox.isChecked()) {
                        if (curPos == null || desPos == null) {
                            androidExt.showGetDirection(UserInputFormFragment.this.getContext(), "Vui lòng chọn hướng", new ClickDialogListener.Yes() {
                                @Override
                                public void onCLickYes() {
                                    showLoading();
                                    isAddDirection = true;
                                    getDevicePosition(false);
                                }
                            });
                        } else {
                            showLoading();
                            //Vị trí hiện tại, cần gửi: causeId, velocity, curPos, desPos, before, after, description, images
                            callApiPostTrafficMove(causeId, velocity, curPos, desPos, description, images);
                        }
                    } else if (otherPosCBox.isChecked()) {
                        showLoading();
                        if (curPos == null || desPos == null) {
                            reportOtherLocation();
                        } else {
                            //Vị trí khác, cần gửi: velocity, curPos, desPos, description, images
                            callApiPostTrafficRemote(causeId, velocity, curPos, desPos, description, images);
                        }
                    }
                }
            }
        });
    }

    private void reportCurrentLocation() {
        if (curPosCBox.isChecked()) {
            mActivity.setVisibilitySearchBar(false);
            otherPosCBox.setChecked(false);
            if (currentMode == VOICE_MODE) curPosCBox.setEnabled(false);
            long remainDelay = GET_GPS_DELAY - (System.currentTimeMillis() - currentTimeMillis);
            if (progressDialog != null) hideLoading();
            if (remainDelay > 0) {
                //PROGRESS DIALOG SHOW
                showLoading();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 1000ms (1 second)
                        getDevicePosition(false);
                    }
                }, remainDelay);

            } else {
                //RENDER
                getDevicePosition(false);
            }
        }
    }

    private void reportOtherLocation() {
        if (otherPosCBox.isChecked()) {
            mActivity.setVisibilitySearchBar(false);
            curPosCBox.setChecked(false);
            mActivity.startMapInteraction(UserInputFormFragment.this, false);
        }
    }

    private void callApiPostTrafficRemote(final int causeId, final int velocity, final LatLng curPos, final LatLng desPos, final String description, final ArrayList<String> images) {
        ArrayList<String> causes = new ArrayList<>();
        if (causeId == 0) {
            causes = null;
        } else
            causes.add(String.valueOf(causeId));

        ReportRequest reportRequest = new ReportRequest(velocity, curPos.latitude, curPos.longitude, desPos.latitude, desPos.longitude, causes, description, images);
        CallApi.createService().postTrafficReport(accessToken, reportRequest)
                .enqueue(new Callback<BaseResponse<ReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                        hideLoading();
                        Log.d("Response: ", response.toString());
                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                androidExt.showSuccessDialog(UserInputFormFragment.this.getContext(), "Gửi thành công!", new ClickDialogListener.OK() {
                                    @Override
                                    public void onCLickOK() {
                                    }
                                });
                                getActivity().onBackPressed();
                            } else {
                                androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Đường chưa được hỗ trợ!");
                                getActivity().onBackPressed();
                            }
                        } else {
                            androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Lỗi hệ thống. Vui lòng thử lại sau!");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                        hideLoading();
                        androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Gửi thất bại!");
                    }
                });
    }

    private void callApiPostTrafficMove(final int causeId, final int velocity, final LatLng curPos, final LatLng desPos, final String description, final ArrayList<String> images) {
        ArrayList<String> causes = new ArrayList<>();
        if (causeId == 0)
            causes = null;
        else
            causes.add(String.valueOf(causeId));
        ReportRequest reportRequest = new ReportRequest(velocity, curPos.latitude, curPos.longitude, desPos.latitude, desPos.longitude, causes, description, images);
        CallApi.createService().postTrafficReport(accessToken, reportRequest)
                .enqueue(new Callback<BaseResponse<ReportResponse>>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse<ReportResponse>> call, @NotNull Response<BaseResponse<ReportResponse>> response) {
                        hideLoading();
                        Log.d("Response: ", response.toString());
                        if (response.body() != null) {
                            if (response.body().getCode() == 200) {
                                androidExt.showSuccessDialog(UserInputFormFragment.this.getContext(), "Gửi thành công!", new ClickDialogListener.OK() {
                                    @Override
                                    public void onCLickOK() {
                                    }
                                });
                                getActivity().onBackPressed();
                            } else {
                                androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Đường chưa được hỗ trợ!");
                                getActivity().onBackPressed();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                        hideLoading();
                        androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Gửi thất bại!");

                    }
                });
    }

//    private MultipartBody.Part[] prepareImageToUpload() {
//        final MultipartBody.Part[] parts = new MultipartBody.Part[totalImgCount - 1];
//        for (int i = 1; i <= totalImgCount - 1; i++) {
//            File file = new File(Environment.getExternalStorageDirectory()
//                    + "/Traffic/", (i + ".jpg"));
//            try {
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, new FileOutputStream(file));
//            } catch (Throwable t) {
//                Log.e("ERROR", "Error compressing file." + t.toString());
//                t.printStackTrace();
//            }
//            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
//            parts[i - 1] = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
//        }
//        return parts;
//    }


    private boolean isFilledInput() {
        if (edtAddress.getText().isEmpty()) {
            androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Vui lòng chọn vị trí báo cáo!");
            return false;
        }

        if (edtSpeed.getText().isEmpty()) {
            androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Vui lòng điền vận tốc!");
            return false;
        }

        return true;
    }

    private boolean isValidInput(EditTextCustom editTextCustom) {
        if (!isNumeric(editTextCustom.getText())) {
            //Wrong format or empty
            androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Vui lòng nhập số!");
            editTextCustom.requestFocus();
            return false;
        } else {
            int value = Integer.parseInt(editTextCustom.getText());
            if (value < 0) {
                androidExt.showErrorDialog(UserInputFormFragment.this.getContext(), "Vui lòng nhập số lớn hơn 0!");
                editTextCustom.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void setCurAndDesPos(LatLng curPos, String curPosAddress, LatLng desPos) {
        if (userInput == null) {
            userInput = new UserInput();
        }
        userInput.setCurPos(curPos);
        edtAddress.setText(curPosAddress);
        userInput.setDesPos(desPos);
        if (!isAddDirection) {
            edtSpeed.setText("");
        } else {
            isAddDirection = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //TextToSpeech
    private void askForInput() {
        //mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        switch (currentStep) {
            //1:check position
            case 1: {
                String speech = "Bạn báo cáo ở vị trí hiện tại đúng không";
                speak(speech);
                break;
            }
            //2:get position
            case 2: {
                String speech = "Địa chỉ bạn muốn báo cáo là gì?";
                speak(speech);
                break;
            }
            //3:get speed
            case 3: {
                String speech = "Vận tốc hiện tại là bao nhiêu km/h?";
                speak(speech);
                edtSpeed.setText("...");
                break;
            }
            //4:get before
            case 4: {
                String speech = "Độ dài của dòng kẹt phía trước là bao nhiêu";
                speak(speech);
                break;
            }
            //5:get afterspeak(speech);
            case 5: {
                String speech = "Độ dài của dòng kẹt phía sau là bao nhiêu";
                speak(speech);
                break;
            }
            case 6: {
                String speech = "Mô tả thêm là gì";
                speak(speech);
                edtDesc.setText("...");
                break;
            }
        }
    }

    private void speak(String text) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    private void handleGetImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PERMISSION_CODE);
            } else {
                getPhotoIntent();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                getPhotoIntent();
            }
        }
    }

    private void getPhotoIntent() {
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getContext().getPackageManager();

        // collect all camera intents if Camera permission is available
        allIntents.add(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

        List<Intent> galleryIntents =
                getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK);
        }
        allIntents.addAll(galleryIntents);

        Intent target;
        if (allIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = allIntents.get(allIntents.size() - 1);
            allIntents.remove(allIntents.size() - 1);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, getString(R.string.title_take_photo));

        // Add all other intents
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        startActivityForResult(chooserIntent, IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            handleImageResult(data);
        }
    }


    private void handleImageResult(Intent data) {
        if (data.getData() == null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Async_ReSizeImageFromBitmap async = (Async_ReSizeImageFromBitmap) new Async_ReSizeImageFromBitmap(getContext(), photo, new Async_ReSizeImageFromBitmap.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    Uri resultUri = Uri.parse(output);
                    // request permissions and handle the result in onRequestPermissionsResult()
                    if (resultUri != null && resultUri.getPath() != null) {
                        file = new File(getRealPathFromURI(getContext(), resultUri));
                        try {
                            uploadFile(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("TAG_GLIDE", e.getMessage());
                        }
                    }
                }
            }).execute();
        } else {
            Uri resultUri = data.getData();
            Async_ResizeImageFromUri async = (Async_ResizeImageFromUri) new Async_ResizeImageFromUri(getContext(), resultUri, new Async_ResizeImageFromUri.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    Uri resultUri12 = Uri.parse(output);
                    // request permissions and handle the result in onRequestPermissionsResult()
                    if (resultUri12 != null && resultUri12.getPath() != null) {
                        file = new File(getRealPathFromURI(getContext(), resultUri12));
                        try {
                            uploadFile(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("TAG_GLIDE", e.getMessage());
                        }
                    }
                }
            }).execute();
        }
    }


    private void uploadFile(File file) {
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

        new CallApi().createService().uploadFile(parts)
                .enqueue(new Callback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<String>> call, final Response<BaseResponse<String>> response) {
                        if (response.body() != null) {
                            ImageView imageView;
                            if (images.size() < imagePositionClick) {
                                images.add(response.body().getData());
                                if (imagePositionClick == 1) {
                                    imageView = imgView1;
                                    imgView2.setVisibility(View.VISIBLE);
//                                    sumImage = 1;
                                    delImgOne.setVisibility(View.VISIBLE);
                                    view1.setVisibility(View.VISIBLE);
                                } else if (imagePositionClick == 2) {
                                    imageView = imgView2;
                                    imgView3.setVisibility(View.VISIBLE);
                                    delImgTwo.setVisibility(View.VISIBLE);
//                                    sumImage = 2;
                                    view2.setVisibility(View.VISIBLE);
                                } else if (imagePositionClick == 3) {
                                    imageView = imgView3;
                                    imgView4.setVisibility(View.VISIBLE);
                                    delImgThree.setVisibility(View.VISIBLE);
//                                    sumImage = 3;
                                    view3.setVisibility(View.VISIBLE);
                                } else {
                                    imageView = imgView4;
                                    delImgFour.setVisibility(View.VISIBLE);
//                                    sumImage = 4;
                                    view4.setVisibility(View.VISIBLE);
                                }
                            } else {
                                images.set(imagePositionClick - 1, response.body().getData());
                                if (imagePositionClick == 1) {
                                    imageView = imgView1;
                                } else if (imagePositionClick == 2) {
                                    imageView = imgView2;
                                } else if (imagePositionClick == 3) {
                                    imageView = imgView3;
                                } else {
                                    imageView = imgView4;
                                }
                            }
                            Glide.with(getContext()).load(response.body().getData()).into(imageView);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<String>> call, Throwable t) {

                    }
                });

    }

    private void getDevicePosition(final boolean isOriginPos) {
        if (mContext == null) return;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        //get location -> latlng
                        Location currentLocation = (Location) task.getResult();
                        if (currentLocation == null) {
                            androidExt.showSuccessDialog(getContext(), "Vui lòng bật định vị vị trí", new ClickDialogListener.OK() {
                                @Override
                                public void onCLickOK() {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            });
                            return;
                        }
                        LatLng locationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        if (isOriginPos) {
                            if (progressDialog != null) hideLoading();
                            originPos = locationLatLng;
                            currentTimeMillis = System.currentTimeMillis();
                        } else {
                            if (progressDialog != null) hideLoading();
                            //----Confirm direction----
                            //Log.d("orginPos",Double.toString(originPos.latitude));
                            //Log.d("desPos",Double.toString(desPos.latitude));

                            if (locationLatLng != null) {
                                mActivity.confirmDirection(UserInputFormFragment.this, locationLatLng, desPos, currentMode);
                                edtAddress.setText(getAddressByLatLng(mContext, locationLatLng));
                                originPos = null;
                            }
                        }
                    }
                }
            });
        } catch (SecurityException ignored) {
        }
    }

    private void showLoading() {
        progressDialog = ProgressDialog.show(getContext(), "", getString(R.string.loading), true);
    }

    private void hideLoading() {
        progressDialog.dismiss();
    }

    public void uncheckCBoxes() {
        //reset
        currentTimeMillis = System.currentTimeMillis();
//        getDevicePosition(true);
        if (!haveDataOtherLocation && otherPosCBox.isChecked()) {
            edtAddress.setText("");
//            curPosCBox.setChecked(true);
//            otherPosCBox.setChecked(false);
        }
    }
}