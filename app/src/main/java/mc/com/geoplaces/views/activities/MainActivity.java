package mc.com.geoplaces.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import mc.com.geoplaces.R;
import mc.com.geoplaces.utils.Utils;
import mc.com.geoplaces.views.fragments.DeliveryCardFragment;
import mc.com.geoplaces.views.fragments.DeliveryDetailsFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_tb);
        toolbar.setTitle(getString(R.string.title_delivery_txt));
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            if (Utils.isTablet(this)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_delivery_container_ll, DeliveryCardFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_delivery_details_container_ll, DeliveryDetailsFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_ll, DeliveryCardFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        }


    }
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_ll);
        if (Utils.isTablet(this))
            System.exit(0);
        else {
            if (!(fragment instanceof DeliveryCardFragment)) {
                super.onBackPressed();
            } else {
                System.exit(0);
            }
        }
    }
}
