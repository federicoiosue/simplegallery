# Simplegallery Library

## Include this library as dependency

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.federicoiosue:simplegallery:3.0.1'
	}
  
That's it!

## Install into local repository

```
./gradlew clean install
```

