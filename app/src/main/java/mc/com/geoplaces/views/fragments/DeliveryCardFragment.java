package mc.com.geoplaces.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mc.com.geoplaces.R;
import mc.com.geoplaces.models.entities.DeliveryEntity;
import mc.com.geoplaces.models.repositories.DeliveryRepository;
import mc.com.geoplaces.models.repositories.OnDeliveryListLoadedCallback;
import mc.com.geoplaces.utils.Utils;
import mc.com.geoplaces.views.adapters.DeliveryAdapter;
import mc.com.geoplaces.views.components.CardOnClickListener;
import mc.com.geoplaces.views.components.CardOnLoadMoreListener;
import mc.com.geoplaces.views.components.CardOnLoadMoreScroll;


public class DeliveryCardFragment extends Fragment {

    private RecyclerView deliveryRecyclerView;
    private DeliveryAdapter deliveryAdapter;
    private RelativeLayout loadingContainer,reloadContainer;
    private Button reloadButton;
    private ArrayList<DeliveryEntity> deliveryEntities;
    private DeliveryRepository deliveryRepository;
    private CardOnLoadMoreScroll scrollListener;
    private LinearLayoutManager linearLayoutManager;

    public DeliveryCardFragment() {
        deliveryEntities = new ArrayList<>();

    }

    public static DeliveryCardFragment newInstance() {
        DeliveryCardFragment fragment = new DeliveryCardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deliveryRepository = new DeliveryRepository();
        loadData(false);
    }

    private void initView(View view){
        deliveryRecyclerView =  view.findViewById(R.id.delivery_rv);
        loadingContainer = view.findViewById(R.id.loading_container);
        reloadContainer = view.findViewById(R.id.reload_container);
        reloadButton = view.findViewById(R.id.reload_btn);
        deliveryRecyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);
    }
    private void setEvent(){
        deliveryAdapter = new DeliveryAdapter(getContext(),deliveryEntities, new CardOnClickListener() {
            @Override
            public void onClick(DeliveryEntity deliveryEntity) {
                if (Utils.isTablet(getContext())){
                    ((DeliveryDetailsFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_delivery_details_container_ll)
                    ).updatePosition(deliveryEntity.getId());
//                    DeliveryDetailsFragment.getInstance().updatePosition(deliveryEntity.getId());
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_ll, DeliveryDetailsFragment.newInstance(deliveryEntity.getId()))
                            .addToBackStack(null)
                            .commit();
                }

            }
        });
        deliveryRecyclerView.setAdapter(deliveryAdapter);
        scrollListener=new CardOnLoadMoreScroll(linearLayoutManager);
        scrollListener.setOnLoadMoreListener(new CardOnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(true);
            }
        });
        deliveryRecyclerView.addOnScrollListener(scrollListener);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadContainer.setVisibility(View.GONE);
                loadData(false);
            }
        });
    }

    private void loadData(final boolean hasNext){
        if (hasNext){
            deliveryAdapter.addLoadingView();
        } else {
            deliveryEntities.clear();
            loadingContainer.setVisibility(View.VISIBLE);
        }
        deliveryRepository.getDeliveryList(getContext(),hasNext ,  new OnDeliveryListLoadedCallback() {
            @Override
            public void onSuccess(ArrayList<DeliveryEntity> deliveryEntitiesResult) {
                if (hasNext){
                    deliveryAdapter.removeLoadingView();
                    scrollListener.setLoaded();
                    deliveryEntities.addAll(deliveryEntitiesResult);
                    deliveryAdapter.notifyDataSetChanged();
                } else {
                    deliveryEntities.addAll(deliveryEntitiesResult);
                    loadingContainer.setVisibility(View.GONE);
                    deliveryAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String errorState) {
                if (hasNext){
                    deliveryAdapter.removeLoadingView();
                    scrollListener.setLoaded();
                } else {
                    loadingContainer.setVisibility(View.GONE);
                    reloadContainer.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_list, container, false);
        initView(view);
        setEvent();
        return view;
    }

}
