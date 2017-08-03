package cn.com.shijizl.customerfiling.order.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.com.shijizl.customerfiling.R;
import cn.com.shijizl.customerfiling.net.model.ImageResponse;
import cn.com.shijizl.customerfiling.utils.GlideRoundTransform;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.ViewHolder> {
    private ItemClickListener listener;
    private List<ImageResponse> list;
    private Context context;

    public AddImageAdapter(Context context, List<ImageResponse> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<ImageResponse> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public AddImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_view_image, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddImageAdapter.ViewHolder holder, final int position) {
        String imgUrl = list.get(position).getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(context)
                    .load(list.get(position).getImgUrl())
                    .override(300, 300)
                    .fitCenter()
                    .transform(new GlideRoundTransform(context))
                    .placeholder(R.drawable.icon_loading)
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(v, position);
                }
            });
        } else {
            Glide.with(context)
                    .load(R.drawable.icon_camer)
                    .override(300, 300)
                    .transform(new GlideRoundTransform(context))
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddItemClickListener(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else if (list.size() > 12) {
            return 12;
        } else {
            return list.size();
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, int position);

        void onAddItemClickListener(View view, int position);
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
