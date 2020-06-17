package mc.com.geoplaces.views.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

import mc.com.geoplaces.R;
import mc.com.geoplaces.models.entities.DeliveryEntity;
import mc.com.geoplaces.models.repositories.DeliveryRepository;
import mc.com.geoplaces.utils.Utils;


public class DeliveryDetailsFragment extends Fragment implements OnMapReadyCallback {


    private MapView mapView;
    private GoogleMap googleMap;
    private Integer deliveryId = null;
    private DeliveryRepository deliveryRepository;
    private DeliveryEntity deliveryEntity;
    private TextView descriptionTextView, addressTextView;
    private ImageView deliveryItemImageView;
    private FrameLayout deliveryContainerLay;
    private static DeliveryDetailsFragment fragment;

    public DeliveryDetailsFragment() {
    }

    public static DeliveryDetailsFragment newInstance(int id) {
        fragment = new DeliveryDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("delivery_id", id);
        fragment.setArguments(args);
        return fragment;
    }
    public static DeliveryDetailsFragment getInstance(){
        return fragment;
    }
    public static DeliveryDetailsFragment newInstance() {
        return new DeliveryDetailsFragment();
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            deliveryId = bundle.getInt("delivery_id");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readBundle(getArguments());
        if (deliveryId != null){
            deliveryRepository = new DeliveryRepository();
            deliveryEntity = new DeliveryEntity();
            deliveryEntity = deliveryRepository.getDeliveryById(deliveryId);
            if (!Utils.isTablet(getContext())){
                deliveryContainerLay.setVisibility(View.VISIBLE);
                addressTextView.setText(deliveryEntity.getAddress());
                descriptionTextView.setText(deliveryEntity.getDescription());
                Picasso.get()
                        .load(deliveryEntity.getImageUrl())
                        .placeholder(R.mipmap.img_place_holder)
                        .error(R.mipmap.img_place_holder_error)
                        .into(deliveryItemImageView);
            } else {
                deliveryContainerLay.setVisibility(View.GONE);
            }
        }else
            deliveryContainerLay.setVisibility(View.GONE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_delivery_details, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.title_delivery_details_txt);
        deliveryContainerLay = view.findViewById(R.id.item_container_fl);
        descriptionTextView = view.findViewById(R.id.description_tv);
        addressTextView = view.findViewById(R.id.address_tv);
        deliveryItemImageView = view.findViewById(R.id.delivery_item_iv);
        mapView =  view.findViewById(R.id.mapView);
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
        if (deliveryId == null){
            location = new LatLng(22.336093, 114.155288);
        } else {
            location = new LatLng(deliveryEntity.getLat(), deliveryEntity.getLng());
            this.googleMap .addMarker(new MarkerOptions().position(location).title(deliveryEntity.getAddress()).snippet(deliveryEntity.getDescription()));
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15).build();
        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void updatePosition(int id){
        this.googleMap.clear();
        DeliveryRepository deliveryRepository2 = new DeliveryRepository();
        DeliveryEntity deliveryEntity2 = new DeliveryEntity();
        deliveryEntity2 = deliveryRepository2.getDeliveryById(id);
        LatLng location = new LatLng(deliveryEntity2.getLat(), deliveryEntity2.getLng());
        this.googleMap .addMarker(new MarkerOptions().position(location).title(deliveryEntity2.getAddress()).snippet(deliveryEntity2.getDescription()));
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
