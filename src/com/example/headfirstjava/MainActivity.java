package com.example.headfirstjava;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IotdHandler handler = new IotdHandler();
		handler.processFeed();
		resetDisplay(handler.getTitle(), handler.getDate(), handler.getImage(),
				handler.getDescription());
	}

	public class IotdHandler extends DefaultHandler {
		private String url = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
		private boolean inUrl = false;
		private boolean inTitle = false;
		private boolean inDescription = false;
		private boolean inItem = false;
		private boolean inDate = false;
		private Bitmap image = null;
		private String title = null;
		private StringBuffer description = new StringBuffer();
		private String date = null;

		public void processFeed() {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				reader.setContentHandler(this);
				InputStream inputStream = new URL(url).openStream();
				reader.parse(new InputSource(inputStream));
			} catch (Exception e) {
			}
		}

		private Bitmap getBitmap(String url) {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(url)
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap bilde = BitmapFactory.decodeStream(input);
				input.close();
				return bilde;
			} catch (IOException ioe) {
				return null;
			}
		}

		public void startElement(String url, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (localName.endsWith(".jpg")) {
				inUrl = true;
			} else {
				inUrl = false;
			}

			if (localName.startsWith("item")) {
				inItem = true;
			} else if (inItem) {

				if (localName.equals("title")) {
					inTitle = true;
				} else {
					inTitle = false;
				}

				if (localName.equals("description")) {
					inDescription = true;
				} else {
					inDescription = false;
				}

				if (localName.equals("pubDate")) {
					inDate = true;
				} else {
					inDate = false;
				}
			}
		}

		public void characters(char ch[], int start, int length) {
			String chars = new String(ch).substring(start, start + length);
			if (inUrl && url == null) {
				image = getBitmap(chars);
			}
			if (inTitle && title == null) {
				title = chars;
			}
			if (inDescription) {
				description.append(chars);
			}
			if (inDate && date == null) {
				date = chars;
			}
		}

		public Bitmap getImage() {
			return image;
		}

		public String getTitle() {
			return title;
		}

		public StringBuffer getDescription() {
			return description;
		}

		public String getDate() {
			return date;
		}
	}

	private void resetDisplay(String title, String date, Bitmap image,
			StringBuffer description) {

		TextView titleView = (TextView) findViewById(R.id.app_name);
		titleView.setText(title);

		TextView dateView = (TextView) findViewById(R.id.date);
		dateView.setText(date);

		ImageView imageView = (ImageView) findViewById(R.id.img);
		imageView.setImageBitmap(image);

		TextView descriptionView = (TextView) findViewById(R.id.image_desc);
		descriptionView.setText(description);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}