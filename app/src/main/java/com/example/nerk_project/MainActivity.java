package com.example.nerk_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            openHome();
        } else {
            openLogin();
        }
    }

    public void openRegister(){openFragment(RegisterFragment.newInstance());}
    public void openLogin(){
        openFragment(LoginFragment.newInstance());
    }
    public void openPairCode(){
        openFragment(PairCodeFragment.newInstance());
    }

    public void openLocation(){
        openFragment(LocationFragment.newInstance());
    }

    public void openChat(){
        openFragment(ChatFragment.newInstance());
    }


    public void openHome (){
        openFragment(HomeFragment.newInstance());
    }

    public void openPostFeed(){
        openFragment(PostFeedFragment.newInstance());
    }

    public void openOption(){
        openFragment(OptionFragment.newInstance());
    }

    public void openMemory(){
        openFragment(MemoryFragment.newInstance());
    }

    public void openAnniversary(){
        openFragment(AnniversaryFragment.newInstance());
    }
    public  void openSetting(){openFragment(SettingFragment.newInstance());}

    private void openFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow();
    }
}