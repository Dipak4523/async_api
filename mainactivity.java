
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAsync myAsync=new myAsync();
        myAsync.execute();


    }
    class myAsync extends AsyncTask<Void,Void,ArrayList<String>>
    {
            ArrayList<String> name=new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            try
            {
                URL url=new URL("http://192.168.1.7:88/DemoWS/select.php");
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","applicatio/json");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.connect();
                int code=urlConnection.getResponseCode();
                if(code==200)
                {
                    String s=null;
                    StringBuilder stringBuilder=new StringBuilder();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((s=reader.readLine())!=null)
                    {
                        stringBuilder.append(s);
                    }

                    JSONObject jsonObject=new JSONObject(stringBuilder.toString());
                    JSONArray jsonArray=new JSONArray();
                    jsonArray=  jsonObject.getJSONArray("Name");

                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject object = new JSONObject();
                        object = jsonArray.getJSONObject(i);
                        name.add(object.getString("name"));
                    }
                }
            }catch (Exception e)
            {
                Log.e("Error",e.getMessage().toString());
            }
            return name;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            ListView listView=(ListView)findViewById(R.id.lst);
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
            listView.setAdapter(arrayAdapter);

        }
    }
}
