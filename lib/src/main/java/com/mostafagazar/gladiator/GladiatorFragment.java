package com.mostafagazar.gladiator;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A slave fragment to execute commands in a background thread and deliver results back to the
 * correct fragment instance.
 *
 * @author Mostafa Gazar <mmegazar@gmail.com>
 */
public class GladiatorFragment<T extends BaseFragment, V> extends Fragment {

    public interface IOwner {
        ArrayList<String> getContractsIds();
    }

    public interface ICommand<V> {
        @WorkerThread
        V execute(Subscriber<? super V> subscriber);
    }

    public interface ICallback<T extends BaseFragment, V> {
        @UiThread
        void onNext(T context, V result);

        @UiThread
        void onError(T context, Throwable e);

        @UiThread
        void onComplete(T context);
    }

    private T mOwnerFragment;

    private ICommand<V> mCommand;
    private ICallback<T, V> mCallback;

    private Observable<V> mObservable;
    private Subscriber<V> mSubscriber;
    private Subscription mSubscription;

    private String mContractId;

    private boolean mIsFighting;

    private Object mLock = new Object();

    public static <T extends BaseFragment, V> GladiatorFragment newGladiator(@NonNull ICommand<V> command, ICallback<T, V> callback) {
        GladiatorFragment fragment = new GladiatorFragment();
        fragment.mCommand = command;
        fragment.mCallback = callback;

        return fragment;
    }

    public static <T extends BaseFragment, V> GladiatorFragment newGladiator(@NonNull Observable<V> observable, ICallback<T, V> callback) {
        GladiatorFragment fragment = new GladiatorFragment();
        fragment.mObservable = observable;
        fragment.mCallback = callback;

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
//        if (context instanceof IOwner) {
//            if (((IOwner) context).getContractId() == mContractId) {
//                mOwnerFragment = context;
//                return;
//            }
//        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        if (fragmentActivity != null) {
            List<Fragment> fragments = fragmentActivity.getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof IOwner &&
                        (((IOwner) fragment).getContractsIds()).contains(mContractId)) {
                    mOwnerFragment = (T) fragment;
                    break;
                }
            }
        }

        if (mOwnerFragment == null) {
            throw new ClassCastException(activity.toString()
                    + " must implement GladiatorFragment.IOwner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOwnerFragment = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    /**
     * Execute command in a background thread and delivers results back in main UI thread.
     *
     * @return Unique contract Id.
     */
    public String executeCommand() {
        if (mObservable == null) {
            mObservable = Observable.create(new Observable.OnSubscribe<V>() {
                @Override
                public void call(Subscriber<? super V> subscriber) {
                    subscriber.onNext(mCommand.execute(subscriber));
                    subscriber.onCompleted();
                }
            });
        }

        mSubscriber = new Subscriber<V>() {
            @Override
            public void onNext(V result) {
                if (mCallback != null) { mCallback.onNext(mOwnerFragment, result); }
            }

            @Override
            public void onError(Throwable e) {
                if (mCallback != null) { mCallback.onError(mOwnerFragment, e); }
            }

            @Override
            public void onCompleted() {
                if (mCallback != null) { mCallback.onComplete(mOwnerFragment); }
            }

        };

        mSubscription =
                mObservable
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                synchronized (mLock) {
                                    mIsFighting = true;
                                }
                            }
                        })
                        .doOnUnsubscribe(new Action0() {
                            @Override
                            public void call() {
                                synchronized (mLock) {
                                    mIsFighting = false;
                                }
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mSubscriber);

        mContractId = UUID.randomUUID().toString();
        return mContractId;
    }

    public boolean isFighting() {
        synchronized (mLock) {
            return mIsFighting;
        }
    }

    //    startFight()
}
