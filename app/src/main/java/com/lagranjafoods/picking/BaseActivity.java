package com.lagranjafoods.picking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lagranjafoods.picking.models.PickingResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by joang on 26/01/2018.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;

    // variable to track event time (to avoid double click on buttons)
    protected long mLastClickTime = 0;

    protected abstract void pressedOnClick(View view);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AppTheme_Dark_Dialog
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
    }

    @Override
    public void onClick(View view) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        pressedOnClick(view);
    }

    protected void showProgressDialog(String message){
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    protected void hideProgressDialog(){
        progressDialog.dismiss();
    }

    protected void showToastWithErrorMessageFromResponse(PickingResponse response){
        showToastWithErrorMessageFromResponse("Error: \n\n", response);
    }

    protected void showToastWithErrorMessageFromResponse(String message, PickingResponse response){
        showToast(message + response.getErrorMessage());
    }

    protected void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    protected Response.ErrorListener volleyErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            String message = String.format("Error crítico:\n\n%s", error.getMessage());
            Log.e(this.getClass().getName(), message);
            showMessage(message);
            hideProgressDialog();
        }
    };

    protected void showMessage(String message){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    protected void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void crossfadeViews(final View viewFrom, final View viewTo) {

        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewTo.setAlpha(0f);
        viewTo.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        viewTo.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        viewFrom.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewFrom.setVisibility(View.GONE);
                    }
                });
    }

    protected String getDateParsedForJsonBody(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(date);
    }
}
