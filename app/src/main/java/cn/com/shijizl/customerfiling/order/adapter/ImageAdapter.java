package cn.com.shijizl.customerfiling.order.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.com.shijizl.customerfiling.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ItemClickListener listener;
    private List<String> list;
    private Context context;

    public ImageAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_view_image, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, final int position) {
        Glide.with(context)
                .load(list.get(position))
                .override(300, 300)
                .fitCenter()
                .placeholder(R.drawable.place_holder)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item_recycle_view);
        }
    }
}
