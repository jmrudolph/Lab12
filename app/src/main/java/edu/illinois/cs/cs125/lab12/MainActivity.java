package edu.illinois.cs.cs125.lab12;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Main class for our UI design lab.
 */
public final class MainActivity extends Activity {
    /**
     * categories lists the different categories in the spinner.
     */
    private String[] categories = {"Trivia", "Date", "Year", "Math Fact"};
    /**
     * description provides a description for each category.
     */
    private String[] description = {"Choose Number to see a random fact about the number you input, " +
            "with the topic being random.", "Choose Date and then enter in the number in the " +
            "following format mm/dd/yyyy to see a random fact about that specific date in time.",
            "Choose Year and enter in a year to see a random fact about that specific year.",
            "Choose Math Fact to see a random fact about the relation that that specific number " +
                    "has with math."};
    /**
     * adapter.
     */
    ArrayAdapter<String> adapter;
    /**
     * spinner.
     */
    Spinner sp;
    /**
     * description for textView.
     */
    private TextView des;

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our API requests. */
    private static RequestQueue requestQueue;

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set up the queue for our API requests
        requestQueue = Volley.newRequestQueue(this);




        setContentView(R.layout.activity_main);
        sp = (Spinner) findViewById(R.id.spin);
        des = (TextView) findViewById(R.id.text1);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, categories);

        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int i, final long id) {
                switch (i) {
                    case 0:
                        des.setText(description[i]);
                        break;
                    case 1:
                        des.setText(description[i]);
                        break;
                    case 2:
                        des.setText(description[i]);
                        break;
                    case 3:
                        des.setText(description[i]);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Button button = findViewById(R.id.Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                startAPICall();
            }
        });
    }

    /**
     * Run when this activity is no longer visible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     *
     * Make a call to the weather API.
     */
    void startAPICall() {
        Spinner mySpinner = findViewById(R.id.spin);
        String catagory = mySpinner.getSelectedItem().toString().toLowerCase();

        EditText myEdit = findViewById(R.id.editText);
        String number = myEdit.getText().toString();
        if (catagory.equals("math fact")) {
            catagory = "math";
        }
        try {
            double d = Double.parseDouble(number);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://numbersapi.com/" + number + "/" + catagory + "?json"
                            + BuildConfig.API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                Log.d(TAG, response.toString(2));
                                successfulAPICall(response.toString());
                            } catch (JSONException ignored) {
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (NumberFormatException nfe) {
            final TextView textView = findViewById(R.id.result);
            textView.setText("Invalid input, please retry.");
            Log.d(TAG, "Invalid input");
        }
        catch (Error e) {
            e.printStackTrace();
        }
    }

    void successfulAPICall(final String json) {
        String response;
        try {
            JsonParser parser = new JsonParser();
            JsonObject result = parser.parse(json).getAsJsonObject();
            System.out.println(result.get("found"));
            System.out.println("That worked");
            if (result.get("found").getAsBoolean()) {
                response = result.get("text").getAsString();
            } else {
                response = "Number format not correct, Please try again.";
            }
        } catch (Exception ignored) {
            response = "something failed oops";
            ignored.printStackTrace();
        }
        final TextView textView = findViewById(R.id.result);
        textView.setText(response);
        Log.d(TAG, "API call completed");
    }
}
