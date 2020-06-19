package mc.com.geoplaces.views.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mc.com.geoplaces.R;
import mc.com.geoplaces.models.entities.TrafficEntity;
import mc.com.geoplaces.models.repositories.TrafficRepository;
import mc.com.geoplaces.models.repositories.OnTrafficProblemsLoadedCallback;
import mc.com.geoplaces.utils.Utils;
import mc.com.geoplaces.views.adapters.TrafficAdapter;
import mc.com.geoplaces.views.components.CardOnClickListener;
import mc.com.geoplaces.views.components.CardOnLoadMoreListener;
import mc.com.geoplaces.views.components.CardOnLoadMoreScroll;


public class TrafficCardFragment extends Fragment {

    private RecyclerView trafficRecyclerView;
    private TrafficAdapter trafficAdapter;
    private RelativeLayout loadingContainer, reloadContainer;
    private Button reloadButton;
    private ArrayList<TrafficEntity> trafficEntities;
    private TrafficRepository trafficRepository;
    private CardOnLoadMoreScroll scrollListener;
    private LinearLayoutManager linearLayoutManager;

    public TrafficCardFragment() {
        trafficEntities = new ArrayList<>();
    }

    public static TrafficCardFragment newInstance() {
        TrafficCardFragment fragment = new TrafficCardFragment();
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
        trafficRepository = new TrafficRepository();
        loadData(false);
    }

    private void initView(View view){
        trafficRecyclerView = view.findViewById(R.id.traffic_rv);
        loadingContainer = view.findViewById(R.id.loading_container);
        reloadContainer = view.findViewById(R.id.reload_container);
        reloadButton = view.findViewById(R.id.reload_btn);
        trafficRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        trafficRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setEvent(){
        trafficAdapter = new TrafficAdapter(getContext(), trafficEntities, new CardOnClickListener() {

            @Override
            public void onClick(TrafficEntity trafficEntity) {
                if (Utils.isTablet(getContext())){
                    ((TrafficDetailsFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_traffic_details_container_ll)
                    ).updatePosition(trafficEntity.getId());
//                    DeliveryDetailsFragment.getInstance().updatePosition(deliveryEntity.getId());
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_ll, TrafficDetailsFragment.newInstance(trafficEntity.getId()))
                            .addToBackStack(null)
                            .commit();
                }

            }
        });

        trafficRecyclerView.setAdapter(trafficAdapter);
        scrollListener = new CardOnLoadMoreScroll(linearLayoutManager);
        scrollListener.setOnLoadMoreListener(new CardOnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(true);
            }
        });
        trafficRecyclerView.addOnScrollListener(scrollListener);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadContainer.setVisibility(View.GONE);
                loadData(false);
            }
        });
    }

    private void loadData(final boolean hasNext){

        if (hasNext) {
            trafficAdapter.addLoadingView();
        } else {
            trafficEntities.clear();
            loadingContainer.setVisibility(View.VISIBLE);
        }

        trafficRepository.getTrafficProblems(getContext(), hasNext, new OnTrafficProblemsLoadedCallback() {
            @Override
            public void onSuccess(ArrayList<TrafficEntity> trafficEntitiesResult) {
                if (hasNext){
                    trafficAdapter.removeLoadingView();
                    scrollListener.setLoaded();
                    trafficEntities.addAll(trafficEntitiesResult);
                    trafficAdapter.notifyDataSetChanged();
                } else {
                    trafficEntities.addAll(trafficEntitiesResult);
                    loadingContainer.setVisibility(View.GONE);
                    trafficAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorState) {
                if (hasNext) {
                    trafficAdapter.removeLoadingView();
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
        View view = inflater.inflate(R.layout.fragment_traffic_list, container, false);
        initView(view);
        setEvent();
        return view;
    }

}
