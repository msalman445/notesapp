package com.example.notes;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


public class RecycleBinFragment extends Fragment implements Searchable{

    RecyclerView rvFavNotes;
    List<Note> noteList;
    NotesAdapter notesAdapter;
    DB db;
    String currentSearchQuery = "";

    public RecycleBinFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycle_bin, container, false);
        rvFavNotes = view.findViewById(R.id.rvRecycleNotes);

        db = DB.getInstance(getContext());
        noteList = db.viewAllNotes(Note.VIEW_DELETED);
        notesAdapter = new NotesAdapter(noteList);
        rvFavNotes.setAdapter(notesAdapter);
        rvFavNotes.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvFavNotes.setHasFixedSize(false);

        removeNotes();

        return view;
    }

    public void removeNotes(){
        notesAdapter.setIONoteLongClickListener(new NotesAdapter.IONoteLongClickListener() {
            @Override
            public void onNoteLongClick(NotesAdapter.NotesViewHolder holder, int position) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.fav_del_layout);
                Button btnRestore = bottomSheetDialog.findViewById(R.id.btnFavorite);
                Button btnDelete = bottomSheetDialog.findViewById(R.id.btnDelete);
                btnDelete.setText("Delete Permanently");
                btnRestore.setText("Restore Note");
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        long id = noteList.get(position).getId();
                        note.setId(id);
                        if (db.deleteNote(note)){
                            onResume();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
                btnRestore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        long id = noteList.get(position).getId();
                        note.setId(id);
                        note.setDeleted(false);

                        if (db.delNote(note)) {
                            onResume();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        noteList = db.viewAllNotes(Note.VIEW_DELETED);
        notesAdapter = new NotesAdapter(noteList);
        rvFavNotes.setAdapter(notesAdapter);
        removeNotes();
        super.onResume();
    }


    @Override
    public void performSearch(String query) {
        currentSearchQuery = query;
        notesAdapter.getFilter().filter(query);
    }
}