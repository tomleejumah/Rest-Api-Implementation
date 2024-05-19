package com.app.postmancollection.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.postmancollection.Model.ProjectTaskModels.ProjectTask;
import com.app.postmancollection.Inteface.OnItemClickListener;
import com.app.postmancollection.R;

import java.util.List;

public class TaskAdapter  extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final boolean isExpanded;
    private final List<ProjectTask> projectTasks;
    private final Context mContext;
    private OnItemClickListener listener;

    public TaskAdapter(boolean isExpanded, List<ProjectTask> projectTasks, Context mContext,OnItemClickListener listener) {
        this.isExpanded = isExpanded;
        this.projectTasks = projectTasks;
        this.mContext = mContext;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);

        return new TaskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectTask currentItem = projectTasks.get(position);
        holder.bind(currentItem);

        holder.title1.setText(currentItem.getName());

    }

    @Override
    public int getItemCount() {
//        return (isExpanded) ? mJobItems.size() : Math.min(mJobItems.size(),5);
        return projectTasks.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView title1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title1 = itemView.findViewById(R.id.title1);
        }
        public void bind(ProjectTask projectData) {
            itemView.setOnClickListener(v -> listener.onTaskItemClick(
                    projectData.getUuid(),
                    projectData.getProjectUuid(),
                    projectData.getName(),
                    projectData.getDeadline(),
                    projectData.getCreatedAt()
            ));
        }
    }
}
