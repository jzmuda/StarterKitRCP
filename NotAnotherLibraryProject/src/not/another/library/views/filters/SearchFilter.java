package not.another.library.views.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import not.another.library.project.data.BookVo;


public class SearchFilter extends ViewerFilter {

	private String authorContains;
	private String titleContains;

	public SearchFilter(String authorContains, String titleContains) {
		this.authorContains =".*" + unNull(authorContains).toLowerCase() + ".*";
		this.titleContains = ".*" + unNull(titleContains).toLowerCase() + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		BookVo book = (BookVo) element;
		if (book.getAuthors().toLowerCase().matches(authorContains)
				&& book.getTitle().toLowerCase().matches(titleContains))
			return true;
		return false;
	}

	public String getAuthorPrefix() {
		return authorContains;
	}

	public void setAuthorPrefix(String authorPrefix) {
		this.authorContains = ".*" + unNull(authorPrefix).toLowerCase() + ".*";
	}

	public String getTitlePrefix() {
		return titleContains;
	}

	public void setTitlePrefix(String titlePrefix) {
		this.titleContains = ".*" + unNull(titlePrefix).toLowerCase() + ".*";
	}
	
	public String unNull(String text) {
		if(text == null){
			return "";
		}
		else return text;
	}

}

