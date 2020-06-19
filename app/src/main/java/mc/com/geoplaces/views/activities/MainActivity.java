package mc.com.geoplaces.views.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import mc.com.geoplaces.R;
import mc.com.geoplaces.utils.Utils;
import mc.com.geoplaces.views.fragments.TrafficCardFragment;
import mc.com.geoplaces.views.fragments.TrafficDetailsFragment;

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
                        .replace(R.id.fragment_traffic_container_ll, TrafficCardFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_traffic_details_container_ll, TrafficDetailsFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_ll, TrafficCardFragment.newInstance())
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
            if (!(fragment instanceof TrafficCardFragment)) {
                super.onBackPressed();
            } else {
                System.exit(0);
            }
        }
    }
}
