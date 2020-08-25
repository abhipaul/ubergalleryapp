# Image processing App for Android
Based on Image cache and persistance storage.
## Understanding the Code.

* Recuycler view using GridLayoutManager with three column of images.
* Used Pagination cocept on scroll of recycler view.
* Used Retrofit API to fetch data from flickr API, we can use simple HTTPURLConnection to fecth data.
* Stored data in model class and fetch API to get bitmap image and set in the Image View.
* Used Persistance storage and Cache to store Bitmap object to avoid repeated service call and save data.

## Known Bugs:

* There is small flickering of Images while scrolling fast along the list.
* The progress bar still shows even when the data loads, user has to scroll the app manualy.

# Things to be complete
* Unit testing is pending.
* Minor fixes of bugs required to go into production.
Testing
