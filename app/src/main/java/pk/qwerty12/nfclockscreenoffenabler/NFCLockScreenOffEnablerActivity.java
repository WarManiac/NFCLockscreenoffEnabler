package pk.qwerty12.nfclockscreenoffenabler;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import pk.qwerty12.nfclockscreenoffenabler.fragments.MainMenuFragment;
import pk.qwerty12.nfclockscreenoffenabler.fragments.NfcTagsFragment;

public class NFCLockScreenOffEnablerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setFragment(MainMenuFragment.newInstance());
    }

    public void setFragment(Fragment frag){
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_main_menu_fragment, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

    private Fragment getFragment(){
        FragmentManager fm = this.getFragmentManager();
        return fm.findFragmentById(R.id.activity_main_menu_fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Main", "Received intent");
        Fragment frag = getFragment();
        if (frag instanceof NfcTagsFragment){
            ((NfcTagsFragment)frag).receiveNfcEvent(intent);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            super.onBackPressed();
        }else{
            fm.popBackStackImmediate();
        }
    }

}
