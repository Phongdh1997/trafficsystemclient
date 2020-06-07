package com.hcmut.admin.bktrafficsystem.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.model.PlaceAutoCompleteAdapter;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;

/**
 * Created by Admin on 9/28/2018.
 */

public class DestinationFragment extends Fragment {

    private AutoCompleteTextView destinationEdt;
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter = null;
    private PlacesClient mGeoDataClient;

    public void setPlaceAutoCompleteAdapter(PlaceAutoCompleteAdapter placeAutoCompleteAdapter) {
        mPlaceAutoCompleteAdapter = placeAutoCompleteAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.destination_fragment, container, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        destinationEdt = view.findViewById(R.id.destination_edt);
        if (mPlaceAutoCompleteAdapter != null) {
            destinationEdt.setAdapter(mPlaceAutoCompleteAdapter);
        }
        destinationEdt.setOnEditorActionListener(
            new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            /*|| actionId == EditorInfo.IME_ACTION_DONE
                            || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER*/) {
                        //Do sth
                        String destination = destinationEdt.getText().toString();
                        MapActivity mActivity = (MapActivity) getActivity();
                        if (!destination.isEmpty()) {
                            Log.d("Yeah", "Mess");

                            //mActivity.sendGuideRequest(destination);

                        } else {
                            Toast.makeText(mActivity, "Please enter destination", Toast.LENGTH_LONG).show();
                        }
                    }
                    return false;
                }
            });
    }

}
