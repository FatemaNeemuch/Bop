Original App Design Project - README Template
===

# PersonalApp- Name TBD

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Music app with accounts for all users. User can search up songs, artists, and albums to listen to. Each artist will have all their albums, each album will have all their songs, and each song will have the artist name, album name, lyrics, release date (year), album cover, and Youtube link to view music video (if it exists). User can like songs and create playlists. User can share the song, album, or artist with other users of the app using the username and in-app messaging or by copying the sharing link. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Music
- **Mobile:** Mobile only
- **Story:** Allows the user to search and listen to music using the Spotify API for free. 
- **Market:** Everyone who listens to music
- **Habit:** can create playlists to come back and listen to attached to their account. Additionally, can learn all about a song in once location while listening to it
- **Scope:** Starts with just being able to search and play songs. Adds categorization based on artists and based on albums. Adds song details. Adds ability to create playlist and like songs. Adds Youtube music video. Adds song sharing ability. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can log into account
* User can create an account
* User can view their profile
   * User can use camera for theri profile pic 
* User sees list of new releases on search screen if nothing has been searched
* User can search for songs
* User can play song
* User can search for artist 
* User can search for album
* User can see an artist details page with all of artists albums
* User can see an album details page with all of the songs in that album, album release date (year), and album cover
* User can see song details page with song name, album cover, artist name, album name, lyrics, release date (year)
* User can create playlists 
* User can double-tap to like songs which automatically get added to default Favorite Songs Playlist
* User can scroll through their playlists
   * Cardview + animation
* User can scroll through songs in their playlists 
    * User can see the songs in the playlist details page

**Optional Nice-to-have Stories**

* Placeholder images
* Implement view binding library to reduce boiler plate
* User can play Youtube music video
* User can share song, album, or artist by copying link
* User can share song, album, or artist in app with other users

### 2. Screen Archetypes

* Login
   * User can log into account
* Stream (2 of these- one for search and one for playlist)
   * User sees list of new releases on search screen if nothing has been searched
   * User can scroll through their playlists
   * User can scroll through songs in their playlists
   * User can search for songs
   * User can search for artist 
   * User can search for album
   * User can play song by tapping it
   * There are two tabs on the main activity- Search and Playlists
   * User can double-tap to like songs which automatically get added to default Favorite Songs Playlist
* Details
   * User can see song details page with song name, album cover, artist name, album name, lyrics, release date (year)
   * User can see an artist details page with all of artists albums
   * User can see an album details page with all of the songs in that album, album release date (year), and album cover
   * User can see the songs in the playlist details page
   * User can view profile
* Creation (2)
   *  User can create playlists 
   *  User can create an account
* Profile
    * User can view their profile


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream - Search
* Stream - Playlist
* Profile

**Flow Navigation** (Screen to Screen)

* Login
   * Stream (forward)
* Stream (2)
   * Playlist stream or Search stream (switch)
   * Details (forward)
       * songs, artist, albums, and playlist
* Details
   * Stream (back to whichever one called details (finish()))
* Creation (2)
   *  Playlist Stream from Playlist creation
   *  Playlist creation is more of a modal overlay
   *  Search Stream from Account Creation

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
