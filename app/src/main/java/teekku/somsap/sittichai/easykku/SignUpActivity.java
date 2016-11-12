package teekku.somsap.sittichai.easykku;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import org.jibble.simpleftp.SimpleFTP;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    // explict
    private EditText nameEditText, phoneEditText, userEditText, passwordEditText;
    private ImageView imageView;
    private Button button;
    private String nameString, phoneString, userString, passwordString,
            imagePathString,imageNameString;
    private Uri uri;
    private boolean aBoolean = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // bind widget
        nameEditText = (EditText) findViewById(R.id.editText);
        phoneEditText = (EditText) findViewById(R.id.editText2);
        userEditText = (EditText) findViewById(R.id.editText3);
        passwordEditText = (EditText) findViewById(R.id.editText4);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button3);

        // SignUp  controller
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameString = nameEditText.getText().toString().trim();
                phoneString = phoneEditText.getText().toString().trim();
                userString = userEditText.getText().toString().trim();
                passwordString = passwordEditText.getText().toString().trim();


                // check space
                if (nameString.equals("") || phoneString.equals("") ||
                        userString.equals("") || passwordString.equals("")) {
                    //have space
                    Log.d("12novV1", "Have space");
                    MyAlert myAlert = new MyAlert(SignUpActivity.this, R.drawable.bird48, "Space",
                            "please full fill");
                    myAlert.myDialog();


                } else if (aBoolean) {
                    // non choose image
                    MyAlert myAlert = new MyAlert(SignUpActivity.this, R.drawable.bird48,"ยังไม่เลิอกรูป","เลือกรูปด้วย");
                    myAlert.myDialog();;


                } else {
                    // chose image
                    uploadImageToServer();


                }


            } // on click
        });
// image  controller

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกโปรแกรมดูภาพ"), 0);


            } // on click
        });

    }   // main method

    private void uploadImageToServer() {
            // change Policy
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try{
            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcodingthai.com",21,"kku@swiftcodingthai.com","Abc12345");
            simpleFTP.bin();
            simpleFTP.cwd("Image");
            simpleFTP.stor(new File(imagePathString));
            simpleFTP.disconnect();


        }catch (Exception e){
            Log.d("12novV1","e simpleFTP ==>" + e.toString());

        }


    } // upload

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( (requestCode == 0) && (resultCode == RESULT_OK) ) {
            Log.d("12novV1", "Result OK!!");
            aBoolean = false;

            // show image
            uri = data.getData();
            try{
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imageView.setImageBitmap(bitmap);



            }catch (Exception e){
                e.printStackTrace();
            }

            // find path of image
            imagePathString = myFindPath(uri);
            Log.d("12novV1","imagePath ==>" + imagePathString);
            // find name of image
            imageNameString = imagePathString.substring(imagePathString.lastIndexOf("/"));
            Log.d("12novV1", "imageName ==>"+imageNameString);


        } // if

    } // onActivity

    private String myFindPath(Uri uri) {
        String result = null;

        String[] string = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,string, null,null,null);
        if (cursor != null){

            cursor.moveToFirst();
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            result =  cursor.getString(index);

        } else {
            result = uri.getPath();

        }

        return result;
    }


}   // main class
