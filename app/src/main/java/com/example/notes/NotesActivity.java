package com.example.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {
    Toolbar toolbar;
    BottomAppBar bottomAppBar;
    private TextView tvDateTime;
    private EditText etNotesTitle, etNotesDescription;
    private Boolean isInserted = false;
    private ImageView ivImage;
    public static final int REQUEST_IMAGE_GET= 1;
    String imagePath;
    private Boolean isFavorite = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        toolbar = findViewById(R.id.topAppBar);
        tvDateTime = findViewById(R.id.tvDateTime);
        etNotesTitle = findViewById(R.id.etNotesTitle);
        etNotesDescription = findViewById(R.id.etNotesDescription);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        ivImage = findViewById(R.id.ivImage);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault());
        String currentDateAndTime = sdf.format(currentTime);
        tvDateTime.setText(currentDateAndTime);

        DB db = DB.getInstance(this);
        Note note = new Note();

//        For Update Notes
        Note selectedNote = Note.getNoteForId(getIntent().getLongExtra("ID", 0), db);

        if (selectedNote != null) {
            etNotesTitle.setText(selectedNote.getNoteTitle());
            etNotesDescription.setText(Html.fromHtml(selectedNote.getNoteDescription()));
            tvDateTime.setText(selectedNote.getTimeSpan());
            Bitmap bitmap = FileUtils.loadImageFromInternalStorage(selectedNote.getImagePath());
            ivImage.setImageBitmap(bitmap);
        }


        toolbar.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                    String html = Html.toHtml(etNotesDescription.getText());
                    note.setNoteDescription(html);

                    note.setNoteTitle(etNotesTitle.getText().toString());
                    note.setTimeSpan(tvDateTime.getText().toString());
                    note.setImagePath(imagePath);

                    int id = menuItem.getItemId();

                if (selectedNote == null) {
                    if (id == R.id.itmSave) {
                        if (!isInserted) {
                            if (db.insertNote(note)) {
                                note.setId(db.rowId);
                            }
                            isInserted = true;
                            Toast.makeText(NotesActivity.this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            if (db.updateNote(note)) {
                                Toast.makeText(NotesActivity.this, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }else{
                    if (id == R.id.itmSave) {
                        note.setId(selectedNote.getId());
                        db.updateNote(note);
                    }
                }



                return true;
            }
        });

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Spannable spannableString = new SpannableStringBuilder(etNotesDescription.getText());

                int id = item.getItemId();
                if (id == R.id.itmBold){
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), etNotesDescription.getSelectionStart(), etNotesDescription.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                } else if (id == R.id.itmItalic) {
                    spannableString.setSpan(new StyleSpan(Typeface.ITALIC), etNotesDescription.getSelectionStart(), etNotesDescription.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                } else if (id == R.id.itmNoFormat) {
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, spannableString.length(), 0);
                    StyleSpan[] spans = spannableString.getSpans(0, spannableString.length(), StyleSpan.class);
                    for (StyleSpan styleSpan: spans) spannableString.removeSpan(styleSpan);
                } else if (id == R.id.itmImage) {
                    showImageDialog();
                }
                etNotesDescription.setText(spannableString);
                return false;
            }
        });





    }

    ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
        @Override
        public void onActivityResult(Bitmap o) {
            ivImage.setImageBitmap(o);
        }
    });



    public void showImageDialog(){
        BottomSheetDialog dialog = new BottomSheetDialog(NotesActivity.this);
        dialog.setContentView(R.layout.image_picker_layout);
        LinearLayout galleryLayout = dialog.findViewById(R.id.galleryLayout);
        LinearLayout cameraLayout = dialog.findViewById(R.id.cameraLayout);


        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
                dialog.dismiss();
            }
        });
        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLauncher.launch(null);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_IMAGE_GET == requestCode && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                String imageName = "image_"+System.currentTimeMillis();
                imagePath = FileUtils.saveImageToInternalStorage(this, bitmap, imageName);
            }catch (Exception e){
                e.printStackTrace();
            }

            ivImage.setImageURI(data.getData());
        }
    }

}