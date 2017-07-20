package cn.com.shijizl.customerfiling.order.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.net.model.ProjectScheduleResponse;

public class ProjectSpeedAdapter extends RecyclerView.Adapter<ProjectSpeedAdapter.ViewHolder> {

    private Context context;
    private List<ProjectScheduleResponse.DataBean> list;
    private RadioButtonClick click;

    public ProjectSpeedAdapter(Context context, List<ProjectScheduleResponse.DataBean> list) {
        this.list = list;
        this.context = context;
    }

    public void update(List<ProjectScheduleResponse.DataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project_speed, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(list.get(position).getStepDesc());
        holder.radioButton.setChecked(list.get(position).isChecked());
        holder.radioButton.setEnabled(list.get(position).isEnable());
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.rb_item_speed);
            textView = (TextView) itemView.findViewById(R.id.tv_content_speed);
        }
    }

    public interface RadioButtonClick {
        void onClick(View view, int position);
    }

    public void setOnRadioButtonClick(RadioButtonClick click) {
        this.click = click;
    }
}
