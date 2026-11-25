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
 @Override public void onCreate(Bundle state){super.onCreate(state); web=new WebView(this); web.setBackgroundColor(0xff0b1612);
 android.widget.FrameLayout root=new android.widget.FrameLayout(this); root.setBackgroundColor(0xff0b1612);
 if(android.os.Build.VERSION.SDK_INT>=30){getWindow().setDecorFitsSystemWindows(false); root.setOnApplyWindowInsetsListener((v,insets)->{android.graphics.Insets i=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout()|WindowInsets.Type.ime()); v.setPadding(i.left,i.top,i.right,i.bottom); return WindowInsets.CONSUMED;});}else{root.setFitsSystemWindows(true);}
 root.addView(web,new android.widget.FrameLayout.LayoutParams(-1,-1)); setContentView(root); root.requestApplyInsets();
 WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(true);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
 web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !HOST.equals(r.getUrl().getHost());}
 @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){Uri u=r.getUrl();if("blob".equals(u.getScheme())||"data".equals(u.getScheme()))return null;if("https".equals(u.getScheme())&&"eexgnwkphjrugfuaixcs.supabase.co".equals(u.getHost()))return null;if(!"https".equals(u.getScheme())||!HOST.equals(u.getHost()))return new WebResourceResponse("text/plain","UTF-8",403,"Forbidden",java.util.Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));String p=u.getPath(); if(p==null||!p.startsWith("/assets/")||p.contains(".."))return null;String f=p.substring(8);String mime=f.endsWith(".js")?"text/javascript":f.endsWith(".css")?"text/css":f.endsWith(".png")?"image/png":f.endsWith(".webmanifest")?"application/manifest+json":"text/html";try{return new WebResourceResponse(mime,"UTF-8",getAssets().open(f));}catch(IOException e){return new WebResourceResponse("text/plain","UTF-8",404,"Not found",java.util.Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));}}});
 web.addJavascriptInterface(new Object(){@JavascriptInterface public void saveBackup(String json){runOnUiThread(()->{pendingBackup=json;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"us-forever-backup.json");startActivityForResult(i,22);});}},"AndroidBackup");
 web.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){if(picker!=null)picker.onReceiveValue(null);picker=cb;try{Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);intent.addCategory(Intent.CATEGORY_OPENABLE);String[] types=p.getAcceptTypes();boolean images=false;for(String type:types){if(type!=null&&type.contains("image/"))images=true;}intent.setType(images?"image/*":"application/json");intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,p.getMode()==FileChooserParams.MODE_OPEN_MULTIPLE);intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivityForResult(intent,21);return true;}catch(Exception e){picker.onReceiveValue(null);picker=null;return false;}}});web.loadUrl("https://"+HOST+"/assets/index.html");}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==21&&picker!=null){ValueCallback<Uri[]> callback=picker;picker=null;java.util.ArrayList<Uri> selected=new java.util.ArrayList<>();if(result==RESULT_OK&&data!=null){if(data.getClipData()!=null){for(int i=0;i<data.getClipData().getItemCount();i++){Uri u=data.getClipData().getItemAt(i).getUri();if(u!=null)selected.add(u);}}else if(data.getData()!=null)selected.add(data.getData());}callback.onReceiveValue(selected.isEmpty()?null:selected.toArray(new Uri[0]));}if(request==22){if(result==RESULT_OK&&data!=null&&pendingBackup!=null){try(OutputStream out=getContentResolver().openOutputStream(data.getData())){out.write(pendingBackup.getBytes(StandardCharsets.UTF_8));Toast.makeText(this,"Backup saved",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Backup failed",Toast.LENGTH_LONG).show();}}pendingBackup=null;}}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}