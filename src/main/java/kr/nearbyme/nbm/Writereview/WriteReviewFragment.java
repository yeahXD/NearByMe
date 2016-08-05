package kr.nearbyme.nbm.Writereview;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wefika.flowlayout.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.nearbyme.nbm.R;
import kr.nearbyme.nbm.Review.ReviewDetailActivity;
import kr.nearbyme.nbm.data.ItemDataList;
import kr.nearbyme.nbm.data.WriteResult;
import kr.nearbyme.nbm.manager.NetworkManager;
import kr.nearbyme.nbm.manager.PropertyManager;
import okhttp3.Request;

//import kr.nearbyme.nbm.Writereview.ItemData;

/**
 * A simple {@link Fragment} subclass.
 */
public class WriteReviewFragment extends Fragment implements WritePostKeywordFragment.WritePostKeyWordDoneClickListener{
    EditText storeNameView, designerNameView;
    RatingBar ratingBar;
    TextView ratingbar_text;
    Button buttonTag, buttonPost;
    FlowLayout tagLayout;
    EditText contentView;
    ImageView ImageUploadView;
    ScrollView scrollView;

    private String shop_id;
    private String dsnr_id;
    private double post_score;
    private String post_content;
    List<String> post_filters = new ArrayList<String>();

    ItemData selectItem = null;
    ItemData selectItem_dsnr = null;

    List<String> shop_ids = new ArrayList<>();
    List<String> shop_names = new ArrayList<>();
    List<String> dsnr_names = new ArrayList<>();
    List<ItemData> shopList = new ArrayList<>();

    private static final int RC_GALLERY = 1;
    private static final int RC_CAMERA = 2;

    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCameraCaptureFile());
        startActivityForResult(intent, RC_CAMERA);
    }

    File mCameraCaptureFile;

    private Uri getCameraCaptureFile() {
        File dir = getContext().getExternalFilesDir("myphoto");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mCameraCaptureFile = new File(dir, "my_photo_"+ System.currentTimeMillis()+".jpg");
        return Uri.fromFile(mCameraCaptureFile);
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/jpeg");
        startActivityForResult(intent, RC_GALLERY);
    }

    File mUploadFile = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUploadFile != null) {
            outState.putString("uploadfile", mUploadFile.getAbsolutePath());
        }
        if (mCameraCaptureFile != null) {
            outState.putString("cameraFile", mCameraCaptureFile.getAbsolutePath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor c = getContext().getContentResolver().query(uri, projection, null, null, null);
                if (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    mUploadFile = new File(path);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(path, opts);
                    ImageUploadView.setImageBitmap(bm);
                }
            }
            return;
        }

        if (requestCode == RC_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {

//                Toast.makeText(MyApplication.getContext(), mCameraCaptureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                File file = mCameraCaptureFile;
//                BitmapFactory.Options opts = new BitmapFactory.Options();
//                opts.inSampleSize = 2;
//                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                mUploadFile = file;
                Glide.with(this).load(mUploadFile).into(ImageUploadView);
//                ImageUploadView.setImageBitmap(bm);
            }
            return;
        }
//        onActivityResult(requestCode, resultCode, data); //
    }

    public WriteReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getShopName();
//        getDsnr();

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("uploadfile");
            if (!TextUtils.isEmpty(path)) {
                mUploadFile = new File(path);
            }
            path = savedInstanceState.getString("cameraFile");
            if (!TextUtils.isEmpty(path)) {
                mCameraCaptureFile = new File(path);
            }
        }
        mAdapter = new ArrayAdapter<ItemData>(getContext(), android.R.layout.simple_list_item_1);
    }

    private void initData() {
//        shop_id = storeNameView.getText().toString();
//        dsnr_id = designerNameView.getText().toString();
        shop_id = selectItem.id;
        dsnr_id = selectItem_dsnr.id;
        post_score = ratingBar.getRating();
        post_content = contentView.getText().toString();
        post_filters = PropertyManager.getInstance().getWritePostfilter();

        NetworkManager.getInstance().getPostUpload(getContext(), shop_id, dsnr_id, post_score, post_content, post_filters, mUploadFile, new NetworkManager.OnResultListener<WriteResult>() {

            @Override
            public void onSuccess(Request request, WriteResult result) {
                Intent intent = new Intent(getContext(), ReviewDetailActivity.class);
                intent.putExtra(ReviewDetailActivity.EXTRA_REVIEW_ID, result.result.post_id);
                startActivity(intent);
            }

            @Override
            public void onFail(Request request, IOException exception) {
                Toast.makeText(getContext(), "server disconnected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    ListPopupWindow popupWindow, popupWindow2;
    ArrayAdapter<ItemData> mAdapter;

    boolean isForced = false;
    boolean isForced2 = false;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_write_review, container, false);

        /* get shop name list for writePost */
        popupWindow = new ListPopupWindow(getContext());
        scrollView = (ScrollView) view.findViewById(R.id.scrollView2);
        storeNameView = (EditText) view.findViewById(R.id.autoCompleteTextView_store);
        popupWindow.setAnchorView(storeNameView);
        popupWindow.setAdapter(mAdapter);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem = mAdapter.getItem(position);
            isForced = true;
            storeNameView.setText(selectItem.name);
            isForced = false;
//                Toast.makeText(getContext(), "매장 이름:" + selectItem.name, Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
//                storeNameView.setFocusableInTouchMode(false);
            designerNameView.requestFocus();
            }
        });

        storeNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text) && !isForced) {

                    NetworkManager.getInstance().getShopNameList(text, new NetworkManager.OnResultListener<ItemDataList>() {
                        @Override
                        public void onSuccess(Request request, ItemDataList result) {
                            mAdapter.clear();
                            mAdapter.addAll(result.result);
                            if (mAdapter.getCount() > 0) {
                                popupWindow.show();
                            }
                        }

                        @Override
                        public void onFail(Request request, IOException exception) {

                        }
                    });
                } else {
                    mAdapter.clear();
                    popupWindow.dismiss();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* get designer list for writePost */
        designerNameView = (EditText) view.findViewById(R.id.autoCompleteTextView_dsnr);
        popupWindow2 = new ListPopupWindow(getContext());
        popupWindow2.setAnchorView(designerNameView);
        popupWindow2.setAdapter(mAdapter);

        popupWindow2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem_dsnr = mAdapter.getItem(position);
                isForced2 = true;
                designerNameView.setText(selectItem_dsnr.name);
                isForced2 = false;

            }
        });

        designerNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();

                if (!TextUtils.isEmpty(keyword) && !isForced2) {

                    NetworkManager.getInstance().getDsnrNameList(selectItem.id, keyword, new NetworkManager.OnResultListener<ItemDataList>() {
                        @Override
                        public void onSuccess(Request request, ItemDataList result) {
                            mAdapter.clear();
                            mAdapter.addAll(result.result);
                            if (mAdapter.getCount() > 0) {
                                popupWindow2.show();
                            }
                        }

                        @Override
                        public void onFail(Request request, IOException exception) {

                        }
                    });
                } else {
                    mAdapter.clear();
                    popupWindow2.dismiss();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar_review);
        ratingbar_text = (TextView) view.findViewById(R.id.ratingbar_text);
        buttonTag = (Button) view.findViewById(R.id.btn_keyword);
        tagLayout = (FlowLayout) view.findViewById(R.id.text_keywords);
        contentView = (EditText) view.findViewById(R.id.edit_review);
        ImageUploadView = (ImageView) view.findViewById(R.id.image_upload);
        buttonPost = (Button) view.findViewById(R.id.btn_post);

        if (mUploadFile != null) {
            Glide.with(this).load(mUploadFile).into(ImageUploadView);
        }


        ImageUploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("사진올리기");
                builder.setPositiveButton("앨범에서 선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getImageFromGallery();

                    }
                });
                builder.setNegativeButton("카메라 촬영", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getImageFromCamera();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
            if(PropertyManager.getInstance().getIsGuest() == 0){
                if(selectItem == null || TextUtils.isEmpty(selectItem.id)){
                    Toast.makeText(getContext(), "매장명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectItem_dsnr == null || TextUtils.isEmpty(selectItem_dsnr.id)){
                    Toast.makeText(getContext(), "디자이너이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(contentView.getText().toString())){
                    Toast.makeText(getContext(), "후기를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mUploadFile == null){
                    Toast.makeText(getContext(), "사진을 올려주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(PropertyManager.getInstance().getWritePostfilter() == null  || TextUtils.isEmpty(PropertyManager.getInstance().getWritePostfilter().get(0))){
                    Toast.makeText(getContext(), "키워드를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                initData();

            }
            else{
                Toast.makeText(getContext(), "로그인이 필요한 서비스입니다", Toast.LENGTH_SHORT).show();
            }

            storeNameView.setText("");
            designerNameView.setText("");
            ratingBar.setRating(0.0f);
            contentView.setText("");
            ImageUploadView.setImageResource(R.drawable.nm_010_btn_imgupload);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                switch(String.valueOf(rating)){
                    case "0.5":
                        ratingbar_text.setText("핵핵핵 별로에요ㅠㅠ");
                        break;
                    case "1.0":
                        ratingbar_text.setText("핵 별로에요ㅠㅠ");
                        break;
                    case "1.5":
                        ratingbar_text.setText("별로에요");
                        break;
                    case "2.0":
                        ratingbar_text.setText("쫌 별로에요");
                        break;
                    case "2.5":
                        ratingbar_text.setText("쪼오끔 별로에요!");
                        break;
                    case "3.0":
                        ratingbar_text.setText("쏘쏘");
                        break;
                    case "3.5":
                        ratingbar_text.setText("쫌 괜찮아요");
                        break;
                    case "4.0":
                        ratingbar_text.setText("좋아요!!!");
                        break;
                    case "4.5":
                        ratingbar_text.setText("짱 좋아요!");
                        break;
                    case "5.0":
                        ratingbar_text.setText("짱짱 최고에요!");
                        break;
                }
            }
        });

        buttonTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WritePostKeywordFragment f = new WritePostKeywordFragment();
                f.setWritePostKeyWordDoneClickListener(WriteReviewFragment.this);
                f.show(getActivity().getSupportFragmentManager(), "create");
            }



        });
        return view;
    }

    public void setKeywords(){
        tagLayout.removeAllViews();
        if (PropertyManager.getInstance().getWritePostfilter() != null) {
            for (int i = 0; i < PropertyManager.getInstance().getWritePostfilter().size(); i++) {
                TextView filterTagsView = new TextView(getContext());
                FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(6, 6, 6, 6);
                filterTagsView.setLayoutParams(lp);
                filterTagsView.setTextSize(10);
                filterTagsView.setBackgroundColor(Color.BLUE);
                filterTagsView.setBackgroundResource(R.drawable.tag_s_001);
                filterTagsView.setText(PropertyManager.getInstance().getWritePostfilter().get(i));
                tagLayout.addView(filterTagsView);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onWritePostKeyWordDoneClick(List<String> keyFilters) {
        setKeywords();
    }
}
