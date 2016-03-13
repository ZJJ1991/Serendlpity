package com.example.jzhou.serendlpity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Pair;
import android.widget.Toast;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.ReferenceQueue;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bel on 21.02.2016.
 */
public class ServerRequests {

    ProgressDialog progressDialog;

    public static final String SERVER_ADDRESS = "http://serendipity.netne.net/";
    public static final String ENCODING_FORMAT = "UTF-8";

    public ServerRequests(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback){
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback userCallback){
        progressDialog.show();
        new FetchUserDataAsyncTask(user, userCallback).execute();
    }

    public void uploadRecordingInBackground(String filePath, String fileName, String username){
        progressDialog.show();
        new UploadRecordingAsyncTask(filePath, fileName, username).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void, Void>{

        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback){
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {

            //HttpParams CHANGE
            //from https://www.youtube.com/watch?v=cOsZHuu8Qog

            try

                {
                    URL url = new URL(SERVER_ADDRESS + "Register.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    //we past information
                    httpURLConnection.setDoOutput(true);
                    //get outputstreamwrite from http connection
                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    //write down information
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, ENCODING_FORMAT));
                    //encode data before sending
                    String data = URLEncoder.encode("username", ENCODING_FORMAT) + "=" + URLEncoder.encode(user.username, ENCODING_FORMAT) + "&" +
                            URLEncoder.encode("email", ENCODING_FORMAT) + "=" + URLEncoder.encode(user.email, ENCODING_FORMAT) + "&" +
                            URLEncoder.encode("password", ENCODING_FORMAT) + "=" + URLEncoder.encode(user.password, ENCODING_FORMAT) + "&";
                    //write data into buffer writer
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    //input stream to get response from the server
                    InputStream inputStream = httpURLConnection.getInputStream();
                    inputStream.close();

                    httpURLConnection.disconnect();

                }catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void,Void, User> {

        User user;
        GetUserCallback userCallback;

        public FetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {

            try{
                URL url = new URL(SERVER_ADDRESS + "FetchUserData.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                //we past information
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //get outputstreamwrite from http connection
                OutputStream outputStream = httpURLConnection.getOutputStream();
                //write down information
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, ENCODING_FORMAT));
                //encode data before sending
                String data = URLEncoder.encode("email", ENCODING_FORMAT) + "=" + URLEncoder.encode(user.email, ENCODING_FORMAT) + "&" +
                        URLEncoder.encode("password", ENCODING_FORMAT) + "=" + URLEncoder.encode(user.password, ENCODING_FORMAT) + "&";
                //write data into buffer writer
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //input stream to get response from the server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String response ="";
                String line = "";

                while((line = bufferedReader.readLine()) != null)
                {
                    response+=line;
                }

                //GET {"username":"yunhui","email":"zhouyunhui1994@gmail","password":"zyh1994","photo":null,"gender":null,"birthday":null,"city":null,"country":null}

                //decode JsonArray got from response
                //JSONArray jsonArray = new JSONArray(response);
                User returnedUser = null;
                JSONObject jsonObject = new JSONObject(response);

                if(jsonObject.length() == 0 ){
                    returnedUser = null;
                }
                else{
                        String username = jsonObject.getString("username");
                        String email = jsonObject.getString("email");
                        String password = jsonObject.getString("password");
                        //user.photo = jsonObject.getString("photo");
                        //user.birthday = jsonObject.get("birthday");
                        //String city = jsonObject.getString("city");
                        //String country = jsonObject.getString("country");

                    returnedUser = new User(username,email, password);
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return returnedUser;

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            progressDialog.dismiss();
            userCallback.done(user);
            super.onPostExecute(user);
        }

    }

    public class UploadRecordingAsyncTask extends AsyncTask<String,Void, Void>{
            String fileName;
            String filePath;
            String username;

        public UploadRecordingAsyncTask(String filePath, String fileName, String username){
            this.filePath = filePath;
            this.fileName = fileName;
            this.username = username;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            try{
                URL url = new URL(SERVER_ADDRESS + "UploadRecording.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                //we past information
                httpURLConnection.setDoOutput(true);

                //get outputstreamwrite from http connection
                OutputStream outputStream = httpURLConnection.getOutputStream();
                //write down information
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, ENCODING_FORMAT));

                //Encoding file and than sending it to the database folder
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();



                //CHECKING FROM HERE
                FileInputStream fileInputStream = new FileInputStream(new File(filePath));



                InputStream inputStreamFile = new BufferedInputStream(fileInputStream);
                int numOfBytes = inputStreamFile.available();

                byte[] audioBytesFile = new byte[numOfBytes];
                int i = inputStreamFile.read(audioBytesFile,0,numOfBytes);


                //String audioString = Base64.encodeToString(audioBytesFile, 0);
                String audioString = Base64.encodeToString(audioBytesFile, 0);

                inputStreamFile.close();

//                byte[] bytes = new byte[1024];
//                int n;
//                while (-1 != (n = fileInputStream.read(bytes)))
//                    byteArrayOutputStream.write(bytes, 0, n);

                //byte[] audioBytes = byteArrayOutputStream.toByteArray();

                //String audioString = Base64.encodeToString(audioBytes, 0);

                //encode data before sending
                String data = URLEncoder.encode("filename", ENCODING_FORMAT) + "=" + URLEncoder.encode(fileName, ENCODING_FORMAT) + "&" +
                        URLEncoder.encode("owner", ENCODING_FORMAT) + "=" + URLEncoder.encode(username, ENCODING_FORMAT) + "&" +
                        URLEncoder.encode("encodedfile", ENCODING_FORMAT) + "=" + URLEncoder.encode(audioString, ENCODING_FORMAT) + "&";

                //write data into buffer writer
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //input stream to get response from the server
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();

                httpURLConnection.disconnect();

            }catch(MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }



}
