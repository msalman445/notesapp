package com.example.notes;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.jvm.internal.Lambda;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements Filterable {
    List<Note> notesList;
    List<Note> notesListAll;
    IOnNoteClickListener iOnNoteClickListener;
    IOnDeleteClickListener iOnDeleteClickListener;
    IONoteLongClickListener ioNoteLongClickListener;

    public NotesAdapter(List<Note> notesList){
        this.notesList = notesList;
        this.notesListAll = new ArrayList<>(this.notesList);
    }

    public void setIOnNoteClickListener(IOnNoteClickListener iOnNoteClickListener){
        this.iOnNoteClickListener = iOnNoteClickListener;
    }

    public void setIONoteLongClickListener(IONoteLongClickListener ioNoteLongClickListener) {
        this.ioNoteLongClickListener = ioNoteLongClickListener;
    }

    public void setIOnDeleteClickListener(IOnDeleteClickListener iOnDeleteClickListener) {
        this.iOnDeleteClickListener = iOnDeleteClickListener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, null);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.tvTitle.setText(note.getNoteTitle());
//        Date time = note.getTimeSpan();
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        holder.tvTime.setText(note.getTimeSpan());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private final Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredNoteList = new ArrayList<>();

            if (constraint.toString().isEmpty()){
                filteredNoteList.addAll(notesListAll);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note note : notesListAll) {
                    if (note.getNoteTitle().toString().toLowerCase().contains(filterPattern)){
                        filteredNoteList.add(note);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredNoteList;
            results.count = filteredNoteList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesList.clear();
            notesList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public class NotesViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvTime;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iOnNoteClickListener != null){
                        int position = getAdapterPosition();
                        iOnNoteClickListener.onNoteClick(NotesViewHolder.this, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (ioNoteLongClickListener != null){
                        int position = getAdapterPosition();
                        ioNoteLongClickListener.onNoteLongClick(NotesViewHolder.this, position);
                    }
                    return true;
                }
            });


        }


    }

    public interface IOnNoteClickListener{
        void onNoteClick(NotesViewHolder holder, int position);
    }

    public interface IONoteLongClickListener{
        void onNoteLongClick(NotesViewHolder holder, int position);
    }

    public interface IOnDeleteClickListener{
        void onNoteDeleteClick(NotesViewHolder holder, int position);
    }
}
