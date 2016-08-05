package kr.nearbyme.nbm.Store;

import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import kr.nearbyme.nbm.MyApplication;
import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.Writereview.ItemData;
import kr.nearbyme.nbm.data.Shop;
import kr.nearbyme.nbm.data.StoreData;

/**
 * Created by CHOIMOONYOUNG on 2016. 5. 17..
 */
public class StoreSearchViewHolder extends RecyclerView.ViewHolder {

    ListPopupWindow popupWindow;
    ArrayAdapter<ItemData> mAdapter;
    EditText store;
    Button search;
    ImageView order, order2;
    StoreData mData;
    Shop shopData;

    public interface OnItemClickListener {
        public void onItemClick(View view, StoreData storeData);
    }

    public interface OnOrderClickListener {
        public void onOrderClick(View view, StoreData storeData);
    }

    public interface OnItemClickListener4 {
        public void onItemClick4(View view, String str);
    }

    public interface OnPopupClickListener {
        public void onPopupClick(AdapterView<?> parent, View view, int position, long id,
                                 ArrayAdapter<ItemData> mAdapter, EditText store, ListPopupWindow popupWindow);
    }

    public interface OnStoreSearchListener {
        public void onTextChanged(CharSequence s, int start, int before, int count,
                                  ArrayAdapter<ItemData> mAdapter, ListPopupWindow popupWindow);
    }

    OnStoreSearchListener mTextChangeListener;
    public void addOnStoreSearchListener(OnStoreSearchListener listener) {
        mTextChangeListener = listener;
    }

    OnPopupClickListener mPopupListener;
    public void setOnPopupClickListener(OnPopupClickListener listener) {
        mPopupListener = listener;
    }

    OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    OnOrderClickListener mOrderListener;
    public void setOnOrderClickListener(OnOrderClickListener listener) {
        mOrderListener = listener;
    }

    OnItemClickListener4 mListener4;
    public void setOnItemClickListener4(OnItemClickListener4 listener) {
        mListener4 = listener;
    }

    public StoreSearchViewHolder(View itemView) {
        super(itemView);

        popupWindow = new ListPopupWindow(MyApplication.getContext());
        mAdapter = new ArrayAdapter<ItemData>(MyApplication.getContext(), android.R.layout.simple_list_item_1);
        store = (EditText) itemView.findViewById(R.id.edit_store_search);
        order = (ImageView) itemView.findViewById(R.id.btn_order);
        order2 = (ImageView) itemView.findViewById(R.id.btn_order2);
        //iconView = (ImageView) itemView.findViewById(R.id.image_search);
        search = (Button) itemView.findViewById(R.id.btn_search);

        popupWindow.setAnchorView(store);
        popupWindow.setAdapter(mAdapter);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPopupListener != null) {
                    mPopupListener.onPopupClick(parent, view, position, id, mAdapter, store, popupWindow);
                }
            }
        });

        store.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mTextChangeListener != null){
                    mTextChangeListener.onTextChanged(s, start, before, count, mAdapter, popupWindow);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setVisibility(View.GONE);
                order2.setVisibility(View.VISIBLE);
                if (mListener != null) {
                    mListener.onItemClick(v, mData);
//                    setOrderList();
                }
            }
        });
        order2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setVisibility(View.VISIBLE);
                order2.setVisibility(View.GONE);
                if (mOrderListener != null) {
                    mOrderListener.onOrderClick(v, mData);
//                    setReviewList();
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener4 != null){
                    mListener4.onItemClick4(v, store.getText()+"");
                }
            }
        });
    }


    public void setStoreSearchData(StoreData data){
        mData = data;
        //iconView.setImageDrawable(data.getSearchImg());
    }

}
