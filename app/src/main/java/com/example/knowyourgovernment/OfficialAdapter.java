package com.example.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private List<Officials> officialDataList;
    private MainActivity mainActivity;

    public OfficialAdapter(List<Officials> officialDataList, MainActivity mainActivity){
        this.officialDataList = officialDataList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enterdata, parent, false);
        view.setOnClickListener(mainActivity);
        return new OfficialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Officials o = officialDataList.get(position);
        holder.Designation.setText(o.getOffice());
        holder.name.setText(o.getName() + " (" + o.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return officialDataList.size();
    }
}
