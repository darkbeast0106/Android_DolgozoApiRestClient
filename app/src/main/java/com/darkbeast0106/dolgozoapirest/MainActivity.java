package com.darkbeast0106.dolgozoapirest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.darkbeast0106.dolgozoapirest.core.RequestHandler;
import com.darkbeast0106.dolgozoapirest.core.RequestType;
import com.darkbeast0106.dolgozoapirest.core.Response;
import com.darkbeast0106.dolgozoapirest.models.Dolgozo;
import com.darkbeast0106.dolgozoapirest.models.DolgozoApiValasz;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private class RequestTask extends AsyncTask<Void, Void, Response>{
        String url;
        String params;
        RequestType requestType;

        public RequestTask(String url, String params, RequestType requestType) {
            this.url = url;
            this.params = params;
            this.requestType = requestType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            textHiba.setText("");
        }

        @Override
        protected void onPostExecute(Response r) {
            super.onPostExecute(r);
            progressBar.setVisibility(View.GONE);
            if (r != null){
                Gson jsonConvert = new Gson();
                DolgozoApiValasz valasz = jsonConvert.fromJson(r.getContent(), DolgozoApiValasz.class);
                switch (r.getResponseCode()) {
                    case 200:
                    case 201:
                        refreshDolgozoList(valasz.getAdatok(), requestType, params);
                        break;
                    default:
                        Toast.makeText(MainActivity.this, r.getResponseCode()+ " "+ valasz.getMessage(), Toast.LENGTH_SHORT).show();
                        textHiba.setText(r.getResponseCode()+" "+ valasz.getMessage());
                        break;
                }
            }
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response r = null;
            switch (requestType){
                case GET:
                    try {
                        r = RequestHandler.getRequest(url);
                    } catch (IOException e) {
                        hibaKiir(e);
                    }
                    break;
                case POST:
                    try {
                        r = RequestHandler.postRequestJSON(url, params);
                    } catch (IOException e) {
                        hibaKiir(e);
                    }
                    break;
                case PUT:
                    try {
                        r = RequestHandler.putRequestJSON(url, params);
                    } catch (IOException e) {
                        hibaKiir(e);
                    }
                    break;
                case DELETE:
                    try {
                        r = RequestHandler.deleteRequest(url);
                    } catch (IOException e) {
                        hibaKiir(e);
                    }
                    break;
            }
            return r;
        }
    }

    private void hibaKiir(IOException e) {
        this.runOnUiThread(new HibaRunnable(e));
    }

    private class HibaRunnable implements Runnable {
        Exception e;

        public HibaRunnable(Exception e) {
            this.e = e;
        }

        @Override
        public void run() {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            textHiba.setText(e.toString());
        }
    }

    private class DolgozoAdapter extends ArrayAdapter<Dolgozo>{

        public DolgozoAdapter() {
            super(MainActivity.this, R.layout.dolgozo_list_item, dolgozoList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.dolgozo_list_item, null, true);
            TextView display = listViewItem.findViewById(R.id.display);
            TextView update = listViewItem.findViewById(R.id.update);
            TextView delete = listViewItem.findViewById(R.id.delete);

            Dolgozo dolgozo = dolgozoList.get(position);
            display.setText(String.format("%s (%d)", dolgozo.getNev(), dolgozo.getKor()));

            update.setOnClickListener(v -> {
                editID.setText(String.valueOf(dolgozo.getId()));
                editNev.setText(dolgozo.getNev());
                if (dolgozo.getNem().equals("férfi")){
                    radioFerfi.setChecked(true);
                } else {
                    radioNo.setChecked(true);
                }
                editKor.setText(String.valueOf(dolgozo.getKor()));
                editFizetes.setText(String.valueOf(dolgozo.getFizetes()));
                btnFelvetelre.setVisibility(View.GONE);
                btnModosit.setVisibility(View.VISIBLE);
                inputs.setVisibility(View.VISIBLE);
                btnFelvesz.setVisibility(View.GONE);
            });

            delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Megerősítés");
                builder.setMessage(String.format("Biztos törli a következő dolgozót: %s?", dolgozo.getNev()));
                builder.setPositiveButton("Igen", (dialog, which) -> dolgozoTorlese(dolgozo.getId()));
                builder.setNegativeButton("Nem",  (dialog, which) -> {});
                builder.setCancelable(false);
                builder.create().show();
            });

            return listViewItem;
        }
    }

    private void refreshDolgozoList(List<Dolgozo> adatok, RequestType requestType, String params) {
        switch (requestType){
            case GET:
                dolgozoList.clear();
                dolgozoList.addAll(adatok);
                break;
            case POST:
                dolgozoList.addAll(0, adatok);
                inputokAlaphelyzetbe();
                break;
            case PUT:
                Dolgozo dolgozoPut = adatok.get(0);
                Dolgozo regi = dolgozoList.stream().filter(d -> d.getId() == dolgozoPut.getId()).findFirst().get();
                regi.setNev(dolgozoPut.getNev());
                regi.setNem(dolgozoPut.getNem());
                regi.setKor(dolgozoPut.getKor());
                regi.setFizetes(dolgozoPut.getFizetes());
                inputokAlaphelyzetbe();
                break;
            case DELETE:
                int id = Integer.parseInt(params);
                dolgozoList.removeIf(d -> d.getId() == id);
                break;
        }
        inputs.setVisibility(View.GONE);
    }

    //region fields
    private TextView textHiba;
    private ListView adatok;
    private Button btnFelvesz;
    private Button btnModosit;
    private Button btnMegse;
    private Button btnFelvetelre;
    private LinearLayout inputs;
    private EditText editID;
    private EditText editNev;
    private EditText editKor;
    private EditText editFizetes;
    private RadioButton radioFerfi;
    private RadioButton radioNo;
    private ProgressBar progressBar;
    private List<Dolgozo> dolgozoList;
    private final String BASE_URL = "http://darkbeast0106.ddns.net/api/dolgozo";
    //endregion fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listeners();
        dolgozokListazasa();
    }
    private void inputokAlaphelyzetbe() {
        btnFelvetelre.setVisibility(View.VISIBLE);
        btnFelvesz.setVisibility(View.GONE);
        btnModosit.setVisibility(View.GONE);
        inputs.setVisibility(View.GONE);
        editID.setText("");
        editNev.setText("");
        editKor.setText("");
        editFizetes.setText("");
        radioFerfi.setChecked(true);
    }

    private void listeners() {
        textHiba.setMovementMethod(new ScrollingMovementMethod());
        btnFelvetelre.setOnClickListener(v -> {
            btnFelvetelre.setVisibility(View.GONE);
            btnModosit.setVisibility(View.GONE);
            inputs.setVisibility(View.VISIBLE);
            btnFelvesz.setVisibility(View.VISIBLE);
        });

        btnMegse.setOnClickListener(v -> {
            inputokAlaphelyzetbe();
        });

        btnFelvesz.setOnClickListener(v -> {
            try {
                Dolgozo d = createFromInput();
                Gson converter = new Gson();
                String json = converter.toJson(d);
                RequestTask task = new RequestTask(BASE_URL, json, RequestType.POST);
                task.execute();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnModosit.setOnClickListener(v -> {
            try {
                Dolgozo d = createFromInput();
                String id = editID.getText().toString();
                d.setId(Integer.parseInt(id));
                Gson converter = new Gson();
                String json = converter.toJson(d);
                RequestTask task = new RequestTask(BASE_URL+"/"+id, json, RequestType.PUT);
                task.execute();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Dolgozo createFromInput() throws Exception {
        String nev = editNev.getText().toString().trim();
        String nem = radioFerfi.isChecked() ? "férfi" : "nő";
        String korString = editKor.getText().toString().trim();
        String fizetesString = editFizetes.getText().toString().trim();
        if (nev.isEmpty() || korString.isEmpty() || fizetesString.isEmpty()){
            throw new Exception("Minden mezőt ki kell tölteni");
        }
        int kor = Integer.parseInt(korString);
        int fizetes = Integer.parseInt(fizetesString);
        return new Dolgozo(0, nev, nem, kor, fizetes);
    }

    private void init(){
        adatok = findViewById(R.id.adatok);
        textHiba = findViewById(R.id.textHiba);
        btnFelvesz = findViewById(R.id.btnFelvesz);
        btnModosit = findViewById(R.id.btnModosit);
        btnMegse = findViewById(R.id.btnMegse);
        btnFelvetelre = findViewById(R.id.btnFelvetelre);
        inputs = findViewById(R.id.inputs);
        editID = findViewById(R.id.editID);
        editNev = findViewById(R.id.editNev);
        editKor = findViewById(R.id.editKor);
        editFizetes = findViewById(R.id.editFizetes);
        radioFerfi = findViewById(R.id.radioFerfi);
        radioNo = findViewById(R.id.radioNo);
        progressBar = findViewById(R.id.progressBar);
        dolgozoList = new ArrayList<>();
        adatok.setAdapter(new DolgozoAdapter());
    }

    private void dolgozoTorlese(int id) {
        String json = String.valueOf(id);
        RequestTask task = new RequestTask(BASE_URL+"/"+id, json, RequestType.DELETE);
        task.execute();
    }

    private void dolgozokListazasa(){
        String json = "";
        RequestTask task = new RequestTask(BASE_URL, json, RequestType.GET);
        task.execute();
    }


}