package com.melodicloud.activities;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.ProcessButton;
import com.melodicloud.R;
import com.melodicloud.common.FastLogin;
import com.melodicloud.dto.BaseDto;
import com.melodicloud.services.requests.EmailLoginRequest;
import com.melodicloud.util.LogUtil;
import com.melodicloud.util.PrefrencesUtil;
import com.percolate.caffeine.ActivityUtils;
import com.percolate.caffeine.ToastUtils;
import com.throrinstudio.android.common.libs.validator.validator.EmailValidator;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import info.hoang8f.widget.FButton;


/**
 * A login screen that offers login via email/password.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private EmailLoginRequest loginRequest = null;


    // UI references.
    @ViewById(R.id.email)
    public AutoCompleteTextView mEmailView;
    @ViewById(R.id.password)
    public EditText mPasswordView;
    @ViewById(R.id.facebook_sign_in_button)
    public FButton mFacebookSignInButton;
    @ViewById(R.id.google_sign_in_button)
    public FButton mGPlusSignInButton;
    @ViewById(R.id.login_form)
    public View mLoginFormView;
    @ViewById(R.id.email_sign_in_button)
    public ProcessButton mEmailSignInButton;


    public static final String SOCIAL_NETWORK_TAG = "SocialFragment.SOCIAL_NETWORK_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.melodicloud", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewsLoaded() {

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        if (FastLogin.ENABLED) {
            mEmailView.setText(FastLogin.account.getUsername());
            mPasswordView.setText(FastLogin.account.getPassword());
        }

        if (PrefrencesUtil.prefrences.getUsername() != null && !PrefrencesUtil.prefrences.getUsername().isEmpty()) {
            mEmailView.setText(PrefrencesUtil.prefrences.getUsername());
            mPasswordView.setText(PrefrencesUtil.prefrences.getPassword());
            attemptLogin();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (loginRequest != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            loginRequest = new EmailLoginRequest(email, password);

            loginRequest.execute(new LoginRequestListener());
            ToastUtils.quickToast(this, "Login");
        }
    }

    private boolean isEmailValid(String email) {
        return new EmailValidator(this).isValid(email);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (show) {
            mEmailSignInButton.setProgress(1);
        } else {
            mEmailSignInButton.setProgress(100);
        }

    }


    private class LoginRequestListener implements
            NetworkRequestListener<BaseDto> {

        @Override
        public void onRequestFailure(Exception e) {
            loginRequest = null;
            mEmailSignInButton.setProgress(0);


            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();


        }

        @Override
        public void onRequestSuccess(BaseDto rootServerResponse) {
            loginRequest = null;
            showProgress(false);

            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();
            PrefrencesUtil.prefrences.setUsername(email);
            PrefrencesUtil.prefrences.setPassword(password);

            ActivityUtils.launchActivity(LoginActivity.this, SongListActivity_.class, true);
        }


    }


}



