package com.mostafagazar.gladiator;

import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;

/*
 * If you have your own BaseFragment, you can just copy the contents of this Fragment into yours.
 *
 * @author Mostafa Gazar <mmegazar@gmail.com>
 */
public abstract class BaseFragment extends Fragment implements GladiatorFragment.IOwner {

    private static final String TAG_GLADIATOR_FRAGMENT = "gladiator_fragment";

    @State
    ArrayList<String> mContractsIds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @WorkerThread
    public final <T extends BaseFragment, V> void runOnBackgroundThread(GladiatorFragment.ICommand<V> command,
                                         GladiatorFragment.ICallback<T, V> callback) {

        FragmentManager fragmentManager = getFragmentManager();
        GladiatorFragment gladiatorFragment = (GladiatorFragment) fragmentManager.findFragmentByTag(TAG_GLADIATOR_FRAGMENT);
        if (gladiatorFragment == null || gladiatorFragment.isFighting()) {
            gladiatorFragment = GladiatorFragment.newGladiator(command, callback);
            fragmentManager.beginTransaction().add(gladiatorFragment, TAG_GLADIATOR_FRAGMENT).commit();
        }

        String contractId = gladiatorFragment.executeCommand();
        mContractsIds.add(contractId);
    }

    @Override
    public ArrayList<String> getContractsIds() {
        return mContractsIds;
    }

}
