package kr.nearbyme.nbm.Store;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.nearbyme.nbm.R;

/**
 * Created by YeahXD on 2016. 8. 3..
 */
public class StoreDetailTitleHolder extends RecyclerView.ViewHolder {
    TextView text_style;

    public StoreDetailTitleHolder(View itemView) {
        super(itemView);
        text_style = (TextView) itemView.findViewById(R.id.text_style);
    }
}
