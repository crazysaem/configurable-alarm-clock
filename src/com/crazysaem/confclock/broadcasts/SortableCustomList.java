package com.crazysaem.confclock.broadcasts;

public class SortableCustomList implements Comparable<SortableCustomList> {
	private int sortBy=-1;
	private Object information = null;
	
	public SortableCustomList() {}	
	
	public SortableCustomList(int sortBy) {
		this();
		this.sortBy = sortBy;		
	}
	
	public SortableCustomList(int sortBy, Object information) {
		this.sortBy = sortBy;		
		this.information = information;
	}

	public int getSortBy() {
		return sortBy;
	}

	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}
	
	public Object getInformation() {
		return information;
	}

	public void setInformation(Object information) {
		this.information = information;
	}
	
	@Override
	public int compareTo(SortableCustomList sortableCustomList) {
		if(this.getSortBy()<sortableCustomList.getSortBy()) return -1;
		if(this.getSortBy()>sortableCustomList.getSortBy()) return 1;
		return 0;
	}
	
}
