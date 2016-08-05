package kr.nearbyme.nbm.Store;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import kr.nearbyme.nbm.MainActivity;
import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.Writereview.ItemData;
import kr.nearbyme.nbm.data.ItemDataList;
import kr.nearbyme.nbm.data.Shop;
import kr.nearbyme.nbm.data.ShopListResult;
import kr.nearbyme.nbm.data.StoreData;
import kr.nearbyme.nbm.manager.NetworkManager;
import kr.nearbyme.nbm.manager.PropertyManager;
import okhttp3.Request;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements MainActivity.OnNotifyUpdateListener{
    RecyclerView recyclerView;
    StoreListAdapter mAdapter;
    private String keyword = "없음";
    private int order = 2;
    private double locX;
    private double locY;
    private int radius;
    public int onoff = 0;
    public String shop_id;
    EditText searchstorename;
    //private List<String> shop_id= new ArrayList<>();

    //InputMethodManager mIMM;
    ItemData selectItem = null;
    boolean isForced = false;
    ItemDataList SuccessShopList = null;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new StoreListAdapter();
        mAdapter.setOnItemClickListener(new StoreListViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Shop shop) {
                Intent intent = new Intent(getContext(), StoreDetailActivity.class);
                intent.putExtra(StoreDetailActivity.EXTRA_SHOP_ID, shop.getShop_id());
                intent.putExtra(StoreDetailActivity.EXTRA_SHOP_NAME, shop.getShop_name());
                startActivity(intent);

            }
        });
        mAdapter.setOnShopLikeClickListener(new StoreListViewHolder.OnShopLikeClickListener() {
            @Override
            public void onShopLikeClick(View view, Shop shop) {
                shop_id = shop.getShop_id();

                if(PropertyManager.getInstance().getIsGuest() == 0){
                    if(shop.getLiked() == 0){
                        onoff = 1;
                        changeShopLike(shop_id, onoff);
                    }
                    else if(shop.getLiked() == 1){
                        onoff = 0;
                        changeShopLike(shop_id, onoff);
                    }
                    initData();

                }
                else{
                    Toast.makeText(getContext(), "로그인이 필요한 서비스입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mAdapter.setOnItemClickListener(new StoreSearchViewHolder.OnItemClickListener() { //평점순
            @Override
            public void onItemClick(View view, StoreData storeData) {

                order = 1;
                initData();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage("정렬순서");
//                builder.setPositiveButton("평점순", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getContext(), "평점순눌림", Toast.LENGTH_SHORT).show();
//                        order = 1;
//                        initData();
//                    }
//                });
//                builder.setNegativeButton("후기순", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        order = 2;
//                        initData();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();

            }
        });

        mAdapter.setOnOrderClickListener(new StoreSearchViewHolder.OnOrderClickListener() { //후기순
            @Override
            public void onOrderClick(View view, StoreData storeData) {
            order = 2;
            initData();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage("정렬순서");
//                builder.setPositiveButton("평점순", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        order = 1;
//                       initData();
//                    }
//                });
//                builder.setNegativeButton("후기순", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        order = 2;
//                        initData();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
        });

        mAdapter.setOnItemClickListener4(new StoreSearchViewHolder.OnItemClickListener4() { //검색
            @Override
            public void onItemClick4(View view, String str) {
//                Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_SHORT).show();
                keyword = str;
                initData();
            }
        });
//        mIMM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        mAdapter.setOnPopupClickListener(new StoreSearchViewHolder.OnPopupClickListener() {
            @Override
            public void onPopupClick(AdapterView<?> parent, View view, int position, long id,
                                     ArrayAdapter<ItemData> myAdapter, EditText store, ListPopupWindow popupWindow) {
                selectItem = myAdapter.getItem(position);
                isForced = true;
                store.setText(selectItem.name);
                isForced = false;
                popupWindow.dismiss();
            }
        });

        mAdapter.addOnStoreSearchListener(new StoreSearchViewHolder.OnStoreSearchListener(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count, final ArrayAdapter<ItemData> myAdapter, final ListPopupWindow popupWindow) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text) && !isForced) {

                    NetworkManager.getInstance().getShopNameList(text, new NetworkManager.OnResultListener<ItemDataList>() {
                        @Override
                        public void onSuccess(Request request, ItemDataList result) {
                            SuccessShopList = result;
                        }

                        @Override
                        public void onFail(Request request, IOException exception) {

                        }
                    });
                } else {
                    myAdapter.clear();
                    popupWindow.dismiss();
                }

                if(SuccessShopList != null) {
                    myAdapter.clear();
                    myAdapter.addAll(SuccessShopList.result);
                    if (myAdapter.getCount() > 0) {
                        popupWindow.show();
                    }
                    SuccessShopList = null;
                }
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
        locX = PropertyManager.getInstance().getLatitude();
        locY = PropertyManager.getInstance().getLongitude();
        radius = PropertyManager.getInstance().getMyRadius();

        initData();
        recyclerView.scrollToPosition(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_store);

        View v = inflater.inflate(R.layout.view_search_store, null);
        searchstorename = (EditText) v.findViewById(R.id.edit_store_search);


//        buttonChange = (ImageView) v.findViewById(R.id.btn_order); //평점순
//        buttonChange2 = (ImageView) v.findViewById(R.id.btn_order2); //후기순

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    private void initData() {
        

        NetworkManager.getInstance().getShopList(getContext()
                , keyword, order, locX, locY, radius, new NetworkManager.OnResultListener<ShopListResult>() {

            @Override
            public void onSuccess(Request request, ShopListResult result) {
                mAdapter.clear();
                mAdapter.addAll(result.result);

//                for(int i=0; i<result.result.size(); i++){
//                    shop_id.add(result.result.get(i).getShop_id());
//                }


            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(getContext(), "네트워크 에러", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onNotifyUpdate() {
        initData();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).removeOnNotifyUpdateListener(this);
    }

}

