

public class MainActivity extends AppCompatActivity {
    ImageView image;
    Uri uri1;
    int imgname=0;
    String boundary="*****";
    String lineEnd="\r\n";
    String twoHyphen="--";
    int bytesAvailable,bytesRead,bufferSize;
    int maxBufferSize=1*1024*1024;
    byte buffer[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image=(ImageView)findViewById(R.id.img1);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         uri1=data.getData();
        try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri1);
            image.setImageBitmap(bitmap);
            ImageUpload imageUpload=new ImageUpload();
            imageUpload.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  class ImageUpload extends AsyncTask<Void,Void,String>
    {
String message=null;
        @Override
        protected String doInBackground(Void... params) {

            try {
                String img="34";
           //     commonurl commonurl=new commonurl();
              //  String u=commonurl.getURL("image_upload.php?id="+id2);

                URL url=new URL("http://192.168.1.10:88/ImageUploadDemo/uploadimg.php");
                InputStream stream=getContentResolver().openInputStream(uri1);

                String rename="1.png";
                // String rename="37.png";

                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept","application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                httpURLConnection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
                httpURLConnection.setRequestProperty("ENCTYPE","application/json");
                httpURLConnection.setRequestProperty("Connection","Keep-Alive");

                httpURLConnection.connect();
                DataOutputStream dataOutputStream=new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphen+boundary+lineEnd);
                dataOutputStream.writeBytes("Content-Disposition:form-data;name=\"fileUpload\";filename=\""+rename+"\""+lineEnd);
                dataOutputStream.writeBytes(lineEnd);


                bytesAvailable=stream.available();
                bufferSize=Math.min(bytesAvailable,maxBufferSize);
                buffer=new byte[bufferSize];

                bytesRead=stream.read(buffer,0,bufferSize);
                while (bytesRead>0)
                {
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable=stream.available();
                    bufferSize=Math.min(bytesAvailable,maxBufferSize);
                    buffer=new byte[bufferSize];
                    bytesRead=stream.read(buffer,0,bufferSize);

                }
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphen+boundary+twoHyphen+lineEnd);
                stream.close();
                dataOutputStream.close();
                dataOutputStream.flush();


                int responsecode=httpURLConnection.getResponseCode();
                if(responsecode==200)
                {

                    StringBuilder stringBuilder=new StringBuilder();
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String s;
                    while ((s=bufferedReader.readLine())!=null)

                    {
                        stringBuilder.append(s);

                    }
                    JSONObject jsonObject=new JSONObject(stringBuilder.toString());
                    message=jsonObject.getString("path");
                    //   Toast.makeText(this,message,Toast.LENGTH_LONG).show();
                }

            }
            catch(Exception e)
            {

                e.printStackTrace();
            }

            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
        }
    }


}
