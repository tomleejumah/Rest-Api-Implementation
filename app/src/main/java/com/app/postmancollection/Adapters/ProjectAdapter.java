package com.app.postmancollection.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.postmancollection.MainActivity;
import com.app.postmancollection.Model.ProjectModels.ProjectData;
import com.app.postmancollection.Inteface.OnItemClickListener;
import com.app.postmancollection.R;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
private static final String TAG = "ProjectAdapter";
    private List<ProjectData> projectDataList;
    private final Context mContext;
    private int layoutResId;
    private OnItemClickListener listener;

    public ProjectAdapter(List<ProjectData> projectDataList, Context mContext, int layoutResId, OnItemClickListener listener) {
        this.projectDataList = projectDataList;
        this.mContext = mContext;
        this.layoutResId = layoutResId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(layoutResId,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectData currentItem = projectDataList.get(position);
        holder.bind(currentItem);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Show Dialog
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Do you want to archive post");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", (dialogInterface, i) -> alertDialog.dismiss());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", (dialogInterface, i) -> {
                    //archive post
                    String uid = String.valueOf(projectDataList.get(position).getUuid());
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).archiveProject(mContext,uid);
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return projectDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private  TextView txtJobTittle;
        private  TextView txtShortDes,description,title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (layoutResId == R.layout.projects_layouts) {
            txtJobTittle=itemView.findViewById(R.id.txtTittle);
            txtShortDes=itemView.findViewById(R.id.txtBody);
            }else if (layoutResId == R.layout.archived_project_item){
                description=itemView.findViewById(R.id.txtShortDes);
                title=itemView.findViewById(R.id.txtJobTittle);
            }
        }
        public void bind(ProjectData projectData) {
            // Bind data to views
            if (layoutResId == R.layout.projects_layouts) {
                txtJobTittle.setText(projectData.getName());
                txtShortDes.setText(projectData.getDescription());
            } else if (layoutResId == R.layout.archived_project_item) {
                title.setText(projectData.getName());
                description.setText(projectData.getDescription());
            }
            itemView.setOnClickListener(v -> listener.onProjectItemClick(
                    projectData.getUuid(),
                    projectData.getUserUuid(),
                    projectData.getName(),
                    projectData.getDescription(),
                    projectData.getCreatedAt()
            ));
        }
    }


}
