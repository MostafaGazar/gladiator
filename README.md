## gladiator

A slave fragment to execute commands in a background thread and deliver results back to the correct fragment instance.

It is based on RxAndroid, so there is potential to grow with with it.

[![Build Status](https://travis-ci.org/MostafaGazar/gladiator.svg)](https://travis-ci.org/MostafaGazar/gladiator)
[![PayPal Donations](https://img.shields.io/badge/paypal-donate-yellow.svg?style=flat)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=mmegazar%40gmail%2ecom&lc=NZ&item_name=Mostafa%20Gazar&item_number=GitHub&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)

### Usage

1. Extend from `com.mostafagazar.gladiator.BaseFragment` or copy it is basic content into your own BaseFragment
2. Call `runOnBackgroundThread` and be certain that it is going to return the result back to the correct fragment, if the calling fragment got destroyed because of device orientation change for example.

Complete example:
```java
runOnBackgroundThread(new MyCommand(), new MyCallback());

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
```

### Download

Add the `gladiator` dependency to your `build.gradle` file:

[![Maven Central](https://img.shields.io/maven-central/v/com.mostafagazar/gladiator.svg)](http://search.maven.org/#search%7Cga%7C1%7Cgladiator)
```groovy
dependencies {
    ...
    compile 'com.mostafagazar:gladiator:1.0.0'
    ...
}
```

### Proguard

If you're using proguard for code shrinking and obfuscation, make sure to add the following:
```proguard
   -keep class com.mostafagazar.** { *; }
```

### Developed by

* Mostafa Gazar - <mmegazar@gmail.com>

### License

    Copyright 2016 Mostafa Gazar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
### Donations

If you'd like to support this library, you could make a donation here:

[![PayPal Donation](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=mmegazar%40gmail%2ecom&lc=NZ&item_name=Mostafa%20Gazar&item_number=GitHub&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)
