package gma.android_rxfizzbuzz;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public final String TAG = "RxFizzBuzz";

        public TextView tvFizzBuzz;
        public Observable<String> obs;
        public Subscription subs;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            tvFizzBuzz = (TextView)rootView.findViewById(R.id.fb_value);

            obs = Observable.interval(1, TimeUnit.SECONDS).map(t -> t.toString() + " : " + (
                t != 0 && t % 3 == 0 && t % 5 == 0 ? "FizzBuzz"
                                      : t % 3 == 0 ? "Fizz"
                                      : t % 5 == 0 ? "Buzz"
                                      :              t.toString()));

            return rootView;
        }

        @Override
        public void onResume()
        {
            super.onResume();

            subs = obs.subscribeOn(Schedulers.newThread())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(m -> {
                          tvFizzBuzz.setText(m);
                          getView().setBackgroundColor(m.contains("FizzBuzz") ? Color.argb(255,190,230,190)
                                  : m.contains("Fizz") ? Color.argb(255,230,190,190)
                                  : m.contains("Buzz") ? Color.argb(255,190,190,230)
                                  :                      Color.argb(255,255,255,255));
                      });
        }

        @Override
        public void onPause()
        {
            super.onPause();
            subs.unsubscribe();
        }

    }
}
