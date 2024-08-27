package com.example.notes;

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
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


public class FavoriteFragment extends Fragment implements Searchable{

    RecyclerView rvFavNotes;
    List<Note> noteList;
    NotesAdapter notesAdapter;
    DB db;
    String currentSearchQuery = "";

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favorite, container, false);
        rvFavNotes = view.findViewById(R.id.rvFavNotes);

        db = DB.getInstance(getContext());
        noteList = db.viewAllNotes(Note.VIEW_FAVORITES);
        notesAdapter = new NotesAdapter(noteList);
        rvFavNotes.setAdapter(notesAdapter);
        rvFavNotes.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvFavNotes.setHasFixedSize(false);

        removeNotes();
        notesClick();


        return view;
    }

    public void removeNotes(){
        notesAdapter.setIONoteLongClickListener(new NotesAdapter.IONoteLongClickListener() {
            @Override
            public void onNoteLongClick(NotesAdapter.NotesViewHolder holder, int position) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.fav_layout);
                Button btnRemFav = bottomSheetDialog.findViewById(R.id.btnRemFav);
                btnRemFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        long id = noteList.get(position).getId();
                        note.setId(id);
                        note.setFavorite(false);
                        if (db.favNote(note)){
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
        noteList = db.viewAllNotes(Note.VIEW_FAVORITES);
        notesAdapter = new NotesAdapter(noteList);
        rvFavNotes.setAdapter(notesAdapter);
        removeNotes();
        notesClick();
        super.onResume();
    }


    @Override
    public void performSearch(String query) {
        currentSearchQuery = query;
        notesAdapter.getFilter().filter(query);
    }

    public void notesClick(){
        notesAdapter.setIOnNoteClickListener(new NotesAdapter.IOnNoteClickListener() {
            @Override
            public void onNoteClick(NotesAdapter.NotesViewHolder holder, int position) {
                long id = noteList.get(position).getId();

                Intent intent = new Intent(getContext(), UpdateActivity.class);
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });
    }
}