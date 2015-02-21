package it.francescopezzato.android.rxpaginationtest;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by francesco on 21/02/2015.
 */
public class FragmentItems extends ListFragment {


	private PublishSubject<NeedMoreData> mNextPageReq = PublishSubject.create();

	Integer mNextPage = 0;
	Boolean mLoading = false;

	List<String> mItems = Lists.newArrayList();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
			android.R.layout.simple_list_item_1, mItems);

		setListAdapter(adapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount - visibleItemCount < firstVisibleItem + 9) {
					mNextPageReq.onNext(new NeedMoreData());
				}
			}
		});

		/**
		 * do NOT use the following code anywhere! It's intended to be a simple example to support a question online.
		 * There is no lifecycle bin, unsub and and a lot of other necessary stuff ;)
		 */

		mNextPageReq
			.filter(new Func1<NeedMoreData, Boolean>() {
				@Override
				public Boolean call(NeedMoreData needDataEvent) {
					return !mLoading;
				}
			})
			.map(new Func1<NeedMoreData, NeedMoreData>() {
				@Override
				public NeedMoreData call(NeedMoreData needDataEvent) {
					mLoading = true;
					return needDataEvent;
				}
			})
			.subscribe(new Action1<NeedMoreData>() {
				@Override
				public void call(NeedMoreData nextPageEvent) {

					Log.i("->", "mNextPage : " + mNextPage.toString());
					DataService.DATA_SERVICE.generateForPage(mNextPage)

						.finallyDo(new Action0() {
							@Override
							public void call() {
								mLoading = false;
							}
						})
						.subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<ResultPage>() {
							           @Override
							           public void onCompleted() {
								           //nop, see the "finallyDo"
							           }

							           @Override
							           public void onError(Throwable e) {
								           Toast.makeText(getActivity(), MoreObjects.firstNonNull(e.getMessage(), "error  during req."), Toast.LENGTH_SHORT).show();
							           }

							           @Override
							           public void onNext(ResultPage resultPage) {
								           mNextPage = resultPage.getPage() + 1;

								           mItems.addAll(resultPage.getItems());

								           final ArrayAdapter adapter = ((ArrayAdapter) getListAdapter());

								           adapter.notifyDataSetChanged();
							           }
						           }


						);
				}
			});


	}

}
