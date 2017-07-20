package cn.com.shijizl.customerfiling.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.net.model.ProjectListResponse;
import cn.com.shijizl.customerfiling.utils.Utils;


public class LoadMoreAdapter extends RecyclerView.Adapter<LoadMoreAdapter.ViewHolder> {
    private ClickListener click;

    private List<ProjectListResponse.DataBean> list;
    private Context context;

    public LoadMoreAdapter(Context context, List<ProjectListResponse.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    public void updateData(List<ProjectListResponse.DataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateMoreData(List<ProjectListResponse.DataBean> list) {
        int length = this.list.size();
        this.list.addAll(list);
        notifyItemRangeChanged(length - 1, list.size() - length - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_load_more, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvTime.setText(Utils.paseTime(list.get(position).getCreateTime()));
        if (list.get(position).getStatus() == 0) {
            holder.tvSpeed.setText("未施工");
        } else if (list.get(position).getStatus() == 1) {
            holder.tvSpeed.setText("开始施工");
        } else {
            holder.tvSpeed.setText("完成");
        }

        holder.rlBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlBox;
        private TextView tvTitle;
        private TextView tvTime;
        private TextView tvSpeed;

        public ViewHolder(View itemView) {
            super(itemView);
            rlBox = (RelativeLayout) itemView.findViewById(R.id.rl_item_box);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_item);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time_item);
            tvSpeed = (TextView) itemView.findViewById(R.id.tv_speed_item);
        }
    }

    public interface ClickListener {
        void onClick(int position, View v);
    }

    public void setOnClickListener(ClickListener click) {
        this.click = click;
    }
}
