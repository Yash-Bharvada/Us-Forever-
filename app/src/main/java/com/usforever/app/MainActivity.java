package com.usforever.app;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;
import android.widget.Toast;
import android.view.WindowInsets;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
 private WebView web; private ValueCallback<Uri[]> picker; private String pendingBackup;
 private static final String HOST="appassets.androidplatform.net";
 @Override public void onCreate(Bundle state){super.onCreate(state); web=new WebView(this); web.setBackgroundColor(0xff0b1612); setContentView(web);
 if(android.os.Build.VERSION.SDK_INT>=35){web.setOnApplyWindowInsetsListener((v,insets)->{android.graphics.Insets i=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout()|WindowInsets.Type.ime());v.setPadding(i.left,i.top,i.right,i.bottom);return insets;});}
 WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(true);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
 web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !HOST.equals(r.getUrl().getHost());}
 @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){Uri u=r.getUrl();if(!"https".equals(u.getScheme())||!HOST.equals(u.getHost()))return new WebResourceResponse("text/plain","UTF-8",403,"Forbidden",java.util.Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));String p=u.getPath(); if(p==null||!p.startsWith("/assets/")||p.contains(".."))return null;String f=p.substring(8);String mime=f.endsWith(".js")?"text/javascript":f.endsWith(".css")?"text/css":f.endsWith(".png")?"image/png":f.endsWith(".webmanifest")?"application/manifest+json":"text/html";try{return new WebResourceResponse(mime,"UTF-8",getAssets().open(f));}catch(IOException e){return new WebResourceResponse("text/plain","UTF-8",404,"Not found",java.util.Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));}}});
 web.addJavascriptInterface(new Object(){@JavascriptInterface public void saveBackup(String json){runOnUiThread(()->{pendingBackup=json;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"us-forever-backup.json");startActivityForResult(i,22);});}},"AndroidBackup");
 web.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){if(picker!=null)picker.onReceiveValue(null);picker=cb;try{startActivityForResult(p.createIntent(),21);return true;}catch(Exception e){picker.onReceiveValue(null);picker=null;return false;}}});web.loadUrl("https://"+HOST+"/assets/index.html");}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==21&&picker!=null){picker.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result,data));picker=null;}if(request==22){if(result==RESULT_OK&&data!=null&&pendingBackup!=null){try(OutputStream out=getContentResolver().openOutputStream(data.getData())){out.write(pendingBackup.getBytes(StandardCharsets.UTF_8));Toast.makeText(this,"Backup saved",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Backup failed",Toast.LENGTH_LONG).show();}}pendingBackup=null;}}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}