package not.another.library.project.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BookVo {


	private Long id;
	private String title;
	private String authors;
	private BookStatusVo status;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public BookVo() {

	}

	public BookVo(Long id, String title, String authors, BookStatusVo status) {
		this.id = id;
		this.authors = authors;
		this.title = title;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BookStatusVo getStatus() {
		return status;
	}

	public void setStatus(BookStatusVo status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "BookVo [id=" + id + ", authors=" + authors + ", title=" + title + ", status=" + status.toString() + "]";
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

}
