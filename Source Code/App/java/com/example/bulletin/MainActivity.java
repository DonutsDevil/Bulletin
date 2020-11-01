package com.example.bulletin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
// heaven only knows where you been i dont need to know
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    // this is the URL REQUEST THAT IS PASSED EVERY EVER in the app
    private static  String URL_REQUEST ;
    // unique API KEY
    private static final String API_KEY="api-key=c0b98da5-a986-430d-ad09-a35d4d1c83d5";
    // stores form date of Edit text from date
    private static String from_date;
    // stores from date to_date
    private static String to_date;

    private static String base_url="https://content.guardianapis.com/search?";
    // empty view so that if no data is display we will display this empty image view
    private ImageView emptyViewForListView;
    // reference to the List view in mainActivity
    private ListView newsListView;
    // interminate progress bar when we first enter the app.
    private ProgressBar progressBar;
    // loads when we search a app a bar will show on the top.
    private ProgressBar horizontalProgressBar;
    // check internet connectivity
    private ConnectivityManager connMgr;
    private NetworkInfo networkInfo;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // reference to the From date edit text
    private EditText etToDate;
    // reference to the To daye edit text
    private NewsAdapter newsAdapter;
    // Loader manger to manage the loadbacks
    private LoaderManager loaderManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // to ge the current date of the System like 2020-11-01
        LocalDate d1 = LocalDate.now();
        // this goes 1 day back and gives us the news of that particular day ie 2020-10-31.
        String datenow = d1.minusDays(1)+"";
        // prepare a url  when we enter first time in the app.
        StringBuilder loadINurl = new StringBuilder();
        loadINurl.append(base_url).append("from-date="+datenow+"&").append("page-size=100&q=latest&").append(API_KEY);

        URL_REQUEST = loadINurl.toString();
        // reference to the progress bar and its visible to identify that we are getting your data
        progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);

        // reference to the progress bar and its visible to identify that we are getting your data
        horizontalProgressBar = findViewById(R.id.Horizontal_ProgressBar);
        emptyViewForListView = findViewById(R.id.empty_view);

        // Reference to the {@ ListView} in activity_main
        newsListView = findViewById(R.id.news_listView);
        newsListView.setEmptyView(emptyViewForListView);

        // Custom adapter to generates views for are ListView
        newsAdapter = new NewsAdapter(MainActivity.this,new ArrayList<News>());
        // sets listView with the Content of the ArrayList i.e NEWS info.
        newsListView.setAdapter(newsAdapter);

        // returns the current time zone and locale of the system.
        Calendar calendar = Calendar.getInstance();
        // YEAR , MONTH, DAY is used by DatePickerDialog as Arguments
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DAY = calendar.get(Calendar.DAY_OF_MONTH);

        // Reference to From date so that When ever edit text is clicked Calender view pops up
        final EditText etFromDate = findViewById(R.id.from_date);
        etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * DatePickerDialog takes 5 Arguments
                 * @PARAM Context,
                 * @PARAM DatePickerDialog.OnDateSetListener,
                 * @PARAM calendar YEAR,
                 * @PARAM calendar MONTH,
                 * @PARAM calendar DAY
                 * */
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // +1 cause month return from onDateSet is from 0-11
                        month = month+1;
                        String days = "";
                        if(dayOfMonth < 10){
                            days = "0"+dayOfMonth;
                            String date = year+"-"+month+"-"+days;
                            etFromDate.setText(date);
                        }else {
                            String date = year + "-" + month + "-" + dayOfMonth;
                            etFromDate.setText(date);
                        }
                    }
                },YEAR,MONTH,DAY);
                datePickerDialog.show();

                // to check for dates when changed and remove error if no errors
                etFromDate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(etFromDate.getText().toString().compareTo(java.time.LocalDate.now().toString()) < 0){
                            etFromDate.setError(null);
                        }
                    }
                });
            }
        });
        // Reference to From date so that When ever edit text is clicked Calender view pops up
        etToDate = findViewById(R.id.to_date);
        etToDate.setOnClickListener(new View.OnClickListener() {
            /**
             * DatePickerDialog takes 5 Arguments
             * @PARAM Context,
             * @PARAM DatePickerDialog.OnDateSetListener,
             * @PARAM calendar YEAR,
             * @PARAM calendar MONTH,
             * @PARAM calendar DAY
             * */
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // +1 cause month return from onDateSet is from 0-11
                        month = month+1;
                        String days = "";
                        if(dayOfMonth < 10){
                            days = "0"+dayOfMonth;
                            String date = year+"-"+month+"-"+days;
                            etToDate.setText(date);
                        }else {
                            String date = year + "-" + month + "-" + dayOfMonth;
                            etToDate.setText(date);
                        }
                    }
                },YEAR,MONTH,DAY);
                datePickerDialog.show();

                etToDate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        // date1.compareTo(date2) < 0 means d1 is after d2 and if d1.comparTo(d2) == 0 means same date so no error msg
                        if(etToDate.getText().toString().compareTo(java.time.LocalDate.now().toString()) < 0 ||
                                etToDate.getText().toString().compareTo(java.time.LocalDate.now().toString()) == 0 ){
                            etToDate.setError(null);
                        }
                        // if current date is same and check box of current date is  unchecked then check the checkbox of current date
                        if(etToDate.getText().toString().compareTo(java.time.LocalDate.now().toString()) != 0){
                            CheckBox checkCurrentDate = findViewById(R.id.current_date);
                            if(checkCurrentDate.isChecked()){
                                checkCurrentDate.setChecked(false);
                            }
                        }
                        // if current date is not same and check box of current date is  checked then uncheck the checkbox of current date
                        if(etToDate.getText().toString().compareTo(java.time.LocalDate.now().toString()) == 0){
                            CheckBox checkCurrentDate = findViewById(R.id.current_date);
                            if(!checkCurrentDate.isChecked()){
                                checkCurrentDate.setChecked(true);
                            }
                        }

                        // if to date is ahead of the current date.
                        if (etToDate.getText().toString().compareTo(java.time.LocalDate.now().toString()) > 0){
                            etToDate.setError("Date Invalid");
                        }

                    }
                });
            }
        });


        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = newsAdapter.getItem(position);
                Uri newsUri = Uri.parse(news.getNewsUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW,newsUri);
                startActivity(webIntent);

            }
        });

        if(isConnectedToInternet()) {
            // LoaderManager checks if a loader of particular id is present then the same loader is used or else changed
            loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(1, null, this);
        }else{
            progressBar.setVisibility(View.GONE);
            horizontalProgressBar.setVisibility(View.GONE);
            emptyViewForListView.setImageResource(R.drawable.nointernet);
        }

        // reference to the search button
        Button btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

    }

    // if no loader is present it will create a loader but here it will always create one cause we using restartLoader not initLoader as call
    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG,"OnCreateLoader.. ");
        return new NewsLoader(this,URL_REQUEST);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {
        // checks for network connectivity
        if(isConnectedToInternet()){
            emptyViewForListView.setImageResource(R.drawable.nointernet);
        }
        progressBar.setVisibility(View.GONE);
        horizontalProgressBar.setVisibility(View.GONE);
        // when ever ArrayList of news is Empty.
        emptyViewForListView.setImageResource(R.drawable.nodatafound);
        // Clear the adapter of previous earthquake data
        newsAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if(news!=null && !news.isEmpty()){
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    // set the To Date EditText view with the Current System date
    public void currentDate(View view){
        // Reference to the Edit Text view which is To date:
        CheckBox currentDate = findViewById(R.id.current_date);
        if(currentDate.isChecked()) {
            Log.i(LOG_TAG, "current time is :" + currentDate.isChecked());
            // java.time.LocalDate.now() returns the instance of Systems Current date
            // so we make it String to set it in edit Text.
            etToDate.setText(java.time.LocalDate.now().toString());
        }

    }
    /**
     * it does all the heavy lifting work
     * checks for the valid input from the user
     * accordingly forming the url and sending a call to the restartLoader()
     * */
    private void search(){
        /**If connected to internet then only it will search or else display no internet */
        if(!isConnectedToInternet()){
            Log.i(LOG_TAG,"is connected to internet"+ isConnectedToInternet());
            emptyViewForListView.setImageResource(R.drawable.nointernet);
        }else {
            // store the tilt from the edit text
            String title = "";
            try {
                // gets the title from the edit text filed
                title = get_search_title(title);
                // gets the date in form of String from the Edit text of From date
                String fDate = get_From_Date();
                // gets the date in form of String from the Edit text of To date
                String tDate = get_to_date();
                // a call to the server is only made when date is from and to date are prefect and title is not null nor empty
                boolean isValid = isValidDates(fDate, tDate);
                if (isValid && !title.trim().isEmpty()) {
                    // returns the order by which the user will like to see the news in the app if selected or else returns @NULL
                    String order_by = getOrderOfNews();
                    // building a url form the input of the user
                    StringBuilder url = new StringBuilder();
                    url.append(base_url).append("from-date=").append(from_date + "&").append("to-date=").append(to_date + "&");
                    // if order by is not null the append it to the url or else leave it
                    if (order_by != null) {
                        url.append("order-by=").append(order_by + "&");
                    }
                    url.append("page-size=200&q=").append(title + "&").append(API_KEY);

                    URL_REQUEST = url.toString();
                    // when searching we need user to see that we are fecthing their requested data
                    horizontalProgressBar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    // when loading we not neeed no data found tag
                    emptyViewForListView.setVisibility(View.GONE);
                    //  call to the loader from there the server
                    loaderManager = getSupportLoaderManager();
                    loaderManager.restartLoader(1, null, this);
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, "Problem in Searching: " + e);
            }
        }

    }

    // check whether the TITLE text is empty or not and return a title which Supports the URL QUERY TO THE SERVER.
    private String get_search_title(String search_title){
        final EditText newsTitle = findViewById(R.id.Search_News);
        if(newsTitle.getText().toString().trim().equalsIgnoreCase("")){
            newsTitle.setError("This field can not be blank");
        }else{
            search_title = newsTitle.getText().toString();
            if(search_title.contains(" ")){
                String splitTitle[] = search_title.split(" ");
                StringBuilder sb = new StringBuilder();
                for(int space = 0 ; space < splitTitle.length ; space++){
                    sb.append(splitTitle[space]);
                    if(space + 1 < splitTitle.length){
                        sb.append("%20");
                    }
                }
                newsTitle.setError(null);
                search_title = sb.toString();
            }
            Log.i(LOG_TAG,"Search q: "+search_title);
        }
        return search_title;
    }

    /**this will get the Date from the From Date {@EDIT_TEXT} and convert it into a String and store it in a from_date*/
    private String get_From_Date(){
        EditText fromDateNews = findViewById(R.id.from_date);
        from_date = fromDateNews.getText().toString();
        return from_date;
    }
    /**this will get the Date from the To Date {@EDIT_TEXT} and convert it into a String and store it in a to_date*/
    private String get_to_date(){
        EditText toDateNews = findViewById(R.id.to_date);
        to_date = toDateNews.getText().toString();
        return to_date;
    }

    /**Validates dates and shows error if any , returns true if no errors or else false if any error found.*/
    private boolean isValidDates(String FromDate, String ToDate){
        // reference to the From date edit text
        EditText fromDate = findViewById(R.id.from_date);
        // from date cannot be null if so return false early
        if(from_date.isEmpty()){
            fromDate.setError("Date Required");
            return false;
        }
        // if from date is ahead of to date return false.
        if(from_date.compareTo(java.time.LocalDate.now().toString()) > 0){
            fromDate.setError("Error");
            return false;
        }
        // if from date is behind of to_Day date
        if(FromDate.compareTo(ToDate) < 0 || FromDate.compareTo(ToDate) == 0){
            fromDate.setError(null);
            // if to_Day date is ahead of current date return early
            if(ToDate.compareTo(java.time.LocalDate.now().toString()) > 0 ){
                etToDate.setError("Error");
                return false;
            }
            // if to day date is prefect and from_date then return true
            etToDate.setError(null);
            return true;
        }else{
            // show error for dates.
            etToDate.setError("Error");
            etToDate.setFocusable(true);
            etToDate.setFocusableInTouchMode(false);
            return false;
        }

    }

    /** we check for a order for the news if client as choosed so if choosed then return the Choosen order in a String format
     * and if the client has not choice any of the radio button then we return null */
    private String getOrderOfNews(){
        RadioGroup orderGroup = findViewById(R.id.new_rev_news);
        if(orderGroup.getCheckedRadioButtonId() != -1){
            int selectedRadioBtnId = orderGroup.getCheckedRadioButtonId();

            RadioButton selected = findViewById(selectedRadioBtnId);
            return selected.getText().toString().toLowerCase();
        }else{
            return null;
        }
    }

    // checking for the network connectivity  if connected to wifi or mobile data it will return @true else @false.
    public boolean isConnectedToInternet(){

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo =connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }
}