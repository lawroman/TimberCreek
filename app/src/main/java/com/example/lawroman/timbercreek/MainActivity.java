package com.example.lawroman.timbercreek;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int READ_BLOCK_SIZE = 100;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;
    private Button registerbtn;
    private TextView idlb;
    private TextView namelb;
    private EditText idtxt;
    private EditText nametxt;
    private ImageView qrvw;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();

        String registeredInfo = getStudentIdContent();

        if (registeredInfo != null && !registeredInfo.isEmpty()) {
            setWidgetsInvisible();
            String idInfo = registeredInfo + "#" + getTimeStamp();
            try {
                Bitmap bitmap = createQRCodeForString(idInfo);
                qrvw.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            String bannerString = "Registered to " + registeredInfo.substring(registeredInfo.indexOf("#") + 1);
            idlb.setText(bannerString);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onClick(View view) {

        Button button = (Button) view;

        switch (button.getId()) {
            case R.id.registerButton:
                // if both widgets are filled
                String id = idtxt.getText().toString().trim();
                String name = nametxt.getText().toString().trim().toUpperCase();

                if (id != null && name != null && id.length() > 0 && name.length() > 0) {
                    String info = id.trim() + "#" + name.trim();

                    // store info locally
                    writeToFile(info);
                    // store info to the cloud
                    // // TODO: 9/4/16

                    idlb.setText("Registered to " + name);

                    setWidgetsInvisible();

                    info += "#" + getTimeStamp();

                    // display barcode
                    try {
                        Bitmap bitmap = createQRCodeForString(info);
                        qrvw.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                } else {
                    // set alert
                    Toast.makeText(getApplicationContext(),
                            "You must first enter your ID and Name", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void init() {
        idlb = (TextView) findViewById(R.id.idText);
        namelb = (TextView) findViewById(R.id.nameText);
        idtxt = (EditText) findViewById(R.id.idEditText);
        nametxt = (EditText) findViewById(R.id.nameEditText);
        registerbtn = (Button) findViewById(R.id.registerButton);
        qrvw = (ImageView) findViewById(R.id.qrImageView);

        registerbtn.setOnClickListener(this);
    }

    public Bitmap createQRCodeForString(String text) throws WriterException {
        BitMatrix result;

        try {
            result = new MultiFormatWriter().encode(text,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);

        return bitmap;
    }

    public void writeToFile(String content) {
        // add-write text into file
        try {
            FileOutputStream fileout = openFileOutput("myid.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(content);
            outputWriter.close();

            //display file saved message
            /*Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getStudentIdContent() {
        String info = "";

        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("myid.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                info += readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public String getTimeStamp() {

        String timeStamp = new SimpleDateFormat("DDDHHmm").format(new Date());

        return timeStamp;
    }

    public void setWidgetsInvisible() {

        namelb.setVisibility(View.GONE);
        idtxt.setVisibility(View.GONE);
        nametxt.setVisibility(View.GONE);
        registerbtn.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.lawroman.timbercreek/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.lawroman.timbercreek/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
