package foodguide.md06.vtca.foodguide.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Calendar;

import foodguide.md06.vtca.foodguide.R;
import foodguide.md06.vtca.foodguide.adapter.MoreRecipesAdapter;
import foodguide.md06.vtca.foodguide.model.Recipes;
import foodguide.md06.vtca.foodguide.ultil.ContractsDatabase;
import foodguide.md06.vtca.foodguide.ultil.ControllerDatabase;


public class RecipesDetailActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Recipes mRecipes, mRecipesMore;
    String ingredients, cacBuocThucHien;
    ImageView imageOnTop, btnCalendar;
    TextView tvTime, tvServing, tvKcal;
    LinearLayout layoutNguyenLieu, layoutHuongDan;
    ControllerDatabase db;
    Boolean pressed = false;

    private FacebookCallback<LoginResult> mCallBack;
    ShareDialog shareDialog;
    CallbackManager mCallBackManager;

    ArrayList<Recipes> mList = new ArrayList<>();
    MoreRecipesAdapter mMoreRecipesAdapter;
    TwoWayView mTwoWayView;
    FloatingActionButton mFloatingActionButton;
    int pickHour, pickMinute = 0;
    int hour = 0;
    int minute = 0;

    int itemAlready = 0;

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    protected boolean signedInUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes_detail_layout);

        db = new ControllerDatabase(this);
        db.open();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //start api share google fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallBackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        mCallBack = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {

            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        // end api

        //get intent
        Intent it = getIntent();
        mRecipes = (Recipes) it.getSerializableExtra("Congthuc");
        // end intent


        loadComponent();


        //////check item already//////
//        Cursor cursor = db.checkFavouriteRecipesAlready(mRecipes.getId());
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            itemAlready = cursor.getInt(cursor.getColumnIndex(ContractsDatabase.KEY_FAVOURITE_ID_RECIPES));
//            cursor.moveToNext();
//        }
//
//        if (itemAlready != 0){
//            pressed = true;
//            mFloatingActionButton.setIcon(R.mipmap.ic_action_favorite_pressed);
//        }

        getDetailRecipes(mRecipes);


        // Listview horizontal
        TwoWayView listView = (TwoWayView) findViewById(R.id.lvItems);
        Cursor c = db.getListRecipes();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            mRecipesMore = new Recipes();
            mRecipesMore.setId(c.getInt(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_ID)));
            mRecipesMore.setImage(c.getString(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_IMAGE)));
            mRecipesMore.setName(c.getString(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_NAME)));
            mRecipesMore.setTime(c.getInt(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_TIME)));
            mRecipesMore.setServing(c.getInt(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_SERVING)));
            mRecipesMore.setKcal(c.getInt(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_KCAL)));
            mRecipesMore.setIngredients(c.getString(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_INGREDIENTS)));
            mRecipesMore.setInstruction(c.getString(c.getColumnIndex(ContractsDatabase.KEY_RECIPES_INSTRUCTION)));

            mList.add(mRecipesMore);
            c.moveToNext();
        }
        mMoreRecipesAdapter = new MoreRecipesAdapter(RecipesDetailActivity.this, mList);
        listView.setAdapter(mMoreRecipesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFloatingActionButton.setIcon(R.mipmap.ic_action_favorite);
                mRecipesMore = mList.get(position);
                getDetailRecipes(mRecipesMore);
            }
        });
        final Calendar myCalendar = Calendar.getInstance();
        hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        minute = myCalendar.get(Calendar.MINUTE);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePicker();
//                Calendar cal = Calendar.getInstance();
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//                int month = cal.get(Calendar.MONTH) + 1;
//                int year = cal.get(Calendar.YEAR);
//                int hours = cal.get(Calendar.HOUR_OF_DAY);
//                int minute = cal.get(Calendar.MINUTE);
//                int endTime = mRecipes.getTime();
//                mCalendar = new foodguide.md06.vtca.foodguide.model.Calendar(idCalendar, nameCalendar, day, month, year, hours, minute, endTime);
//                long save = db.insertRecipesToCalendar(mCalendar);
//                long save = 1;
//                if (save <= 0) {
//                    Toast.makeText(RecipesDetailActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(RecipesDetailActivity.this, "Bạn đã thêm thành công món ăn vào lịch.", Toast.LENGTH_LONG).show();
//                }
            }
        });

    }

    private void createTimePicker() {
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int month = cal.get(Calendar.MONTH) + 1;
        final int year = cal.get(Calendar.YEAR);
        final int cHours = cal.get(Calendar.HOUR_OF_DAY);
        final int cMinute = cal.get(Calendar.MINUTE);
        new TimePickerDialog(RecipesDetailActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        pickHour = hourOfDay;
                        pickMinute = minute;
                        if (cHours == pickHour && pickMinute <= cMinute) {
                            Toast.makeText(RecipesDetailActivity.this, "Chọn lại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int idCalendar = mRecipes.getId();
                        String nameCalendar = mRecipes.getName();
                        int endTime = mRecipes.getTime();
                        foodguide.md06.vtca.foodguide.model.Calendar mCalendar
                                = new foodguide.md06.vtca.foodguide.model.Calendar(idCalendar, nameCalendar, day, month, year, pickHour, pickMinute, endTime);
                        long save = db.insertRecipesToCalendar(mCalendar);
                        if (save <= 0) {
                            Toast.makeText(RecipesDetailActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(RecipesDetailActivity.this, "Bạn đã thêm thành công món ăn vào lịch.", Toast.LENGTH_LONG).show();
                            createAlarm();
                        }
                    }
                }, hour, minute, true).show();
    }

    private void loadComponent() {
        layoutNguyenLieu = (LinearLayout) findViewById(R.id.NguyenLieuLayout);
        layoutHuongDan = (LinearLayout) findViewById(R.id.HuongDanLayout);
        imageOnTop = (ImageView) findViewById(R.id.imageRecipes);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvServing = (TextView) findViewById(R.id.tvServing);
        tvKcal = (TextView) findViewById(R.id.tvKcal);
        btnCalendar = (ImageView) findViewById(R.id.btnCalendar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.btnFavorite);
    }

    private void getDetailRecipes(final Recipes recipes) {
        itemAlready = 0;
        ingredients = recipes.getIngredients();
        cacBuocThucHien = recipes.getInstruction();
        tvTime.setText(getFormatTime(recipes.getTime()));
        tvServing.setText(recipes.getServing() + " người");
        tvKcal.setText(recipes.getKcal() + " kcal");

        getSupportActionBar().setTitle(recipes.getName());


        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(3)
                .cornerRadiusDp(100)
                .oval(false)
                .build();

        Picasso.with(RecipesDetailActivity.this)
                .load(recipes.getImage())
                .fit()
                .transform(transformation)
                .into(imageOnTop);


        String a[] = ingredients.split(",");
        for (int i = 0; i < a.length; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(30, 0, 0, 0);
            CheckBox cb = new CheckBox(RecipesDetailActivity.this);
            cb.setText(a[i]);
            cb.setLayoutParams(lp);
            cb.setTextColor(getResources().getColor(R.color.tv_recipes));
            layoutNguyenLieu.addView(cb);
        }


        String b[] = cacBuocThucHien.split("#");
        for (int j = 0; j < b.length; j++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(30, 0, 10, 0);
            TextView tv = new TextView(RecipesDetailActivity.this);
            tv.setLayoutParams(lp);
            tv.setText(b[j] + "\n");
            tv.setTextColor(getResources().getColor(R.color.tv_recipes));
            layoutHuongDan.addView(tv);
        }

        Cursor cursor = db.checkFavouriteRecipesAlready(recipes.getId());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            itemAlready = cursor.getInt(cursor.getColumnIndex(ContractsDatabase.KEY_FAVOURITE_ID_RECIPES));
            cursor.moveToNext();
        }

        if (itemAlready != 0) {
            mFloatingActionButton.setIcon(R.mipmap.ic_action_favorite_pressed);
        } else {
            mFloatingActionButton.setIcon(R.mipmap.ic_action_favorite);
        }

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed = !pressed;
                if (pressed) {
                    // Khi them mon an
                    mFloatingActionButton.setIcon(R.mipmap.ic_action_favorite_pressed);
                    db.addFavouriteRecipes(recipes.getId(), recipes.getName(), recipes.getImage(), String.valueOf(recipes.getTime()), String.valueOf(recipes.getServing()));
                } else {
                    // Khi bo mon an
                    mFloatingActionButton.setIcon(R.mipmap.ic_action_favorite);
                    db.DelFavouriteRecipesItem(recipes.getId());
                }

            }
        });
    }

    public void buildShareLinkContent() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?"))
                    .setImageUrl(Uri.parse(mRecipes.getImage()))
                    .setContentTitle(mRecipes.getName())
                    .setContentDescription("Click để xem công thức làm món " + mRecipes.getName())
                    .build();
            shareDialog.show(content);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            //Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            //String personName = currentPerson.getDisplayName();
            Intent shareIntent = new PlusShare.Builder(this)
                    .setType("text/plain")
                    .setText("Welcome to the Google+ platform.")
                    .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                    .getIntent();

            startActivityForResult(shareIntent, 0);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {

                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.

                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    signedInUser = false;
                }
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }


    public String getFormatTime(int time) {
        String t = "";
        if (time < 60) {
            t = time + " phút";
        } else {
            int hours, minutes;
            hours = time / 60;
            minutes = time % 60;
            if (minutes == 0) {
                t = hours + " giờ";
            } else {
                t = hours + " giờ " + minutes + " phút";
            }
        }

        return t;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipes_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                AccessToken token = AccessToken.getCurrentAccessToken();
                if (token == null) {
                    final Dialog dialog = new Dialog(RecipesDetailActivity.this);
                    dialog.setContentView(R.layout.share_dialog_custom_view);
                    dialog.setTitle("Login: ");
                    dialog.show();

                    LoginButton loginButton = (LoginButton) dialog.findViewById(R.id.login_button);
                    loginButton.setReadPermissions("user_friends");
                    loginButton.registerCallback(mCallBackManager, mCallBack);

                    SignInButton signInButton = (SignInButton) dialog.findViewById(R.id.signin_button_google);
                    signInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!mGoogleApiClient.isConnecting()) {
                                dialog.cancel();
                                signedInUser = true;
                                mGoogleApiClient.connect();
                            }
                        }
                    });

                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        buildShareLinkContent();
                    }
                }
                if (token != null) {
                    buildShareLinkContent();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAlarm() {
        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        int minuteDiff = 0;
        int hourDiff = pickHour - hours;
        if (hourDiff > 0) {
            minuteDiff = pickMinute + 60 - minute;
        } else {
            minuteDiff = pickMinute - minute;
        }

        long secondDiff = (hourDiff * 60 * 60 + (minuteDiff) * 60) * 1_000L;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("FOO_ACTION");
        intent.putExtra("KEY_FOO_STRING", mRecipes.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ secondDiff, pendingIntent);
            Toast.makeText(RecipesDetailActivity.this,
                    "Thông báo sau " + hourDiff + " giờ và " + minuteDiff + " phút",
                    Toast.LENGTH_SHORT).show();
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+secondDiff, pendingIntent);
            Toast.makeText(RecipesDetailActivity.this,
                    "Thông báo sau " + hourDiff + " giờ và " + minuteDiff + " phút",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
