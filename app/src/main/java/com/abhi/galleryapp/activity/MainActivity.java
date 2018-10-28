package com.abhi.galleryapp.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.galleryapp.R;
import com.abhi.galleryapp.adapter.PaginationAdapter;
import com.abhi.galleryapp.api.PhotoApi;
import com.abhi.galleryapp.api.PhotoService;
import com.abhi.galleryapp.listener.PaginationAdapterCallback;
import com.abhi.galleryapp.listener.PaginationScrollListener;
import com.abhi.galleryapp.models.PhotoDO;
import com.abhi.galleryapp.models.photo;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {

    private static final String TAG = "MainActivity";

    PaginationAdapter adapter;
    GridLayoutManager gridLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError, tv_welcome;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1000;
    private int currentPage = PAGE_START;

    private PhotoService photoService;
    private String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.main_recycler);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        tv_welcome = findViewById(R.id.tv_welcome);

        adapter = new PaginationAdapter(this);

        gridLayoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage(searchQuery, false);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        photoService = PhotoApi.getClient().create(PhotoService.class);
        showInitialMessage();
        loadNextPage("", false);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNextPage(searchQuery, false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if(!TextUtils.isEmpty(query))
                    searchQuery = query;
                    adapter.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    hideInitialMessage();
                    if(isNetworkConnected())
                        loadNextPage(searchQuery,true);
                    else {
                        showErrorView(new Exception());
                        progressBar.setVisibility(View.GONE);
                    }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param response extracts List<{@link photo >} from response
     * @return
     */
    private List<photo> fetchResults(Response<PhotoDO> response) {
        PhotoDO photosDo = response.body();
        return photosDo.getPhotos().getPhoto();
    }

    private void loadNextPage(String query, final boolean isSearch) {
        Log.d(TAG, "loadNextPage: " + currentPage);
        fetchAllPhotos(currentPage, query).enqueue(new Callback<PhotoDO>() {
            @Override
            public void onResponse(Call<PhotoDO> call, Response<PhotoDO> response) {
                if (response.body().getStat().equalsIgnoreCase("ok")) {
                    isLoading = false;
                    List<photo> photos = fetchResults(response);
                    if (photos != null && photos.size() > 0) {
                        if (isSearch) {
                            hideInitialMessage();
                            adapter.clear();
                            adapter.addAll(photos);
                            progressBar.setVisibility(View.GONE);
                        } else
                            adapter.addAll(photos);
                        TOTAL_PAGES = Integer.parseInt(response.body().getPhotos().getTotal());
                        adapter.removeLoadingFooter();
                        rv.setVisibility(View.VISIBLE);
                    }
                    else {
                        if(adapter != null)
                            adapter.clear();
                        showErrorView(new RuntimeException("No Result Found!"));
                        rv.setVisibility(View.GONE);
                    }
                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;


                }
                else {
                    showErrorView(new Exception("Cannot Fetch Data! Please try again."));
                    rv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PhotoDO> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Performs a Retrofit call to the Photos API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<PhotoDO> fetchAllPhotos(int page, String query) {
        return photoService.getAllPhotosFromService(
                getString(R.string.method),
                getString(R.string.my_api_key),
                getString(R.string.format),"1",
                query,
                "1",
                page
        );
    }

    @Override
    public void retryPageLoad() {

        loadNextPage(searchQuery, false);
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tv_welcome.setVisibility(View.GONE);
            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }else if (throwable instanceof RuntimeException) {
            errorMsg = getResources().getString(R.string.no_result);
        }

        return errorMsg;
    }
    // Helpers -------------------------------------------------------------------------------------
    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showInitialMessage() {
       rv.setVisibility(View.GONE);
       errorLayout.setVisibility(View.VISIBLE);
       btnRetry.setVisibility(View.GONE);
       tv_welcome.setVisibility(View.VISIBLE);
       tv_welcome.setText("Welcome!");
       txtError.setText("Please search for photos.");
    }

    private void hideInitialMessage() {
        rv.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }
    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}