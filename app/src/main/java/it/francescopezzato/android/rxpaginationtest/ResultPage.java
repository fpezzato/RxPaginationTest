package it.francescopezzato.android.rxpaginationtest;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by francesco on 21/02/2015.
 */
public class ResultPage {

	private final List<String> mItems;
	private final Integer mPage;

	public ResultPage(List<String> items, Integer page) {
		this.mItems = ImmutableList.copyOf(items);
		this.mPage = page;
	}

	public List<String> getItems() {
		return mItems;
	}

	public Integer getPage() {
		return mPage;
	}
}
