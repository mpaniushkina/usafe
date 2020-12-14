package com.covrsecurity.io.inject.module;

import com.covrsecurity.io.ui.fragment.authorized.AboutFragment;
import com.covrsecurity.io.ui.fragment.authorized.AddConnectionFragment;
import com.covrsecurity.io.ui.fragment.authorized.HelpFragment;
import com.covrsecurity.io.ui.fragment.authorized.HistoryDetailsFragment;
import com.covrsecurity.io.ui.fragment.authorized.HistoryFragment;
import com.covrsecurity.io.ui.fragment.authorized.LockScreenFragment;
import com.covrsecurity.io.ui.fragment.authorized.MyAccountFragment;
import com.covrsecurity.io.ui.fragment.authorized.PartnershipDetailsFragment;
import com.covrsecurity.io.ui.fragment.authorized.PartnershipFragment;
import com.covrsecurity.io.ui.fragment.authorized.SettingsFragment;
import com.covrsecurity.io.ui.fragment.authorized.StandingByFragment;
import com.covrsecurity.io.ui.fragment.authorized.codechange.ChangeCodeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AuthorizedFragmentModule {

    @ContributesAndroidInjector
    abstract LockScreenFragment contributeLockScreenFragment();

    @ContributesAndroidInjector
    abstract StandingByFragment contributeStandingByFragment();

    @ContributesAndroidInjector
    abstract HistoryFragment contributeHistoryFragment();

    @ContributesAndroidInjector
    abstract HistoryDetailsFragment contributeHistoryDetailsFragment();

    @ContributesAndroidInjector
    abstract PartnershipFragment contributePartnershipFragment();

    @ContributesAndroidInjector
    abstract PartnershipDetailsFragment contributePartnershipDetailsFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();

    @ContributesAndroidInjector
    abstract ChangeCodeFragment contributeChangeCodeFragment();

    @ContributesAndroidInjector
    abstract HelpFragment contributeHelpFragment();

    @ContributesAndroidInjector
    abstract AboutFragment contributeAboutFragment();

    @ContributesAndroidInjector
    abstract AddConnectionFragment contributeAddConnectionFragment();

    @ContributesAndroidInjector
    abstract MyAccountFragment contributeMyAccountFragment();

    /*
        AddPartnershipFragment
        ConnectionEstablishedFragment
        FingerprintErrorMessageFragment
        FingerprintReadMoreChildFragment
        LockedScreenFragment
        NavigationDrawerFragment
        ChangeCodeInfoFragment
        MyAccountFragment
     */
}