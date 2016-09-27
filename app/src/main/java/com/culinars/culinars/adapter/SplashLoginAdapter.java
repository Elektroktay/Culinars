package com.culinars.culinars.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.culinars.culinars.R;
import com.culinars.culinars.activity.SplashActivity;
import com.culinars.culinars.data.structure.User;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;

import static com.culinars.culinars.activity.SplashActivity.shakeView;

/**
 * Manages loginPager in SplashActivity.
 * @see SplashActivity#onCreate
 */
public class SplashLoginAdapter extends FragmentPagerAdapter {
    private SplashActivity activity;
    private AppCompatEditText loginEmail;
    private AppCompatEditText loginPassword;
    private AppCompatButton loginButton;
    private LoginButton facebookButton;
    private SignInButton googleButton;
    private AppCompatButton goLoginButton;
    private AppCompatButton goRegisterButton;
    private AppCompatButton continueButton;
    private AppCompatEditText registerEmail;
    private AppCompatButton registerButton;
    private AppCompatEditText registerPassword;

    public SplashLoginAdapter(FragmentManager fm, SplashActivity splashActivity) {
        super(fm);
        this.activity = splashActivity;
    }

    /**
     * Returns a fragment for the appropriate page.
     * @param position Page number
     * @return Appropriate Fragment, or null if page number is invalid.
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return LoginFragment.newInstance(this);
            case 1: return LoginOptionsFragment.newInstance(this);
            case 2: return RegisterFragment.newInstance(this);
        }
        return null;
    }

    /**
     * Returns how many pages there are in total.
     * @return Number of pages.
     */
    @Override
    public int getCount() {
        return 3;
    }


    /**
     * Manages the login page of the loginPager.
     */
    public static class LoginFragment extends Fragment {
        SplashLoginAdapter adapter;
        public static LoginFragment newInstance(@NonNull SplashLoginAdapter adapter) {
            LoginFragment fragment = new LoginFragment();
            fragment.adapter = adapter;
            return fragment;
        }

        /**
         * Runs before xml is loaded onto the screen. Loading the xml (aka inflating) is done here.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            adapter.loginEmail = (AppCompatEditText) rootView.findViewById(R.id.login_email);
            adapter.loginPassword = (AppCompatEditText) rootView.findViewById(R.id.login_password);
            adapter.loginButton = (AppCompatButton) rootView.findViewById(R.id.login_button);

            //When loginButton is clicked, check the validity of the credentials and try logging in.
            adapter.loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = adapter.loginEmail.getText().toString();
                    String password = adapter.loginPassword.getText().toString();
                    if (!SplashActivity.emailIsValid(email)) {
                        shakeView(adapter.loginEmail);
                        shakeView(adapter.loginPassword);
                        return;
                    }
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    shakeView(adapter.loginEmail);
                                    shakeView(adapter.loginPassword);
                                }
                            });
                }
            });
            return rootView;
        }
    }

    /**
     * Manages the loginOptions page in loginPager.
     */
    public static class LoginOptionsFragment extends Fragment {
        SplashLoginAdapter adapter;
        public static LoginOptionsFragment newInstance(@NonNull SplashLoginAdapter adapter) {
            LoginOptionsFragment fragment = new LoginOptionsFragment();
            fragment.adapter = adapter;
            return fragment;
        }

        //TODO: Implement login with Facebook & Google
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login_options, container, false);
            adapter.facebookButton = (LoginButton) rootView.findViewById(R.id.facebook_button);
            adapter.googleButton = (SignInButton) rootView.findViewById(R.id.google_button);
            adapter.goLoginButton = (AppCompatButton) rootView.findViewById(R.id.go_login_button);
            adapter.goRegisterButton = (AppCompatButton) rootView.findViewById(R.id.go_register_button);
            adapter.continueButton = (AppCompatButton) rootView.findViewById(R.id.continue_button);

            adapter.goLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.activity.loginPager.setCurrentItem(0, true);
                }
            });
            adapter.goRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.activity.loginPager.setCurrentItem(2, true);
                }
            });

            adapter.continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signInAnonymously().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("LOGIN_ANONYMOUS", "onFailure: ", e);
                        }
                    });
                }
            });

            return rootView;
        }
    }

    /**
     * Manages the register page in loginPager.
     */
    public static class RegisterFragment extends Fragment {
        SplashLoginAdapter adapter;
        public static RegisterFragment newInstance(@NonNull SplashLoginAdapter adapter) {
            RegisterFragment fragment = new RegisterFragment();
            fragment.adapter = adapter;
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_register, container, false);
            adapter.registerEmail = (AppCompatEditText) rootView.findViewById(R.id.register_email);
            adapter.registerPassword = (AppCompatEditText) rootView.findViewById(R.id.register_password);
            adapter.registerButton = (AppCompatButton) rootView.findViewById(R.id.register_button);

            //As text changes, check validity and show an error if necessary.
            adapter.registerEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String email = adapter.registerEmail.getText().toString();
                    if (email.length() > 0 && !SplashActivity.emailIsValid(email))
                        adapter.registerEmail.setError("E-mail address is invalid.");
                    else
                        adapter.registerEmail.setError(null);
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            //As text changes, check validity and show an error if necessary.
            adapter.registerPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String password = adapter.registerPassword.getText().toString();
                    if (password.length() > 0 && !SplashActivity.passwordIsValid(password))
                        adapter.registerPassword.setError("Password must be at least 6 characters with a number, lowercase and uppercase character.");
                    else
                        adapter.registerPassword.setError(null);
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            //Check validity one last time and if valid, try registering.
            adapter.registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.registerButton.setEnabled(false);
                    final String email = adapter.registerEmail.getText().toString();
                    final String password = adapter.registerPassword.getText().toString();

                    if (!SplashActivity.emailIsValid(email)) {
                        shakeView(adapter.registerEmail);
                        return;
                    }
                    if (!SplashActivity.passwordIsValid(password)) {
                        shakeView(adapter.registerPassword);
                        return;
                    }

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.w("REGISTER", "REGISTER SUCCESS!!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("SIGN_UP", "Failure", e);
                            if (e.getMessage().equalsIgnoreCase("The email address is already in use by another account.")) {
                                adapter.registerEmail.setError("This email address is already in use by another account.");
                                shakeView(adapter.registerEmail);
                            } else {
                                Toast.makeText(adapter.activity, "Something went wrong while signing up.", Toast.LENGTH_SHORT).show();
                                adapter.registerButton.setEnabled(true);
                            }
                        }
                    });
                }
            });
            return rootView;
        }
    }
}