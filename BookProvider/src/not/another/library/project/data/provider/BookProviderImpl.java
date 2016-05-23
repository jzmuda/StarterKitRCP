package not.another.library.project.data.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import not.another.library.project.data.BookStatusVo;
import not.another.library.project.data.BookVo;
/**
 * provides book list from rest service requests to local server
 * @author JZMUDA
 *
 */
public class BookProviderImpl implements BookProvider {

	private String url = "";
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final CloseableHttpClient client= HttpClients.createDefault();
	private RequestConfig requestConfig;
	public BookProviderImpl(String givenUrl) throws IOException {
		givenUrl=unNull(givenUrl);
		checkConnection(givenUrl);
		requestConfig = RequestConfig.custom()
				.setSocketTimeout(1000)
				.setConnectTimeout(1000)
				.build();
	}
	@Override
	public List<BookVo> findBooks(String authors, String title) throws IOException {
		title = unNull(title);
		authors = unNull(authors);

		String searchUrl = url+"/matrix/authors="+authors+";title="+title;

		URL obj = new URL(searchUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", null);

		InputStream inputStream = con.getInputStream();

		CollectionType constructCollectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,
				BookVo.class);
		return extractBook(inputStream, constructCollectionType);
	}

	@Override
	public void addBook(BookVo book) throws IOException {
		add(book);		
	}

	@Override
	public void addBook(String author, String title, BookStatusVo status) throws IOException {
		BookVo book = new BookVo(0L,unNull(author),unNull(title),status);
		add(book);		
	}


	private void add(BookVo book)
			throws IOException {
		String bookJSON = new ObjectMapper().writeValueAsString(book);
		String addUrl = url+"/update";
		HttpPost postRequest = new HttpPost(addUrl);
		postRequest.setConfig(requestConfig);
		postRequest.setHeader("Content-Type", "application/json");
		postRequest.setEntity(new StringEntity(bookJSON));
		CloseableHttpResponse response2;
		response2 = client.execute(postRequest);
		int code=response2.getStatusLine().getStatusCode();
		if(code<200 || code >299) {
			throw new IOException("POST failure "+code);
		}
		response2.close();
	}

	public String unNull(String text) {
		if(text == null){
			return "";
		}
		else return text;
	}

	@Override
	public void checkConnection(String repoUrl) throws IOException {
		URL urlToCheck;
		urlToCheck = new URL(repoUrl);
		HttpURLConnection huc;

		huc = (HttpURLConnection)  urlToCheck.openConnection();
		huc.setRequestMethod("HEAD"); 
		huc.connect();
		int responseCode = huc.getResponseCode();
		huc.disconnect();

		if (responseCode>299 || responseCode<200)
			throw new IOException("ERROR: "+responseCode);
		this.url=repoUrl;
	}

	@Override
	public List<BookVo> getAllBooks() throws IOException {
		String searchUrl = url+"/all";
		URL obj = new URL(searchUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", null);
		int responseCode = con.getResponseCode();
		if( responseCode<200 || responseCode>299)
			throw new IOException("Could not retrieve books, server response code: "+responseCode);
		InputStream inputStream = con.getInputStream();
		CollectionType constructCollectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,
				BookVo.class);
		return extractBook(inputStream, constructCollectionType);
	}

	private ArrayList<BookVo> extractBook(InputStream inputStream, CollectionType constructCollectionType) throws IOException {
		Object value = objectMapper.readValue(inputStream, constructCollectionType);	
		return value instanceof ArrayList ?   (ArrayList<BookVo>) value:new ArrayList<BookVo>();
	}


	@Override
	public void deleteBook(Long ID) throws IOException {
		String searchUrl = url+"/delete?id="+ID;
		URL obj = new URL(searchUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("DELETE");
		con.setRequestProperty("User-Agent", null);
		int responseCode = con.getResponseCode();
		if( responseCode<200 || responseCode>299)
			throw new IOException("Could not delete book, server response code: "+responseCode);

	}
}