package elsaghier.example.com.movieappplus.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import elsaghier.example.com.movieappplus.Model.Film;


public class MyDBHelper extends SQLiteOpenHelper {

    //name & version
    private static final String DATABASE_NAME = "myMovies";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_MOVIE_TABLE = "Create Table " + FilmContract.FilmEntry.filmTableName + "(" + FilmContract.FilmEntry.id + " Text PRIMARY KEY" +
            ", " + FilmContract.FilmEntry.imgUrl + " Text ," + FilmContract.FilmEntry.overview + " Text ," +
            FilmContract.FilmEntry.original_title + " Text, " + FilmContract.FilmEntry.vote_average + " Text , " + FilmContract.FilmEntry.Generes + " Text , " +
            FilmContract.FilmEntry.release_date + " Text ," + FilmContract.FilmEntry.backdrop_path + " Text ," + FilmContract.FilmEntry.isSelected + " Text)";




    private static String db_name = "myMovies";
    private static int dateBase_Version = 1;
    public static String filmTableName = "Movies";
    public static String id = "id";
    private static String imgUrl = "poster_path";
    private static String overview = "overview";
    private static String original_title = "original_title";
    private static String vote_average = "vote_average";
    private static String release_date = "release_date";
    private static String backdrop_path = "backdrop_path";
    private static String isSelected = "isSelected";
    private static String Generes = "Generes";
    private SQLiteDatabase db;

        private String createTableFilm = "Create Table " + filmTableName + "(" + id + " Text PRIMARY KEY" +
            ", " + imgUrl + " Text ," + overview + " Text ," +
            original_title + " Text, " + vote_average + " Text , " + Generes + " Text , " +
            release_date + " Text ," + backdrop_path + " Text ," + isSelected + " Text)";


    public MyDBHelper(Context c) {
        super(c, db_name, null, dateBase_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableFilm);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db = this.getWritableDatabase();
        db.execSQL("Drop Table if exists " + filmTableName);
        onCreate(db);
        db.close();
    }

    public List<Film> getFavouriteFilms() {
        List<Film> data = new ArrayList<>();
        db = this.getWritableDatabase();
        Cursor c = db.query(MyDBHelper.filmTableName, new String[]{MyDBHelper.id,
                MyDBHelper.imgUrl,
                MyDBHelper.overview,
                MyDBHelper.original_title,
                MyDBHelper.vote_average,
                MyDBHelper.release_date,
                MyDBHelper.Generes,
                MyDBHelper.backdrop_path
                , MyDBHelper.isSelected}, null, null, null, null, null);
        if (c.moveToNext()) {
            do {
                Film filmeItem = new Film();
                filmeItem.setId(Integer.parseInt(c.getString(0)));
                filmeItem.setPosterPath(c.getString(1));
                filmeItem.setOverview(c.getString(2));
                filmeItem.setTitle(c.getString(3));
                filmeItem.setVoteAverage(Double.valueOf(c.getString(4)));
                filmeItem.setReleaseDate(c.getString(5));
                filmeItem.setGeneres(c.getString(6));
                filmeItem.setBackdropPath(c.getString(7));
                filmeItem.setSelected(c.getString(8));

                data.add(filmeItem);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    /*public void insertFilm(Film movieData) {
        if (movieData.getSelected().equals("1")) {
            db = this.getWritableDatabase();
            ContentValues con = new ContentValues();
            con.put(MyDBHelper.id, movieData.getId());
            con.put(MyDBHelper.imgUrl, movieData.getPosterPath());
            con.put(MyDBHelper.overview, movieData.getOverview());
            con.put(MyDBHelper.original_title, movieData.getTitle());
            con.put(MyDBHelper.vote_average, movieData.getVoteAverage());
            con.put(MyDBHelper.release_date, movieData.getReleaseDate());
            con.put(MyDBHelper.Generes, movieData.getGeneres());
            con.put(MyDBHelper.backdrop_path, movieData.getBackdropPath());
            con.put(MyDBHelper.isSelected, movieData.getSelected());
            db.replace(MyDBHelper.filmTableName, null, con);
            db.close();
        } else {
            db = this.getWritableDatabase();
            db.delete(MyDBHelper.filmTableName, MyDBHelper.id + "=?", new String[]{String.valueOf(movieData.getId())});
            db.close();
        }

    }

    public void deleteAll() {
        db = this.getWritableDatabase();
        db.execSQL("delete from " + filmTableName);
        db.close();
    }*/

}
