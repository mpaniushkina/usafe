package com.covrsecurity.io.inject.module;

import com.covrsecurity.io.ui.fragment.unauthorized.CreatePersonalCodeFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.DoneSetupFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.EnterVerificationCodeFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.GreetingFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.InfoEnterCodeFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.PhoneNumberFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ReadMorePhoneFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.RecoveryQrCodeFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.RegisterBiometricRecoveryFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.ScanFaceBiometricsFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.SplashFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.TutorialFragment;
import com.covrsecurity.io.ui.fragment.unauthorized.UseFingerprintAuthFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class UnauthorizedFragmentModule {

    @ContributesAndroidInjector
    abstract SplashFragment contributeSplashFragment();

    @ContributesAndroidInjector
    abstract TutorialFragment contributeTutorialFragment();

    @ContributesAndroidInjector
    abstract RecoveryQrCodeFragment contributeRecoveryQrCodeFragment();

    @ContributesAndroidInjector
    abstract RegisterBiometricRecoveryFragment contributeRegisterBiometricRecoveryFragment();

    @ContributesAndroidInjector
    abstract GreetingFragment contributeGreetingFragment();

    @ContributesAndroidInjector
    abstract ReadMorePhoneFragment contributeReadMorePhoneFragment();

    @ContributesAndroidInjector
    abstract PhoneNumberFragment contributePhoneNumberFragment();

    @ContributesAndroidInjector
    abstract EnterVerificationCodeFragment contributeEnterVerificationCodeFragment();

    @ContributesAndroidInjector
    abstract InfoEnterCodeFragment contributeInfoEnterCodeFragment();

    @ContributesAndroidInjector
    abstract CreatePersonalCodeFragment contributeCreatePersonalCodeFragment();

    @ContributesAndroidInjector
    abstract UseFingerprintAuthFragment contributeUseFingerprintAuthFragment();

    @ContributesAndroidInjector
    abstract DoneSetupFragment contributeDoneSetupFragment();

    @ContributesAndroidInjector
    abstract ScanFaceBiometricsFragment contributeScanFaceBiometricsFragment();
}