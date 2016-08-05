package kr.nearbyme.nbm.Store;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.Review.ReviewDetailActivity;
import kr.nearbyme.nbm.data.PostResult;
import kr.nearbyme.nbm.data.Shop;
import kr.nearbyme.nbm.data.ShopDetailResult;
import kr.nearbyme.nbm.manager.NetworkManager;
import kr.nearbyme.nbm.manager.PropertyManager;
import okhttp3.Request;

public class StoreDetailActivity extends AppCompatActivity {
    public static final String EXTRA_SHOP_ID = "shop_id";
    public static final String EXTRA_SHOP_NAME = "shop_name";
    String shop_id;
    String shop_name;
    String shop_phonenum;
    int onoff;
    RecyclerView recyclerView;
    StoreDetailAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    double locX;
    double locY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new StoreDetailAdapter();
        setContentView(R.layout.activity_store_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_storedetail);
        recyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mAdapter.getItemViewType(position);
                if (type == StoreDetailAdapter.VIEW_TYPE_STORESTYLE) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();
        shop_id = intent.getStringExtra(EXTRA_SHOP_ID);
        shop_name = intent.getStringExtra(EXTRA_SHOP_NAME);

        actionBar.setTitle(shop_name);
        initData();

        mAdapter.setOnCallClickListener(new StoreInfoViewHolder.OnCallClickListener() {
            @Override
            public void onCallClick(View view, Shop shop) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + shop_phonenum));
                startActivity(myIntent);
            }
        });

        mAdapter.setOnLikeClickListener(new StoreInfoViewHolder.OnLikeClickListener() {
            @Override
            public void onLikeClick(View view, Shop shop) {
                if (PropertyManager.getInstance().getIsGuest() == 0) {
                    if (shop.getLiked() == 0) {
                        onoff = 1;
                        changeShopLike(shop_id, onoff);
                    } else if (shop.getLiked() == 1) {
                        onoff = 0;
                        changeShopLike(shop_id, onoff);
                    }
                    initData();
                } else {
                    Toast.makeText(StoreDetailActivity.this, "로그인이 필요한 서비스입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAdapter.setOnMapClickListener(new StoreInfoViewHolder.OnMapClickListener() {
            @Override
            public void onMapClick(View view, Shop shop) {
                locX = shop.getShop_locX();
                locY = shop.getShop_locY();

                Intent intent = new Intent(StoreDetailActivity.this, StoreMapActivity.class);
                intent.putExtra(StoreMapActivity.EXTRA_SHOP_LOCX, shop.getShop_locX());
                intent.putExtra(StoreMapActivity.EXTRA_SHOP_LOCY, shop.getShop_locY());
                startActivity(intent);

            }
        });

        mAdapter.setOnItemClickListener(new ReviewViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PostResult post) {
                Intent intent = new Intent(StoreDetailActivity.this, ReviewDetailActivity.class);
                intent.putExtra(ReviewDetailActivity.EXTRA_REVIEW_ID, post.post.getPost_id());
                startActivity(intent);
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


    private void initData() {

        NetworkManager.getInstance().getShopDetail(shop_id, new NetworkManager.OnResultListener<ShopDetailResult>() {
            @Override
            public void onSuccess(Request request, ShopDetailResult result) {

                mAdapter.clear();
                mAdapter.setResult(result);
                shop_phonenum = result.shop_info.getShop_phone();
                onoff = result.shop_info.getLiked();
                //mAdapter.addStore(result.getShop_info());
               // mAdapter.addAll(result.getPost_info());
            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(StoreDetailActivity.this, "exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
