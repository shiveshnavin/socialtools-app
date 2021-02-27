package com.dotpot.app.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenericItem;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.dotpot.app.ui.BaseActivity.REQ_UPLOAD_DOC;
import static com.dotpot.app.ui.BaseActivity.REQ_UPLOAD_FILE;
import static com.dotpot.app.ui.BaseActivity.REQ_UPLOAD_IMG_CAM;
import static com.dotpot.app.ui.BaseActivity.REQ_UPLOAD_IMG_GAL;
import static com.dotpot.app.ui.BaseActivity.REQ_UPLOAD_LOCATION;

// For Picking Place
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
//
public class TextAndContentPicker {

    public static final int PICK_TEXT_ONLY = 0;
    public static final int PICK_IMAGE_ONLY = 1;
    public static final int PICK_LOCAION_ONLY = 2;
    public static final int PICK_ALL = 3;

    BaseActivity act;
    int pickWhat = PICK_TEXT_ONLY;
    GenricObjectCallback<ContentData> callback;
    ContentData contentData;
    String hint = "Say something...";
    //1 pickable , 0 non-pickable , 2 picked
    ArrayList<GenericItem> contentTypes = new ArrayList<>();
    GenriXAdapter<GenericItem> contentDataGenriXAdapter;
    private LinearLayout contImage;
    private RecyclerView contentList;
    private TextInputLayout contText;
    private TextInputEditText subText;
    private View done,cancel;


    public LinearLayout searchLayoutWrapper;
    public View searchBox;
    public EditText searchDisease;

    public TextAndContentPicker(BaseActivity act, GenricObjectCallback<ContentData> callback) {
        this.act = act;

        if(callback==null){
            callback=new GenricObjectCallback<ContentData>() {
                @Override
                public void onEntity(ContentData data) {

                }
            };
        }
        this.callback = callback;
        contentData = new ContentData();
    }


    public TextAndContentPicker(BaseActivity act, int pickWhat, GenricObjectCallback<ContentData> callback, ContentData contentData) {
        this.act = act;
        this.pickWhat = pickWhat;
        this.callback = callback;
        this.contentData = contentData;
    }

    public void setPickWhat(int pickWhat) {
        this.pickWhat = pickWhat;
    }

    public TextInputEditText getSubText() {
        return subText;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public View getRootView() {
        return root;
    }

    public View root;
    public void pick(){
        pick(new View(act));
    }
    public void pick(View anchor) {

        root = act.getLayoutInflater().inflate(R.layout.utl_diag_content_picker, null);
        findViews(root);


        if(pickWhat==PICK_TEXT_ONLY) {

            contImage.setVisibility(View.GONE);
        }
        GenericItem g = new GenericItem();
        g.text = Constants.ATTACHMENT_TYPE_IMAGE;
        g.status = 1;
        contentTypes.add(g);

        g = new GenericItem();
        g.text = Constants.ATTACHMENT_TYPE_DOC;
        g.status = 1;
        contentTypes.add(g);

        g = new GenericItem();
        g.text = Constants.ATTACHMENT_TYPE_FILE;
        g.status = 1;
        contentTypes.add(g);

        g = new GenericItem();
        g.text = Constants.ATTACHMENT_TYPE_LOCATION;
        g.status = 1;
        contentTypes.add(g);

        contentDataGenriXAdapter =
                new GenriXAdapter<GenericItem>(act, R.layout.utl_row_story, contentTypes) {

                    @Override
                    public void onBindViewHolder(@NonNull GenriXAdapter.CustomViewHolder viewHolder, int i) {

                        final GenericItem it = contentTypes.get(viewHolder.getAdapterPosition());
                        final CustomViewHolder vh = (CustomViewHolder) viewHolder;
                        final ImageView img = vh.imageView(R.id.image);
                        final int dr = getDrawableByTitle(it.text);
                        vh.base.findViewById(R.id.cont_image).setBackground(null);
                        img.setImageResource(dr);
                        img.setPadding(5, 5, 5, 5);
                        vh.textView(R.id.name).setText(it.text);
                        utl.addPressReleaseAnimation(vh.base);
                        switch (it.status) {
                            case 0:
                                utl.setImageTint(img, true, R.color.grey_300);
                                vh.base.setOnClickListener((v) -> {

                                });
                                break;
                            case 1:
                                utl.setImageTint(img, true, R.color.colorPrimary);
                                vh.base.setOnClickListener((v) -> {
                                    callFilePicker(it);
                                });
                                break;
                            case 2:
                                img.setImageResource(R.drawable.cancel);
                                utl.setImageTint(img, false, R.color.material_red_300);
                                vh.base.setOnClickListener((v) -> {
                                    contentTypes.stream().forEach((g) -> {
                                        g.status = 1;
                                    });
                                    contentData=new ContentData();
                                    notifyDataSetChanged();
                                });
                                break;
                        }
                    }
                };
        contentList.setAdapter(contentDataGenriXAdapter);
        final FadePopup mBottomSheetDialog=
                new FadePopup(act,anchor,root);

        cancel.setOnClickListener((v)->{
            mBottomSheetDialog.dismiss();
        });
        done.setOnClickListener((v) -> {
            contentData.text = subText.getText().toString();
            if (contentData.locationName != null) {
                contentData.text+="\n"+contentData.locationName;
            }
            contentData.text=contentData.text.trim();
            callback.onEntity(contentData);
            mBottomSheetDialog.dismiss();
        });
        contText.setHint(hint);
        subText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                {
                    contentData.text = subText.getText().toString();
                }
                return true;
            }
            return false;
        });


        try {
            mBottomSheetDialog.popup().findViewById(R.id.dismiss).setVisibility(View.GONE);
            mBottomSheetDialog.cancle.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callFilePicker(GenericItem type) {

        switch (type.text) {

            case Constants.ATTACHMENT_TYPE_IMAGE:
                pickImage();
                break;
            case Constants.ATTACHMENT_TYPE_DOC:
                pickDoc();
                break;
            case Constants.ATTACHMENT_TYPE_FILE:
                pickFile();
                break;
            case Constants.ATTACHMENT_TYPE_LOCATION:
                pickLocation();
                break;
        }

    }

    public static  @DrawableRes
    int getDrawableByTitle(String type) {

        switch (type) {

            case Constants.ATTACHMENT_TYPE_IMAGE:
                return R.drawable.ic_add_a_photo_black_24dp;
            case Constants.ATTACHMENT_TYPE_DOC:
                return R.drawable.ic_insert_drive_file_black_24dp;
            case Constants.ATTACHMENT_TYPE_FILE:
                return R.drawable.ic_add_attachment;
            case Constants.ATTACHMENT_TYPE_LOCATION:
                return R.drawable.ic_place_black_24dp;
        }

        return R.drawable.ic_attachment_black_24dp;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {

            case REQ_UPLOAD_IMG_CAM:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap photo = (Bitmap) intent.getExtras().get("data");
                        File file = new File(act.getCacheDir(), "verif_" + act.user.getId() + ".png");
                        photo.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                        processImage(null, file, requestCode);
                    } catch (Exception | OutOfMemoryError e) {
                        if (utl.DEBUG_ENABLED) e.printStackTrace();
                    }
                }

                break;
            case REQ_UPLOAD_IMG_GAL:
            case REQ_UPLOAD_FILE:
            case REQ_UPLOAD_DOC:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = intent.getData();
                    try {
                        processImage(selectedImage, null, requestCode);
                    } catch (Exception e) {
                        if (utl.DEBUG_ENABLED) e.printStackTrace();
                    }
                }
                break;
            case REQ_UPLOAD_LOCATION:
                if (resultCode == RESULT_OK) {
                    contentData.attachmentType=Constants.ATTACHMENT_TYPE_LOCATION;
                    utl.toast(act,"Not Implemented : Places Picker");
//                    Add Place
//                    Place place = Autocomplete.getPlaceFromIntent(intent);
//                    if (place.getLatLng() != null) {
//                        contentData.locationName = place.getName();
//                        contentData.location = place.getLatLng().latitude+","+place.getLatLng().longitude;
//                        select(posByTitle(Constants.ATTACHMENT_TYPE_LOCATION));
//                    }
                }
                break;
        }

    }

    private int posByTitle(String title) {
        for (GenericItem g : contentTypes) {
            if (title.equals(g.text))
                return contentTypes.indexOf(g);
        }
        return 0;
    }

    private void processImage(Uri o, File file, int requestCode) {

        if (contentData == null)
            return;
        if (file == null && o != null) {
            try {
                file = FileUtil.from(act, o);
            } catch (Exception e) {
                e.printStackTrace();
                utl.snack(act, "Upload files failed ! Please grant file permission");
            }

        }

        if (file == null) {
            utl.snack(act, "Upload files failed ! Please grant file permission");
            return;
        }

        int pos = 0;
        String path = file.getAbsolutePath();
        contentData.attachmentFileName=file.getName();
        switch (requestCode) {

            case REQ_UPLOAD_FILE:
                pos = posByTitle(Constants.ATTACHMENT_TYPE_FILE);
                contentData.attachmentType=Constants.ATTACHMENT_TYPE_FILE;
                contentData.filePath = path;
                break;
            case REQ_UPLOAD_DOC:
                pos = posByTitle(Constants.ATTACHMENT_TYPE_DOC);
                contentData.attachmentType=Constants.ATTACHMENT_TYPE_DOC;

                contentData.docPath = path;
                break;
            case REQ_UPLOAD_IMG_GAL:
            case REQ_UPLOAD_IMG_CAM:
                pos = posByTitle(Constants.ATTACHMENT_TYPE_IMAGE);
                contentData.attachmentType=Constants.ATTACHMENT_TYPE_IMAGE;
                contentData.imagePath = path;
                break;
        }

        long sizeMb = act.mFirebaseRemoteConfig.getLong("max_file_size") / (1024 * 1024);
        if (act.mFirebaseRemoteConfig.getLong("max_file_size") < file.length()) {

            utl.snack(act, "Max file size is " + sizeMb + " MB");
            return;
        }
        select(pos);

    }

    private void select(int pos) {

        contentTypes.stream().forEach((g) -> {
            g.status = 0;
        });
        contentTypes.get(pos).status = 2;
        contentDataGenriXAdapter.notifyDataSetChanged();

    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2020-08-16 18:19:37 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View root) {
        contImage = root.findViewById(R.id.cont_image);
        contentList = root.findViewById(R.id.content_list);
        contText = root.findViewById(R.id.cont_text);
        subText = root.findViewById(R.id.sub_text);
        done = root.findViewById(R.id.done);
        cancel = root.findViewById(R.id.cancel);

        searchLayoutWrapper = root.findViewById(R.id.search_layout_wrapper);
        searchDisease = root.findViewById(R.id.search_et);
        searchBox = root.findViewById(R.id.search_layout);
        searchBox.setVisibility(View.GONE);
        searchDisease.setEnabled(false);
        searchDisease.setTextColor(Color.BLACK);
        searchDisease.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(R.drawable.ic_notifications_black_24dp),null,null,null);
        searchDisease.setHint("Tag a related Message");


        utl.addPressReleaseAnimation(done);
        utl.addPressReleaseAnimation(cancel);
    }

    public static class ContentData {
        public String text;
        public String imagePath;
        public String filePath;
        public String docPath;
        public String locationName;
        public String location; //lat,lng

        public String attachmentType;
        public String attachmentFileName; //lat,lng

        public String getText() {
            if (text == null)
                text = "";
            return text;
        }

        public String getAttachmentPath(){
            if(!utl.isEmpty(imagePath))
                return imagePath;
            if(!utl.isEmpty(filePath))
                return filePath;
            if(!utl.isEmpty(docPath))
                return docPath;
            if(!utl.isEmpty(location))
                return location;
            return null;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getDocPath() {
            return docPath;
        }

        public void setDocPath(String docPath) {
            this.docPath = docPath;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }


    public void pickDoc() {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        act.startActivityForResult(Intent.createChooser(intent, "Choose Document"), REQ_UPLOAD_DOC);

    }

    public void pickFile() {

        String[] mimeTypes =
                {"file/*","*/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        act.startActivityForResult(Intent.createChooser(intent, "Choose File"), REQ_UPLOAD_FILE);

    }

    public void pickImage() {

        String[] opts = {"Take Photo", "Pick from Gallery"};
        AlertDialog.Builder b = new AlertDialog.Builder(act, R.style.AppCompatAlertDialogStyle);
        b.setItems(opts, (dialogInterface, i) -> {


            if (i == 0) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                act.startActivityForResult(cameraIntent, REQ_UPLOAD_IMG_CAM);


            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                act.startActivityForResult(pickPhoto, REQ_UPLOAD_IMG_GAL);//one can be replaced with any action code


            }


        });

        b.show();
    }

    public void pickLocation(){
        utl.toast(act,"Not Implemented : Places Picker");
//
//        String  PLACES_API_KEY =act.mFirebaseRemoteConfig.getString("places_api_key");
//        Places.initialize(act.getApplicationContext(), PLACES_API_KEY);
//        PlacesClient placesClient = Places.createClient(act);
//
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG, Place.Field.ADDRESS);
//        Intent intent = new Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.FULLSCREEN, fields)
//                .build(act);
//        act.startActivityForResult(intent, REQ_UPLOAD_LOCATION);

    }


}
