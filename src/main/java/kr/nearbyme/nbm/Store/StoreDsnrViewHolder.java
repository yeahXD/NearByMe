package kr.nearbyme.nbm.Store;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.data.Designer;

/**
 * Created by YeahXD on 2016. 8. 3..
 */
public class StoreDsnrViewHolder extends RecyclerView.ViewHolder{
    CircularImageView dsnr_pic;
    TextView dsnr_name;
    TextView dsnr_text;

    public StoreDsnrViewHolder(View itemView) {
        super(itemView);
        dsnr_pic = (CircularImageView) itemView.findViewById(R.id.dsnr_pic);
        dsnr_name = (TextView) itemView.findViewById(R.id.dsnr_name);
        dsnr_text = (TextView) itemView.findViewById(R.id.dsnr_text);
    }

    public void setDsnr(Designer designer) {
        Glide.with(dsnr_pic.getContext()).load(designer.getDsnr_profilePic()).into(dsnr_pic);
        dsnr_name.setText(designer.getDsnr_name());
        dsnr_text.setText(designer.getDsnr_info());
    }
}
