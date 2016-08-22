package ibrahim.radwan.doctorsconnect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ibrahim.radwan.doctorsconnect.core.MainController;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.models.User;
import ibrahim.radwan.doctorsconnect.utils.Utils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private RadioGroup userTypeRadioGroup;
    private RadioButton userTypeDoctor;
    private RadioButton userTypeAdmin;
    private View mProgressView;
    private View mLoginFormView;
    private boolean Signup = false;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if already logged in
        User user = Utils.getUserDataFromSharedPreferences(getApplicationContext());
        if (user != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }
        // Setup Toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_login);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction (TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        userTypeRadioGroup = (RadioGroup) findViewById(R.id.radio_group_user_type);
        userTypeDoctor = (RadioButton) findViewById(R.id.doctor);
        userTypeAdmin = (RadioButton) findViewById(R.id.admin);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin () {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        int userType = (userTypeRadioGroup.getCheckedRadioButtonId() == userTypeDoctor.getId()) ?
                Integer.parseInt(Contract.UserTypeEntry.USER_TYPE_USER_ID) : Integer.parseInt(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utils.isValidEmail(email)) {
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
            mAuthTask = new UserLoginTask(email, password, userType);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid (String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress (final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    public void showRadioGroup (View view) {
        view.setVisibility(View.GONE);
        userTypeRadioGroup.setVisibility(View.VISIBLE);
        Signup = true;
        mEmailSignInButton.setText(getString(R.string.action_register));
        getSupportActionBar().setTitle(R.string.action_register);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, User> {

        private final String mEmail;
        private final String mPassword;
        private final int mUserType;

        UserLoginTask (String email, String password, int userType) {
            mEmail = email;
            mPassword = password;
            mUserType = userType;
        }

        @Override
        protected User doInBackground (Void... params) {
            //Check for existing account
            if (Looper.myLooper() == null) Looper.prepare();
            //Check if email exists
            Cursor emailCursor = MainController.getInstance().checkForEmail(mEmail, getApplicationContext());
            User u = null;
            if (emailCursor != null && emailCursor.moveToFirst() && emailCursor.getCount() > 0) {
                if (emailCursor != null) emailCursor.close();
                u = MainController.getInstance().userLogin(mEmail, mPassword, getApplicationContext());
            } else if (Signup) {
                try {
                    u = MainController.getInstance().AddUser(mEmail, mPassword, String.valueOf(mUserType), getApplicationContext());
                    runOnUiThread(new Runnable() {
                        public void run () {
                            Toast.makeText(getApplicationContext(), R.string.sign_up_completed, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (SQLiteConstraintException e) {
                    Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
            if (emailCursor != null) emailCursor.close();
            return u;
        }

        @Override
        protected void onPostExecute (final User u) {
            mAuthTask = null;
            showProgress(false);

            if (u != null) { // Save to sharedPreferences
                Utils.saveUserDataToSharedPreferences(u, getApplicationContext());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", u);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled () {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

