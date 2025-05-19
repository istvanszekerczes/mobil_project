package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> implements Filterable {
    private ArrayList<Tournament> mTournamentsData;
    private ArrayList<Tournament> mTournamentsDataAll;
    private Context context;
    private int lastPosition = -1;
    private OnItemClickListener mListener;
    private Filter tournamentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Tournament> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.count = mTournamentsDataAll.size();
                results.values = mTournamentsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Tournament tournament : mTournamentsDataAll) {
                    if (tournament.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(tournament);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mTournamentsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    // Interface a gomb kattintásának kezelésére
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public TournamentAdapter(Context context, ArrayList<Tournament> tournamentData) {
        this.mTournamentsData = tournamentData;
        this.mTournamentsDataAll = tournamentData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_tournament, parent, false);
        return new ViewHolder(itemView, mListener); // Átadja a listener-t a ViewHolder-nak
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentAdapter.ViewHolder holder, int position) {
        Tournament currentTournament = mTournamentsData.get(position);
        holder.bindTo(currentTournament);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_down);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mTournamentsData.size();
    }

    @Override
    public Filter getFilter() {
        return tournamentFilter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView location;
        private TextView startDate;
        private TextView endDate;
        private TextView description;
        private Button detailsButton;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.location = itemView.findViewById(R.id.location);
            this.startDate = itemView.findViewById(R.id.startDate);
            this.endDate = itemView.findViewById(R.id.endDate);
            this.description = itemView.findViewById(R.id.description);
            this.detailsButton = itemView.findViewById(R.id.detailsButton);

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bindTo(Tournament currentTournament) {
            name.setText(currentTournament.getName());
            location.setText(currentTournament.getLocation());
            startDate.setText(currentTournament.getStartDate().toString());
            endDate.setText(currentTournament.getEndDate().toString());
            description.setText(currentTournament.getDescription().toString());
        }
    }
}