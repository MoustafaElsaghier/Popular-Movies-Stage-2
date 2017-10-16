package elsaghier.example.com.movieappplus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import elsaghier.example.com.movieappplus.Adapters.FilmCursorAdapter;
import elsaghier.example.com.movieappplus.Adapters.HomeAdapter;
import elsaghier.example.com.movieappplus.ApiWork.ApiClient;
import elsaghier.example.com.movieappplus.ApiWork.MovieInterFace;
import elsaghier.example.com.movieappplus.DataBase.FilmContract;
import elsaghier.example.com.movieappplus.DataBase.MyDBHelper;
import elsaghier.example.com.movieappplus.Model.Film;
import elsaghier.example.com.movieappplus.Model.FilmsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView recyclerView;
    HomeAdapter homeAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    static List<Film> filmModels;
    MovieInterFace movieInterFace;

    MyDBHelper myDBHelper = null;
    static List<Film> favouriteFilms;
    Call<FilmsResponse> call;
    TextView ErrorMessages;

    FilmCursorAdapter filmCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        call = movieInterFace.getPopularMovies(getResources().getString(R.string.api_key));
        getDataFromNetwork(call);
        getFavFilm_db();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ErrorMessages.setVisibility(View.INVISIBLE);
        if (id == R.id.nav_popular) {
            getSupportActionBar().setTitle("Popular Movies");
            recyclerView.setAdapter(null);
            call = movieInterFace.getPopularMovies(getResources().getString(R.string.api_key));
            getDataFromNetwork(call);

        } else if (id == R.id.nav_top_rated) {
            getSupportActionBar().setTitle("Top Rated Movies");

            recyclerView.setAdapter(null);
            call = movieInterFace.getTopRatedMovies(getResources().getString(R.string.api_key));
            getDataFromNetwork(call);


        } else if (id == R.id.nav_now_playing) {
            getSupportActionBar().setTitle("Now Playing Movies");

            recyclerView.setAdapter(null);
            call = movieInterFace.getNowPlaying(getResources().getString(R.string.api_key));
            getDataFromNetwork(call);


        } else if (id == R.id.nav_latest) {
            getSupportActionBar().setTitle("Latest Movies");

            recyclerView.setAdapter(null);
            call = movieInterFace.getUpComingMovies(getResources().getString(R.string.api_key));
            getDataFromNetwork(call);


        } else if (id == R.id.nav_film_favourite) {
            getSupportActionBar().setTitle("Favourite Movies");
            refreshFav();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshFav();
    }

    private void refreshFav() {
        getFavFilm_db();
        if (favouriteFilms.size() == 0) {
            findViewById(R.id.ErrorMessage).setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
        } else {
            recyclerView.setAdapter(null);
            homeAdapter = new HomeAdapter(favouriteFilms, this);
            recyclerView.setAdapter(homeAdapter);
        }
    }

    void init() {

        // Navigation Drawer Initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // variables Intialization
        filmModels = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.posters_recycler);
        //Change 2 to your choice because here 2 is the number of Grid layout Columns in each row.
        recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        movieInterFace = ApiClient.getClient().create(MovieInterFace.class);

        myDBHelper = new MyDBHelper(getBaseContext());
        favouriteFilms = new ArrayList<>();
        ErrorMessages = (TextView) findViewById(R.id.ErrorMessage);
    }

    public void getDataFromNetwork(Call<FilmsResponse> call) {
        call.enqueue(new Callback<FilmsResponse>() {
            @Override
            public void onResponse(@NonNull Call<FilmsResponse> call, @NonNull Response<FilmsResponse> response) {
                System.out.println("done1" + response);
                filmModels = response.body().getFilms();
                refresh();
                homeAdapter = new HomeAdapter(filmModels, HomeActivity.this);
                recyclerView.setAdapter(homeAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<FilmsResponse> call, @NonNull Throwable t) {
                System.out.println("failed " + t.getMessage());
            }
        });
    }

    public void refresh() {
        getFavFilm_db();
        for (int i = 0; i < favouriteFilms.size(); ++i) {
            for (int j = 0; j < filmModels.size(); ++j) {
                if (favouriteFilms.get(i).getId() == filmModels.get(j).getId())
                    filmModels.get(j).setSelected(favouriteFilms.get(i).getSelected());
            }
        }
    }

    public void getFavFilm_db() {
        favouriteFilms.clear();
        favouriteFilms = myDBHelper.getFavouriteFilms();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                FilmContract.FilmEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        filmCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        filmCursorAdapter.swapCursor(null);

    }
}
