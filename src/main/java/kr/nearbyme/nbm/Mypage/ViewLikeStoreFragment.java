package kr.nearbyme.nbm.Mypage;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.Store.StoreDetailActivity;
import kr.nearbyme.nbm.data.LikeShopResult;
import kr.nearbyme.nbm.data.LikeShopResults;
import kr.nearbyme.nbm.data.Shop;
import kr.nearbyme.nbm.manager.NetworkManager;
import kr.nearbyme.nbm.manager.PropertyManager;
import okhttp3.Request;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewLikeStoreFragment extends Fragment {
    RecyclerView recyclerView;
    LikeStoreAdapter mAdapter;
    int onoff = 1;
    String shop_id;



    public ViewLikeStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new LikeStoreAdapter();
        mAdapter.setOnItemClickListener(new LikeStoreViewHolder.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Shop shop) {
                Toast.makeText(getContext(), "눌렸습니다", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), StoreDetailActivity.class);
                intent.putExtra(StoreDetailActivity.EXTRA_SHOP_ID, shop.getShop_id());
                startActivity(intent);
            }

        });
        mAdapter.setOnItemClickListener2(new LikeStoreViewHolder.OnItemClickListener2() {
            @Override
            public void onItemClick2(View view, Shop shop) {

                shop_id = shop.getShop_id();


                if (shop.getLiked() == 0) {
                    onoff = 1;
                    changeShopLike(shop_id, onoff);
                } else if (shop.getLiked() == 1) {
                    onoff = 0;
                    changeShopLike(shop_id, onoff);
                }
                initData();


            }
        });


    }

    private void changeShopLike(String shop_id, int onoff) {

        NetworkManager.getInstance().changeShopLike(shop_id, onoff, new NetworkManager.OnResultListener<String>() {
            @Override
            public void onSuccess(Request request, String result) {
                initData();
            }

            @Override
            public void onFail(Request request, IOException exception) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
        recyclerView.scrollToPosition(0);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_like_store, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_likestore);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }

    private void initData() {

        if(PropertyManager.getInstance().getIsGuest() == 0){
            NetworkManager.getInstance().getLikeShop(new NetworkManager.OnResultListener<LikeShopResults>() {
                @Override
                public void onSuccess(Request request, LikeShopResults result) {

                    mAdapter.clear();
                    List<Shop> shopList = new ArrayList<Shop>();
                    for (LikeShopResult tmp : result.result) {
                        shopList.add(tmp.shop);
                    }
                    mAdapter.addAll(shopList);

                }

                @Override
                public void onFail(Request request, IOException exception) {
                    Toast.makeText(getContext(), "exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }



    }

    private void changeShopLike(String shop_id) {

        NetworkManager.getInstance().changeShopLike(shop_id, onoff, new NetworkManager.OnResultListener<String>() {
            @Override
            public void onSuccess(Request request, String result) {
                initData();
            }

            @Override
            public void onFail(Request request, IOException exception) {

            }
        });
    }

}
