package com.abhi.galleryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abhi.galleryapp.common.MyApplication;
import com.abhi.galleryapp.httpimage.HttpImageManager;
import com.abhi.galleryapp.listener.PaginationAdapterCallback;
import com.abhi.galleryapp.models.photo;
import com.abhi.galleryapp.R;

import java.util.ArrayList;
import java.util.List;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int HERO = 2;

    private List<photo> galleryPhotos;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public PaginationAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        galleryPhotos = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_list, parent, false);
                viewHolder = new photoVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        photo photo = galleryPhotos.get(position); // Photo

        switch (getItemViewType(position)) {
            case ITEM:
                final photoVH photoVH = (photoVH) holder;
                photoVH.mProgress.setVisibility(View.VISIBLE);
                String url=loadUrl(photo);
                HttpImageManager.LoadRequest loadRequest = new HttpImageManager.LoadRequest(Uri.parse(url), photoVH.mPosterImg, new HttpImageManager.OnLoadResponseListener() {
                    @Override
                    public void onLoadResponse(HttpImageManager.LoadRequest r, Bitmap data) {
                        photoVH.mProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadProgress(HttpImageManager.LoadRequest r, long totalContentSize, long loadedContentSize) {
                        photoVH.mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadError(HttpImageManager.LoadRequest r, Throwable e) {
                        photoVH.mProgress.setVisibility(View.GONE);
                    }
                },url);

                Bitmap bitmap = MyApplication.getMyApplication().getHttpImageManager().loadImage(loadRequest);
                if(bitmap!=null)
                    photoVH.mPosterImg.setImageBitmap(bitmap);
//                else
//                    photoVH.mPosterImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.image_not_found));

                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return galleryPhotos == null ? 0 : galleryPhotos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == galleryPhotos.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private String loadUrl(@NonNull photo photoDo) {
        return "http://farm"+photoDo.getFarm()+".static.flickr.com/"+photoDo.getServer()+"/"+photoDo.getId()+"_"+photoDo.getSecret()+".jpg";
    }

    public void add(photo r) {
        galleryPhotos.add(r);
        notifyItemInserted(galleryPhotos.size() - 1);
    }

    public void addAll(List<photo> movePhotos) {
        for (photo photo : movePhotos) {
            add(photo);
        }
    }

    public void remove(photo r) {
        int position = galleryPhotos.indexOf(r);
        if (position > -1) {
            galleryPhotos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new photo());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = galleryPhotos.size() - 1;
        photo photo = getItem(position);

        if (photo != null) {
            galleryPhotos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public photo getItem(int position) {
        return galleryPhotos.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(galleryPhotos.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    /**
     * Main list's content ViewHolder
     */
    protected class photoVH extends RecyclerView.ViewHolder {
        private ImageView mPosterImg;
        private ProgressBar mProgress;

        public photoVH(View itemView) {
            super(itemView);

            mPosterImg = itemView.findViewById(R.id.movie_poster);
            mProgress = itemView.findViewById(R.id.movie_progress);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

}

