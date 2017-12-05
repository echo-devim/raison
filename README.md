# Raison
Raison is an Android service that enables the **raise to wake up** feature.

This means that if Raison notices that you raised your phone from the table, Raison will wake up the phone. The service will run at startup and should run all the time checking every 3 seconds (*to save battery*) the position of your smartphone. When the smartphone changes from horizontal to an inclined position (not completely vertical) it will wake up.

The application should work for Android 5 (Lollipop) or higher.

**ATTENTION:** Due to different driver implementations (depend on the vendor), often the accelerometer (needed by this app) is disabled in sleep mode. Read [here](https://stackoverflow.com/questions/9982433/android-accelerometer-not-working-when-screen-is-turned-off) for more details.

#### License
GPLv3
