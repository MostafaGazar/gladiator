package com.mostafagazar.samples.gladiator;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mostafagazar.gladiator.BaseFragment;
import com.mostafagazar.gladiator.GladiatorFragment;

import rx.Subscriber;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment {

    private static final String TAG_GENERIC_PROGRESS_DIALOG = "generic_progress_dialog";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        View button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                runOnBackgroundThread(new MyCommand(), new MyCallback());
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Toast.makeText(getContext(), "New fragment started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getContext(), "Fragment destroyed", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    private void showProgressDialog() {
        GenericProgressDialogFragment dialog = new GenericProgressDialogFragment();
        dialog.show(getFragmentManager(), TAG_GENERIC_PROGRESS_DIALOG);
    }

    private void hideProgressDailog() {
        DialogFragment dialogFragment = (DialogFragment) getFragmentManager().findFragmentByTag(TAG_GENERIC_PROGRESS_DIALOG);
        dialogFragment.dismiss();
    }

    private static class MyCommand implements GladiatorFragment.ICommand<Boolean> {
        @Override
        public Boolean execute(Subscriber<? super Boolean> subscriber) {
            try {
                // Some serious work in progress!
                Thread.sleep(5000);
                return true;
            } catch (InterruptedException e) {
                subscriber.onError(e);
                return false;
            }
        }
    }

    private static class MyCallback implements GladiatorFragment.ICallback<MainActivityFragment, Boolean> {
        @Override
        public void onNext(MainActivityFragment fragment, Boolean result) {
            Toast.makeText(fragment.getContext(), "onNext", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(MainActivityFragment fragment, Throwable e) {
            fragment.hideProgressDailog();
            Toast.makeText(fragment.getContext(), "onError", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(MainActivityFragment fragment) {
            fragment.hideProgressDailog();
            Toast.makeText(fragment.getContext(), "onComplete", Toast.LENGTH_SHORT).show();
        }
    }
}
