package com.example.nerk_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openLogin();
    }

    public void openRegister(){openFragment(RegisterFragment.newInstance());}
    public void openLogin(){
        openFragment(LoginFragment.newInstance());
    }
    public void openHome (){
        openFragment(HomeFragment.newInstance());
    }

    private void openFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow();
    }
}