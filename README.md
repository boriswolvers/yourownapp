## boriswolvers-pset6
### Your own app
Last app of the course: Native App Studio. The main assignments were to make use of an open data API and use Firebase to save specific user data. As a base of my app I have chosen for the Rijksmuseum API. With this app users can search for artworks of the museum. Each artwork of the museum can be retrieved with some additional information. If the user likes some specific artworks, they can save it into their favorites list.

### Use of API
The mainscreen of the app contains a recyclerviewer with cardviews in it, which can be seen in the pictures down below. These cardviews are retrieved with the use of an url and an asynctask. Users can scroll 'infinitely' because more artworks will be retrieved after every ten artworks. 
Below the recyclerviewer there is a search bar. Users can find artworks by filling in the search bar, the results will be fetched into the same recyclerview in the mainscreen. When a user clicks on a artwork they will be sent to another activity where they can find some additional information of the artwork. The additional information will also be retrieved with the use of a url and a asynctask.

### Use of Firebase
First encounter of Firebase is when the app launches, namely the log in screen (see pictures below). Users can sign in with their Google account. A connection is made to Firebase where the user is listed. 
The second usage of Firebase is of course the database. When a user want to add their artwork to their favorites it will be saved into the real time database. For each individual user their favorites will be saved seperately.

### Use of sharedpreference to save small amount of data
When a user signs in, their name of his/her Google account will be saved and used as a welcome message in the main screen. 

#### The UI of the app
![alt text](https://github.com/boriswolvers/yourownapp/blob/master/doc/ui1.png "UI of rijksapp")

#### Snapshot of the realtime database
![alt text](https://github.com/boriswolvers/yourownapp/blob/master/doc/firebasedatabase.png "Realtime database")

