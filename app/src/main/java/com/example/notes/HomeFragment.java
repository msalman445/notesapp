package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements Searchable{
    FloatingActionButton btnNewNote;
    RecyclerView rvNotes;
    Note note;
    DB db;
    NotesAdapter notesAdapter;
    ImageButton btnDelete;
    List<Note> noteList, filteredNoteList;
    String currentSearchQuery = "";

    public HomeFragment() {
        // Required empty public constructor
    }



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        btnNewNote = view.findViewById(R.id.btnNewNote);
        rvNotes = view.findViewById(R.id.rvNotes);
        btnDelete = view.findViewById(R.id.btnDelete);

        btnNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotesActivity.class);
                startActivity(intent);

            }
        });

        note = new Note();
        db = DB.getInstance(getContext());
        noteList = db.viewAllNotes(Note.VIEW_ALL);
        filteredNoteList = new ArrayList<>(noteList);
        notesAdapter = new NotesAdapter(filteredNoteList);


        rvNotes.setAdapter(notesAdapter);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvNotes.setHasFixedSize(false);

        onNoteClick();
        onNoteDeleteClick();

        return view;

    }

    public void onNoteClick(){
        notesAdapter.setIOnNoteClickListener(new NotesAdapter.IOnNoteClickListener() {
            @Override
            public void onNoteClick(NotesAdapter.NotesViewHolder holder, int position) {
                long id = noteList.get(position).getId();
                note.setId(id);
                Intent intent = new Intent(getContext(), UpdateActivity.class);
                intent.putExtra("ID", note.getId());
                startActivity(intent);
            }
        });
    }

    public void onNoteDeleteClick(){
        notesAdapter.setIONoteLongClickListener(new NotesAdapter.IONoteLongClickListener() {
            @Override
            public void onNoteLongClick(NotesAdapter.NotesViewHolder holder, int position) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.fav_del_layout);
                Button btnFavorite = bottomSheetDialog.findViewById(R.id.btnFavorite);
                Button btnDelete = bottomSheetDialog.findViewById(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        long id = noteList.get(position).getId();
                        note.setId(id);
                        note.setDeleted(true);

                        if (db.delNote(note)) {
                            onResume();
                            Toast.makeText(getContext(), "Moved to Recycle Bin", Toast.LENGTH_SHORT).show();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });

                btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        long id = noteList.get(position).getId();
                        note.setId(id);
                        note.setFavorite(true);
                        if (db.favNote(note)){
                            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
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
        super.onResume();
        noteList = db.viewAllNotes(Note.VIEW_ALL);
        filteredNoteList = new ArrayList<>(noteList);
        notesAdapter = new NotesAdapter(filteredNoteList);
        rvNotes.setAdapter(notesAdapter);
        rvNotes.invalidate();

        if (!currentSearchQuery.isEmpty()){
            notesAdapter.getFilter().filter(currentSearchQuery);
        }


        onNoteClick();
        onNoteDeleteClick();
    }



    @Override
    public void performSearch(String query) {
        currentSearchQuery = query;
        notesAdapter.getFilter().filter(query);
    }
}