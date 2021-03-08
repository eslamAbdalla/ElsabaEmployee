package com.elsabaautoservice.elsabaemployee;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeavesAdapter extends RecyclerView.Adapter<LeavesAdapter.ViewHolder> {

    JsonArray leavesListItems;
    private OnItemClickListener onItemClickListener ;
    Context context;

    public LeavesAdapter(JsonArray listItems, Context context,OnItemClickListener onItemClickListener) {
        this.leavesListItems = listItems;
        this.context = context;
        this.onItemClickListener = onItemClickListener ;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaves_details_rv, parent, false);
        return new ViewHolder(v,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LeavesAdapter.ViewHolder holder, int position) {

        Methods format = new Methods();

        JsonObject LeaveObj = leavesListItems.get(position).getAsJsonObject();

        String leaveName = LeaveObj.get("leaveTypeName").toString().replaceAll("\"", "");
        String leaveStatus = LeaveObj.get("leaveStatus").toString().replaceAll("\"", "");
        String leaveFromDate = LeaveObj.get("leaveDateFrom").toString().replaceAll("\"", "");
        String leaveToDate = LeaveObj.get("leaveDateTo").toString().replaceAll("\"", "");
        String leaveFromTime = LeaveObj.get("leaveTimeFrom").toString().replaceAll("\"", "");
        String leaveToTime = LeaveObj.get("leaveTimeTo").toString().replaceAll("\"", "");
        String leaveRequestDate = LeaveObj.get("leaveTimeStamp").toString().replaceAll("\"", "");
        String leaveManagerStatus = LeaveObj.get("managerStatus").toString().replaceAll("\"", "");
        String leaveHrStatus = LeaveObj.get("hrStatus").toString().replaceAll("\"", "");


        if (leaveFromTime.equals("null") && leaveToTime.equals("null")) {
            holder.LeaveFromTime.setText("------");
            holder.LeaveToTime.setText("------");
        } else {
            holder.LeaveFromTime.setText(format.timeFormat(leaveFromTime));
            holder.LeaveToTime.setText(format.timeFormat(leaveToTime));
        }

        holder.LeaveName.setText(leaveName);
        holder.LeaveStatus.setText(leaveStatus);
        holder.LeaveFromDate.setText(format.dateFormat(leaveFromDate, context.getString(R.string.date_format)));
        holder.LeaveToDate.setText(format.dateFormat(leaveToDate, context.getString(R.string.date_format)));
        holder.LeaveRequestDate.setText(format.dateFormat(leaveRequestDate, context.getString(R.string.date_format)));
        holder.LeaveManagerStatus.setText(leaveManagerStatus);
        holder.LeaveHrStatus.setText(leaveHrStatus);

        switch (leaveStatus) {
            case "Pending":
                holder.headLinear.setBackgroundColor(Color.parseColor("#D9E805"));
                break;
            case "Approved":
                holder.headLinear.setBackgroundColor(Color.parseColor("#08CD32"));
                break;
            case "Rejected":
                holder.headLinear.setBackgroundColor(Color.parseColor("#EF354A"));
                break;
        }


    }

    @Override
    public int getItemCount() {
        return leavesListItems.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemClickListener onItemClickListener ;
        TextView LeaveName;
        TextView LeaveStatus;
        TextView LeaveFromDate;
        TextView LeaveToDate;
        TextView LeaveFromTime;
        TextView LeaveToTime;
        TextView LeaveRequestDate;
        TextView LeaveManagerStatus;
        TextView LeaveHrStatus;

        LinearLayout headLinear;


        public ViewHolder(@NonNull View itemView , OnItemClickListener onItemClickListener) {
            super(itemView);

            LeaveName = (TextView) itemView.findViewById(R.id.home_LeaveName);
            LeaveStatus = (TextView) itemView.findViewById(R.id.home_LeaveStatus);
            LeaveFromDate = (TextView) itemView.findViewById(R.id.home_LeaveFromDate);
            LeaveToDate = (TextView) itemView.findViewById(R.id.home_LeaveToDate);
            LeaveFromTime = (TextView) itemView.findViewById(R.id.home_LeaveFromTime);
            LeaveToTime = (TextView) itemView.findViewById(R.id.home_LeaveToTime);
            LeaveRequestDate = (TextView) itemView.findViewById(R.id.home_LeaveRequestedDate);
            LeaveManagerStatus = (TextView) itemView.findViewById(R.id.home_ManagerStatus);
            LeaveHrStatus = (TextView) itemView.findViewById(R.id.home_HrStatus);

            headLinear = (LinearLayout) itemView.findViewById(R.id.home_headRelative);
            this.onItemClickListener = onItemClickListener ;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            onItemClickListener.onItemClick(getAdapterPosition());

        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }


}
