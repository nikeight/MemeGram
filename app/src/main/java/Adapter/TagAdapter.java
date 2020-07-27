package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramfinal.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mTags;
    private List<String> mTagsCounts;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCounts) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCounts = mTagsCounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item,parent,false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tag;
        private TextView noOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag= itemView.findViewById(R.id.hash_tag);
            noOfPosts = itemView.findViewById(R.id.no_of_posts);

        }
    }

    public void filter (List<String> filterTags , List<String> filterTagsCount) {
        this.mTags = filterTags;
        this.mTagsCounts = filterTagsCount;

        notifyDataSetChanged();
    }
}
