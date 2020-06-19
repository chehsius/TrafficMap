package mc.com.geoplaces.views.adapters;

import android.content.Context;
import android.os.Handler;

import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import mc.com.geoplaces.R;
import mc.com.geoplaces.models.entities.TrafficEntity;
import mc.com.geoplaces.views.components.CardOnClickListener;

public class TrafficAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_LOADING = 1;

    private List<TrafficEntity> trafficEntities;
    private CardOnClickListener cardOnClickListener;
    private int selectedIndex = -1;
    private Context context;

    public TrafficAdapter(Context context, List<TrafficEntity> trafficEntities, CardOnClickListener cardOnClickListener) {
        this.context = context;
        this.trafficEntities = trafficEntities;
        this.cardOnClickListener = cardOnClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM)
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_traffic, parent, false));
        else if (viewType == VIEW_LOADING)
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false));
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final TrafficEntity trafficEntity = trafficEntities.get(position);

        if (trafficEntity != null){
            ((ItemViewHolder) holder).addressTextView.setText(trafficEntity.getAddress());
            ((ItemViewHolder) holder).typeTextView.setText(trafficEntity.getType());
            ((ItemViewHolder) holder).dateTextView.setText(trafficEntity.getDate());

            if (trafficEntity.getType().equals("道路維護通報"))
                ((ItemViewHolder) holder).trafficItemImageView.setImageResource(R.drawable.road_stuck_cat);
            else if (trafficEntity.getType().equals("人手孔施工通報"))
                ((ItemViewHolder) holder).trafficItemImageView.setImageResource(R.drawable.manhole_squirrel);
            else
                ((ItemViewHolder) holder).trafficItemImageView.setImageResource(R.drawable.construction_cat);

//            Picasso.get()
//                    .load(trafficEntity.getImageUrl())
//                    .placeholder(R.mipmap.img_place_holder)
//                    .error(R.mipmap.img_place_holder_error)
//                    .into(((ItemViewHolder) holder).trafficItemImageView);

            if (position == selectedIndex){
                ((ItemViewHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                ((ItemViewHolder) holder).addressTextView.setTextColor(context.getResources().getColor(R.color.colorWhite));
                ((ItemViewHolder) holder).typeTextView.setTextColor(context.getResources().getColor(R.color.colorWhite));
                ((ItemViewHolder) holder).dateTextView.setTextColor(context.getResources().getColor(R.color.colorWhite));
            }
            else{
                ((ItemViewHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                ((ItemViewHolder) holder).addressTextView.setTextColor(context.getResources().getColor(R.color.colorGray));
                ((ItemViewHolder) holder).typeTextView.setTextColor(context.getResources().getColor(R.color.colorGray));
                ((ItemViewHolder) holder).dateTextView.setTextColor(context.getResources().getColor(R.color.colorGray));
            }
        }

        ((ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex = position;
                cardOnClickListener.onClick(trafficEntity);
                notifyDataSetChanged();
            }
        });

    }

    public void addLoadingView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                trafficEntities.add(null);
                notifyItemInserted(trafficEntities.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        trafficEntities.removeAll(Collections.singleton(null));
        notifyItemRemoved(trafficEntities.size());
    }

    @Override
    public int getItemCount() {
        return trafficEntities == null ? 0 : trafficEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        return trafficEntities.get(position) == null ? VIEW_LOADING : VIEW_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView addressTextView, typeTextView, dateTextView;
        private ImageView trafficItemImageView;
        private CardView cardView;

        private ItemViewHolder(View view) {
            super(view);
            addressTextView = view.findViewById(R.id.address_tv);
            typeTextView = view.findViewById(R.id.type_tv);
            dateTextView = view.findViewById(R.id.date_tv);
            trafficItemImageView = view.findViewById(R.id.traffic_item_iv);
            cardView = view.findViewById(R.id.container_cv);
        }
    }
}