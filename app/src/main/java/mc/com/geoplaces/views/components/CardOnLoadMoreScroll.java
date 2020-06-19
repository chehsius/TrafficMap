package mc.com.geoplaces.views.components;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CardOnLoadMoreScroll extends RecyclerView.OnScrollListener {

    private int visibleThreshold = 1;
    private CardOnLoadMoreListener cardOnLoadMoreListener;
    private boolean isLoading;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView.LayoutManager layoutManager;

    public void setLoaded() {
        isLoading = false;
    }

    public boolean getLoaded() {
        return isLoading;
    }

    public void setOnLoadMoreListener(CardOnLoadMoreListener mOnLoadMoreListener) {
        this.cardOnLoadMoreListener = mOnLoadMoreListener;
    }

    public CardOnLoadMoreScroll(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy <= 0) return;

        totalItemCount = layoutManager.getItemCount();

        if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (cardOnLoadMoreListener != null) {
                cardOnLoadMoreListener.onLoadMore();
            }
            isLoading = true;
        }

    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }
}
