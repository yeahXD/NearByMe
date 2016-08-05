package kr.nearbyme.nbm.Store;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.data.PostResult;
import kr.nearbyme.nbm.data.Shop;
import kr.nearbyme.nbm.data.ShopDetailResult;

/**
 * Created by CHOIMOONYOUNG on 2016. 5. 20..
 */
public class StoreDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_STOREINFO = 0;
    public static final int VIEW_TYPE_STORESTYLE_TITLE = 2;
    public static final int VIEW_TYPE_STORESTYLE = 3;
    public static final int VIEW_TYPE_STOREDSNR = 1;

    List<PostResult> items = new ArrayList<PostResult>();
    Shop shopDetail = new Shop();

    ShopDetailResult result = new ShopDetailResult();

    StoreInfoViewHolder.OnLikeClickListener likeClickListener;
    public void setOnLikeClickListener(StoreInfoViewHolder.OnLikeClickListener listener) {
        likeClickListener = listener;
    }

    StoreInfoViewHolder.OnCallClickListener callClickListener;
    public void setOnCallClickListener(StoreInfoViewHolder.OnCallClickListener listener) {
        callClickListener = listener;
    }

    StoreInfoViewHolder.OnMapClickListener mapClickListener;
    public void setOnMapClickListener(StoreInfoViewHolder.OnMapClickListener listener) {
        mapClickListener = listener;
    }

    ReviewViewHolder.OnItemClickListener mListener;
    public void setOnItemClickListener(ReviewViewHolder.OnItemClickListener listener) {
        mListener = listener;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setResult(ShopDetailResult _result) {
        result = _result;
    }

    public void addAll(List<PostResult> posts) {
        items.addAll(posts);
        notifyDataSetChanged();
    }

    public void add(PostResult r) {
        items.add(r);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) return VIEW_TYPE_STOREINFO;
        position--;
        if (result.getShop_info().getDsnr_info().size() > 0) {
            if (position < result.getShop_info().getDsnr_info().size())
                return VIEW_TYPE_STOREDSNR;
            position -= result.getShop_info().getDsnr_info().size();
        }
        if (position == 0) return VIEW_TYPE_STORESTYLE_TITLE;
        position--;
        if(result.post_info.size() > 0){
            if(position < result.post_info.size())
                return VIEW_TYPE_STORESTYLE;
            position -= result.post_info.size();
        }
        throw new IllegalArgumentException("invalid position");
    }

    View view;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_STOREINFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_store_info, parent, false);
                return new StoreInfoViewHolder(view);
            case VIEW_TYPE_STOREDSNR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dsnr_info, parent, false);
                return new StoreDsnrViewHolder(view);
            case VIEW_TYPE_STORESTYLE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_style, parent, false);
                return new StoreDetailTitleHolder(view);
            case VIEW_TYPE_STORESTYLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_review, parent, false);
                return new ReviewViewHolder(view);
        }
        throw new IllegalArgumentException("invalid position");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            StoreInfoViewHolder storeInfoViewHolder = (StoreInfoViewHolder) holder;
            storeInfoViewHolder.setOnLikeClickListener(likeClickListener);
            storeInfoViewHolder.setOnCallClickListener(callClickListener);
            storeInfoViewHolder.setOnMapClickListener(mapClickListener);
            storeInfoViewHolder.setStoreInfo(result.getShop_info());
            return;
        }
        position--;
        if (result.getShop_info().getDsnr_info().size() > 0) {
            if (position < result.getShop_info().getDsnr_info().size()) {
                StoreDsnrViewHolder dsnrViewHolder = (StoreDsnrViewHolder) holder;
                if (result.getShop_info().getDsnr_info() == null) {
                    dsnrViewHolder.setDsnr(null);
                } else {
                    dsnrViewHolder.setDsnr(result.getShop_info().getDsnr_info().get(position));
                }
                return;
            }
            position -= result.getShop_info().getDsnr_info().size();
        }
        if (position == 0) return;
        position--;
        if(result.post_info.size() > 0) {
            ReviewViewHolder itemViewHolder = (ReviewViewHolder) holder;
            if (result.getPost_info() == null) {
                itemViewHolder.setReview(null);
            } else {
                itemViewHolder.setReview(result.getPost_info().get(position));
            }
            itemViewHolder.setOnItemClickListener(mListener);
            return;
        }
        position -= result.post_info.size();

        throw new IllegalArgumentException("invalid position");
    }

    @Override
    public int getItemCount() {
        if (result.post_info == null) {
            return 2;
        }else
            return result.getShop_info().getDsnr_info().size() + result.post_info.size() + 2;
    }
}
