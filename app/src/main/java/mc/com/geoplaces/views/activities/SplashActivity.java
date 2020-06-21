package mc.com.geoplaces.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.security.auth.callback.PasswordCallback;

import mc.com.geoplaces.R;

public class SplashActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Handler handler = new Handler();
    private static final int DELAY = 3000;
    /****************************************************/
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION = 100;
    private static Context context;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationManager mLocationMgr;
    private static Location mlocation = null;
    /***************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*******************************/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API).build();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationRequest)
            {
                Log.d("TrafficDetailsFragment", "onLocationResult");
                if (locationRequest == null)
                    return;
                mlocation = locationRequest.getLastLocation();
            }
        };

        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        SplashActivity.context = getApplicationContext();
        /******************************/
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }, DELAY);
    }
/***************************************************************/
    public static Context getAppContext() {
        return SplashActivity.context;
    }

    public static float getDistanceFromCurrentLocation(LatLng inputLatLng)
    {
        if (mlocation == null) return 10;
        float result[] = new float[1];
        Location.distanceBetween(mlocation.getLatitude(), mlocation.getLongitude(),
                inputLatLng.latitude, inputLatLng.longitude, result);
        return result[0];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 檢查收到的權限要求編號是否和我們送出的相同
        if (requestCode == REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION) {
            if (grantResults.length != 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 再檢查一次，就會進入同意的狀態，並且順利啟動。
                enableLocation(true);
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void enableLocation(boolean on) {
        if (ContextCompat.checkSelfPermission(SplashActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // 這項功能尚未取得使用者的同意
            // 開始執行徵詢使用者的流程
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    SplashActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder altDlgBuilder =
                        new AlertDialog.Builder(SplashActivity.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("App需要啟動定位功能。");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                // 顯示詢問使用者是否同意功能權限的對話盒
                                // 使用者答覆後會執行onRequestPermissionsResult()
                                ActivityCompat.requestPermissions(SplashActivity.this,
                                        new String[]{
                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                            }
                        });
                altDlgBuilder.show();

                return;
            } else {
                // 顯示詢問使用者是否同意功能權限的對話盒
                // 使用者答覆後會執行callback方法onRequestPermissionsResult()
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);

                return;
            }
        }

        // 這項功能之前已經取得使用者的同意，可以直接使用
        if (on) {
            // 取得上一次定位資料
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null) {
                        /*Toast.makeText(SplashActivity.this, "成功取得上一次定位",
                                Toast.LENGTH_LONG).show();*/
                        mlocation = location;
                    } else {
                        Toast.makeText(SplashActivity.this, "無法定位的資料",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            // 準備一個LocationRequest物件，設定定位參數，在啟動定位時使用
            LocationRequest locationRequest = LocationRequest.create();
            // 設定二次定位之間的時間間隔，單位是微秒。
            locationRequest.setInterval(5000);
            // 二次定位之間的最大距離，單位是公尺。
            locationRequest.setSmallestDisplacement(5);

            // 啟動定位，如果GPS功能有開啟，優先使用GPS定位，否則使用網路定位。
            if (mLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationRequest.setPriority(
                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                Toast.makeText(SplashActivity.this, "使用GPS定位",
                        Toast.LENGTH_LONG).show();
            } else if (mLocationMgr.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER)) {
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                Toast.makeText(SplashActivity.this, "使用網路定位",
                        Toast.LENGTH_LONG).show();
            }

            // 啟動定位功能
            mFusedLocationClient.requestLocationUpdates(
                    locationRequest, mLocationCallback, Looper.myLooper());
        } else {
            // 停止定位功能
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Toast.makeText(SplashActivity.this, "停止定位", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        enableLocation(false);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(SplashActivity.this,
                "連線成功",
                Toast.LENGTH_LONG).show();
        enableLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(SplashActivity.this,
                "連線失敗",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(SplashActivity.this,
                "onConnectionFailed",
                Toast.LENGTH_LONG).show();
    }
/**************************************************************/
}
