package com.nosoft.lambdas;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


/**
 * Lambda function that simply prints "Hello World" if the input String is not provided,
 * otherwise, print "Hello " with the provided input String.
 */
//public class HelloWorld implements RequestHandler<String, Object>{

public class EpubToMobi implements RequestHandler<SNSEvent, Object> {
	
    static String BUCKET = "my-private-bucket";
    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
   
    
    
    private boolean downloadFileFromS3(String key, String localFile) {
    	
    	File file = new File(localFile);
        ObjectMetadata object = s3.getObject(new GetObjectRequest(BUCKET, key), file);
    
        boolean success = file.exists() && file.canRead();
        if (success ) 
        	System.out.println("downloaded correctly");
        
        return success;
        
    }
    
    
    private void uploadToS3(String localfile, String key) {
    	
    	  File file = new File(localfile);

          PutObjectRequest request = new PutObjectRequest(BUCKET, key, file);
          ObjectMetadata om = new ObjectMetadata();
          om.addUserMetadata("original_file", localfile);
           
          request.setMetadata(om);
          
          s3.putObject(request);
    }
    
    private void execEbookConvert() {
    	

    	String commandLine = "/opt/ebook-convert /tmp/pg10.epub /tmp/pg10.mobi";
    	
    	try {
			
           	Process proc = Runtime.getRuntime().exec(commandLine, null, new File("/tmp"));                        

           	proc.waitFor();

	        
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
			
		}    	
    }


    private void processEbook(String epubLocation, String mailTo) {
    	
    	
    	Path p = Paths.get(epubLocation);
    	String file = p.getFileName().toString();
    	System.out.println(file);
    	
    	this.downloadFileFromS3(epubLocation, "/tmp/" + file );
        this.execEbookConvert();
        this.uploadToS3("/tmp/"+file, "mobis/"+file+".mobi");
    	
        // enquee in SNS for email to xxxx@kindle.com
        
    }

	@Override
	public Object handleRequest(SNSEvent event, Context context) {
		for   (SNSRecord record : event.getRecords()) {
			Map<String, MessageAttribute> msgAttr = record.getSNS().getMessageAttributes();
			String epub = msgAttr.get("epub").getValue();
			String mailTo = msgAttr.get("email").getValue();
	
			processEbook(epub, mailTo);
			
		}
		return null;
	}

}
