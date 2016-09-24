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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //Hide the toolbar if exists.
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        //Load xml to the screen.
        setContentView(R.layout.activity_splash);

        wrapper = (FrameLayout) findViewById(R.id.wrapper);
        logo = (ImageView) findViewById(R.id.splash_logo);
        logo.setTranslationY(0);
        loginWrapper = (FrameLayout) findViewById(R.id.login_wrapper);
        loginWrapper.setVisibility(View.INVISIBLE);
        loginWrapper.setTranslationY(dpToPx(250));

        loginPager = (ViewPager) findViewById(R.id.login_pager);
        loginPager.setOffscreenPageLimit(3);
        loginPager.setAdapter(new SplashLoginAdapter(getSupportFragmentManager(), this));
        loginPager.setCurrentItem(1);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

/*        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                //.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

/*
        DataManager.getInstance().init(DataManager.getAuthStateListener(new Runnable() {
            @Override
            public void run() {
                //Is logged in.
                User.create(getCurrentUser().getUid());
                User.current();
            }
        }, new Runnable() {
            @Override
            public void run() {
                //Is logged out.
                //Toast.makeText(activity, "LOGGED OUT", Toast.LENGTH_SHORT).show();
                auth.signInAnonymously()
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isComplete()) {
                                    //Toast.makeText(activity, "ANONYMOUS LOGIN FAILURE", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //Toast.makeText(activity, "ANONYMOUS LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }));

        DataManager.getInstance().login();
        Log.w("SPLASH_ACT", "1");
        DataManager.getInstance().addOnLoginListener(new DataListener() {
            @Override
            public void onDataChange(Object newValue, int event) {
                Log.w("SPLASH_ACT", "2");
                User.current().addListener(new DataListener<User>() {
                    @Override
                    public void onDataChange(User newValue, int event) {
                        Log.w("SPLASH_ACT", "3");
                        if (newValue == null) {
                            Log.w("SPLASH_ACT", "4");
                            startApp();
                            return;
                        }
                        RefMultiple<Recipe> recommendations = newValue.getRecommendations(0, 10);
                        if (recommendations != null)
                            recommendations.addListener(new DataListener<Recipe>() {
                                @Override
                                public void onDataChange(Recipe newValue, int event) {
                                    Log.w("SPLASH_ACT", "5");
                                    startApp();
                                }
                            });
                    }
                });
            }
        });*/
    }

    public static boolean passwordIsValid(CharSequence text) {
        String passwordRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        return !TextUtils.isEmpty(text)
                && Pattern.compile(passwordRegex).matcher(text).matches();
        //return text.length() > 6;
    }

    public static boolean emailIsValid(CharSequence text) {
        return !TextUtils.isEmpty(text)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }


    public static void shakeView(View view) {
        ObjectAnimator
                .ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                .setDuration(500)
                .start();
    }

/*    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        animateLogin();
    }*/

    @Override
    public void onBackPressed() {
        if (loginPager.getCurrentItem() == 1)
            super.onBackPressed();
        else {
            loginPager.setCurrentItem(1, true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                                FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
                                signInAction = null;
                            }
                        }
                    }
                });
            }
        };

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

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
/*
        Log.w("SPLASH", "1");
        final FirebaseAuth.AuthStateListener authStateListener1 = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    animateLogin();
                } else {
                    Log.w("SPLASH", "2, " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        Log.w("SPLASH", "3");
                        User.current().addListener(new DataListener<User>() {
                            @Override
                            public void onDataChange(User newValue, int event) {
                                Log.w("SPLASH", "4" + (newValue == null));
                                if (newValue == null) {
                                    //FirebaseAuth.getInstance().signOut();
                                    User u;
                                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail() == null)
                                        u = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", "");
                                    else
                                        u = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    u.save(new DataListener<User>() {
                                        @Override
                                        public void onDataChange(User newValue, int event) {
                                            User.current().addListener(new DataListener<User>() {
                                                @Override
                                                public void onDataChange(User newValue, int event) {
                                                    Log.w("SPLASH", "5");
                                                    RefMultipleFromKeys<Recipe> recommendations = newValue.getRecommendations(0, 10);
                                                    if (recommendations != null) {
                                                        recommendations.addFinishedListener(new DataListener<Recipe>() {
                                                            @Override
                                                            public void onDataChange(Recipe newValue, int event) {
                                                                Log.w("SPLASH", "6");
                                                                startApp();
                                                            }
                                                        });
                                                    } else {
                                                        Log.w("SPLASH", "7");
                                                        startApp();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Log.w("SPLASH", "5");
                                    RefMultipleFromKeys<Recipe> recommendations = newValue.getRecommendations(0, 10);
                                    if (recommendations != null) {
                                        recommendations.addFinishedListener(new DataListener<Recipe>() {
                                            @Override
                                            public void onDataChange(Recipe newValue, int event) {
                                                Log.w("SPLASH", "6");
                                                startApp();
                                            }
                                        });
                                    } else {
                                        Log.w("SPLASH", "7");
                                        startApp();
                                    }
                                }
                            }
                        });
                    } else {
                        Log.w("LOGIN", "currentUser is null");
                    }
                }
            }
        };*/

/*        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startApp();
                }
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    startApp();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SplashActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            startApp();
        }*/
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
/*
    private void addTask(final Reference ref) {
        tasks.add(ref);
        ref.addListener(new DataListener() {
            @Override
            public void onDataChange(Object newValue, int event) {
                tasks.remove(ref);
                if (tasks.isEmpty()) {

                }
            }
        });
    }*/

    /**
     * Converts dp to px.
     * @param dp Amount of dp to be converted.
     * @return Corresponding px amount.
     */
    public float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


}