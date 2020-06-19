package mc.com.geoplaces.views.fragments;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import mc.com.geoplaces.R;
import mc.com.geoplaces.models.entities.TrafficEntity;
import mc.com.geoplaces.models.repositories.TrafficRepository;
import mc.com.geoplaces.utils.Utils;


public class TrafficDetailsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Integer trafficId = null;
    private TrafficRepository trafficRepository;
    private TrafficEntity trafficEntity;
    private TextView addressTextView, categoryTextView, dateTextView;
    private ImageView trafficItemImageView;
    private FrameLayout trafficContainerLay;
    private static TrafficDetailsFragment fragment;

    public TrafficDetailsFragment() {
    }

    public static TrafficDetailsFragment newInstance(int id) {
        fragment = new TrafficDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("traffic_id", id);
        fragment.setArguments(args);
        return fragment;
    }

    public static TrafficDetailsFragment getInstance(){
        return fragment;
    }

    public static TrafficDetailsFragment newInstance() {
        return new TrafficDetailsFragment();
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            trafficId = bundle.getInt("traffic_id");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readBundle(getArguments());
        if (trafficId != null) {
            trafficRepository = new TrafficRepository();
            trafficEntity = new TrafficEntity();
            trafficEntity = trafficRepository.getTrafficProblemsById(trafficId);
            if (!Utils.isTablet(getContext())) {
                trafficContainerLay.setVisibility(View.VISIBLE);
                addressTextView.setText(trafficEntity.getAddress());
                categoryTextView.setText(trafficEntity.getType());
                dateTextView.setText(trafficEntity.getDate());

                if (trafficEntity.getType().equals("道路維護通報"))
                    trafficItemImageView.setImageResource(R.drawable.road_stuck_cat);
                else if (trafficEntity.getType().equals("人手孔施工通報"))
                    trafficItemImageView.setImageResource(R.drawable.manhole_squirrel);
                else
                    trafficItemImageView.setImageResource(R.drawable.construction_cat);

//                Picasso.get()
//                        .load(trafficEntity.getImageUrl())
//                        .placeholder(R.mipmap.img_place_holder)
//                        .error(R.mipmap.img_place_holder_error)
//                        .into(trafficItemImageView);

            } else {
                trafficContainerLay.setVisibility(View.GONE);
            }
        } else
            trafficContainerLay.setVisibility(View.GONE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_traffic_details, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.title_delivery_details_txt);
        trafficContainerLay = view.findViewById(R.id.item_container_fl);
        addressTextView = view.findViewById(R.id.address_tv);
        categoryTextView = view.findViewById(R.id.type_tv);
        dateTextView = view.findViewById(R.id.date_tv);

        trafficItemImageView = view.findViewById(R.id.traffic_item_iv);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        readBundle(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng location;

        if (trafficId == null){
            location = new LatLng(22.336093, 114.155288);
        }
        else {
//            location = new LatLng(deliveryEntity.getLat(), deliveryEntity.getLng());
//            String AddressString = "109年度中山區民生東路3段東往西車道（復興北路至建國北路2段）路面更新工程";
//            String AddressString = "義村里忠孝東路3段276巷、248巷13弄(248巷7弄至復興南路1段)路面更新";
//            String AddressString = "民有里民生東路三段113巷6弄路面更新";

            String address = trafficEntity.getAddress();
            String updatedAddress = UpdateAddress(address);
            location = getLocationFromAddress(this.getContext(), updatedAddress);
            if (location == null) {
                location = new LatLng(25.06, 121.54);
            }
            this.googleMap.addMarker(
                    new MarkerOptions()
                            .position(location)
                            .title(updatedAddress)
                            .snippet(trafficEntity.getType() + "\n" + trafficEntity.getDate())
            .flat(true));
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15).build();
        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public LatLng getLocationFromAddress(Context context, String inputAddress)
    {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng AddressLatLng = null;

        // GeoCoder 無法執行
        if (!Geocoder.isPresent())
        {
            Log.d("getLocationFromAddress", "Geocoder Fail");
            return null;
        }
        else Log.d("getLocationFromAddress", "Geocoder Success");
        try
        {
            address = coder.getFromLocationName(inputAddress, 5);
            // 找不到結果
            if (address == null)
            {
                Log.d("getLocationFromAddress", "Result Not Found");
                return null;
            }
            // 第一個結果
            Address location = address.get(0);
            double Lat = location.getLatitude();
            double Lon = location.getLongitude();
            Log.d("getLocationFromAddress", "Latitude:" + String.valueOf(Lat));
            Log.d("getLocationFromAddress", "Longitude:" + String.valueOf(Lon));
            AddressLatLng = new LatLng(Lat, Lon);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return AddressLatLng;
    }

    public String UpdateAddress(String inputAddress)
    {
        String returnAddress;
        int FirstIndex, LastIndex;
        FirstIndex = inputAddress.indexOf("區");

        if (FirstIndex == -1) {
            FirstIndex = inputAddress.indexOf("里");
            if (FirstIndex == -1)
            {
                FirstIndex = 0;
            }
            else FirstIndex -= 2;
        }
        else
            FirstIndex -= 2;   // 區前2字開始取

        LastIndex = inputAddress.indexOf("號");
        if (LastIndex == -1) {
            LastIndex = inputAddress.indexOf("巷");
            if (LastIndex == -1) {
                LastIndex = inputAddress.indexOf("段");
                if (LastIndex == -1) {
                    LastIndex = inputAddress.indexOf("路");
                    if (LastIndex == -1) {
                        LastIndex = inputAddress.indexOf("街");
                    }
                }
            }
        }
        returnAddress = inputAddress.substring(FirstIndex, LastIndex + 1);
//        returnAddress = SplitString(inputAddress, "至", 0);
//        returnAddress = SplitString(returnAddress, "溝蓋", 0);
//        returnAddress = SplitString(returnAddress, "路面", 0);

        return returnAddress;
    }

    public String SplitString(String inputAddress, String splitString, int returnPosition)
    {
        String[] result = inputAddress.split(splitString);
        // 若結果大於兩個代表輸入字串比對成功，取指定位置回傳
        if (result.length > 1)
            return result[returnPosition];
        return inputAddress;
    }

    public void updatePosition(int id){
        this.googleMap.clear();
        TrafficRepository trafficRepository2 = new TrafficRepository();
        TrafficEntity trafficEntity2 = new TrafficEntity();
        trafficEntity2 = trafficRepository2.getTrafficProblemsById(id);
        LatLng location = new LatLng(trafficEntity2.getLat(), trafficEntity2.getLng());
        this.googleMap.addMarker(new MarkerOptions().position(location).title(trafficEntity2.getAddress()).snippet(trafficEntity2.getType()));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15).build();
        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
