package com.culinars.culinars.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.culinars.culinars.R;
import com.culinars.culinars.adapter.SplashLoginAdapter;
import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.User;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.regex.Pattern;

import static com.culinars.culinars.data.FB.fb;

/**
 * The activity that shows the splash screen.
 * Also prepares the current user before starting the app.
 */
public class SplashActivity extends AppCompatActivity {

    public ImageView logo;
    public FrameLayout loginWrapper;
    public FrameLayout wrapper;
    public ViewPager loginPager;

    FirebaseAuth.AuthStateListener authStateListener;
    Runnable signInAction;

    public LoginButton facebookButton;
    public SignInButton googleButton;
    public AppCompatEditText loginEmail, loginPassword, registerEmail, registerPassword;
    public AppCompatButton loginButton, registerButton, goLoginButton, goRegisterButton, continueButton;
    private GoogleApiClient mGoogleApiClient;


    /**
     * This method runs before contents of layout xml are loaded to the screen.
     * Loading the xml onto the screen should be done here.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //Hide the toolbar if exists.
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        //Load xml to the screen.
        setContentView(R.layout.activity_splash);

        //Initialize various views.
        wrapper = (FrameLayout) findViewById(R.id.wrapper);
        logo = (ImageView) findViewById(R.id.splash_logo);
        logo.setTranslationY(0); //Make sure logo is in the center of the screen.
        loginWrapper = (FrameLayout) findViewById(R.id.login_wrapper);
        loginWrapper.setVisibility(View.INVISIBLE); //Make login options invisible.
        loginWrapper.setTranslationY(dpToPx(250)); //Translate login options out of the screen.

        //Initialize loginPager which contains login, login options and register, in that order.
        loginPager = (ViewPager) findViewById(R.id.login_pager);
        loginPager.setOffscreenPageLimit(3);
        loginPager.setAdapter(new SplashLoginAdapter(getSupportFragmentManager(), this));
        loginPager.setCurrentItem(1);
    }

    /**
     * Checks if given text is a valid password.
     * @param text Text to be checked.
     * @return true if valid.
     */
    public static boolean passwordIsValid(CharSequence text) {
        String passwordRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        return !TextUtils.isEmpty(text)
                && Pattern.compile(passwordRegex).matcher(text).matches();
    }

    /**
     * Checks if given text is a valid email.
     * @param text Text to be checked.
     * @return true if valid.
     */
    public static boolean emailIsValid(CharSequence text) {
        return !TextUtils.isEmpty(text)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    /**
     * Applies shaking animation to the given view.
     * @param view View to be shaked.
     */
    public static void shakeView(View view) {
        ObjectAnimator
                .ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                .setDuration(500)
                .start();
    }

    /**
     * Defines which action the back button will take.
     */
    @Override
    public void onBackPressed() {
        if (loginPager.getCurrentItem() == 1)
            super.onBackPressed();
        else {
            loginPager.setCurrentItem(1, true);
        }
    }

    /**
     * This method runs after views are properly loaded.
     */
    @Override
    protected void onStart() {
        super.onStart();
        //Creates a new user if user doesn't exist in the database. Starts app if otherwise.
         signInAction = new Runnable() {
            @Override
            public void run() {
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User.current().onGet(new FB.GetListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = User.from(dataSnapshot);
                        if (u==null) {
                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail() == null)
                                u = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", "");
                            else
                                u = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            fb().user().child(uid).set(u);
                        } else {
                            if (signInAction != null) {
                                startApp();
                                //Self destruct mechanism.
                                FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
                                signInAction = null;
                            }
                        }
                    }
                });
            }
        };

        //If user isn't signed in, signs in anonymously. If user is signed in, calls signInAction.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Toast.makeText(SplashActivity.this, "Auth state: " + (firebaseAuth.getCurrentUser()==null?"logged out":"logged in as:" + firebaseAuth.getCurrentUser().getUid()), Toast.LENGTH_SHORT).show();
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.signInAnonymously().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    if (signInAction != null)
                        signInAction.run();
                }
            }
        };

        //Activates authStateListener.
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    /**
     * Animates the Culinars logo and login options to their appropriate positions.
     */
    private void animateLogin() {
        logo.animate()
                .translationY((float)(-((wrapper.getHeight()/2)*0.8)))
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setDuration(1000)
                .setStartDelay(500)
                .start();
        loginWrapper.setVisibility(View.VISIBLE);
        loginWrapper.animate()
                .translationY(0)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setDuration(1000)
                .setStartDelay(500)
                .start();
    }

    /**
     * Launches MainActivity.
     */
    private void startApp() {
        Toast.makeText(this, "Starting app.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Converts dp to px.
     * @param dp Amount of dp to be converted.
     * @return Corresponding px amount.
     */
    public float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


}