package com.pickmeupscotty.android.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.pickmeupscotty.android.R;


public class LoginActivity extends FragmentActivity {
    private LoginFragment loginFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginFragment = new LoginFragment();
            int commit = getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fb_login_fragment, loginFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fb_login_fragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
