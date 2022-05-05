package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    TextView Designation;
    TextView name;

    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);
        Designation = itemView.findViewById(R.id.Des);
        name = itemView.findViewById(R.id.name);
    }
}
