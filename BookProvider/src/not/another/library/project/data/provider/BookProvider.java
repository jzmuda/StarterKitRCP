package not.another.library.project.data.provider;

import java.io.IOException;
import java.util.List;

import not.another.library.project.data.BookStatusVo;
import not.another.library.project.data.BookVo;

/**
 * Book provider interface from a particular SpringMVC REST web service
 * Requires specific TO/VO types for books
 * @author JZMUDA
 *
 */

public interface BookProvider {
	/**
	 * checks the existence of given repository url, sets the address internally
	 * @param repoUrl
	 * @throws IOException
	 * on failed connection check
	 */
	public void checkConnection(String repoUrl) throws IOException;
	/**
	 * gets all books from the repository
	 * @return
	 * @throws IOException
	 * when GET request fails
	 */
	public List<BookVo> getAllBooks() throws IOException;
	/**
	 * Deletes o book with given ID
	 * @param ID
	 * @throws IOException
	 * if DELETE request fails
	 */
	public void deleteBook(Long ID) throws IOException;
	/**
	 * Adds a book
	 * @param author
	 * @param title
	 * @param status
	 * @throws IOException
	 * when POST request fails
	 */
	public void addBook(String author, String title, BookStatusVo status) throws IOException;
	/**
	 * Adds a book
	 * @param author
	 * @param title
	 * @param status
	 * @throws IOException
	 * when DELETE or POST request fails
	 */
	public void addBook(BookVo book) throws IOException;
	/**
	 * finds books with given author and title prefixes
	 * @param authors
	 * @param title
	 * @return
	 * list of books matching the search criteria
	 * @throws IOException
	 * when GET request fails
	 */
	List<BookVo> findBooks(String authors, String title) throws IOException;
}
