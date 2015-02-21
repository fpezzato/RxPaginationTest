package it.francescopezzato.android.rxpaginationtest;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by francesco on 21/02/2015.
 */
public enum DataService {
	DATA_SERVICE;

	private static final int PAGE_SIZE = 20;

	/**
	 * 	Very simple result generator
	 */
	public Observable<ResultPage> generateForPage(final Integer page) {
		Preconditions.checkState(page != null, "must have a page num!");

		int offset = page * PAGE_SIZE;

		return Observable.range(offset, offset + PAGE_SIZE)
			.collect(new Func0<List<Integer>>() {
				@Override
				public List<Integer> call() {
					return Lists.newArrayList();
				}
			}, new Action2<List<Integer>, Integer>() {
				@Override
				public void call(List<Integer> values, Integer value) {
					values.add(value);
				}
			}).map(new Func1<List<Integer>, List<String>>() {
				@Override
				public List<String> call(List<Integer> integers) {
					return Lists.transform(integers, new Function<Integer, String>() {
						@Override
						public String apply(Integer input) {
							return "Elem n. " + input;
						}
					});
				}
			}).map(new Func1<List<String>, ResultPage>() {
				@Override
				public ResultPage call(List<String> values) {
					return new ResultPage(values, page);
				}
			});

	}


}
