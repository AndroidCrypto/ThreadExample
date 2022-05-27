# ThreadExample

This app shows how to use a **Thread** to avoid blocking the (main) UI thread while running 
long lasting operations.

When performing encryption tasks there is a high chance that you want to derive an encryption key 
using a **PBKDF2** function ("password based key derivation function") because the main goal is 
to make a brute force attack taking to much time.

When doing the key derivation on the main thread the UI is completely blocked while using 
a background task let the UI usable.

This app is showing both methods running the key derivation with 100000 iterations - this can 
take some seconds on older Android devices so be carefull when using the first button 
"run PBKDF2 without thread (UI blocking)".

The app is created with Andoid SDK 32 (Android 12) and runnable on SDK 23+.

