Bop App Design Project
===

# Bop

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Music app with accounts for all users. User can search up songs, artists, and albums to listen to. Each artist will have all their albums and each album will have all its songs that premium users can play. User can like songs and create playlists. There is a default playlist that is created at account creation where the liked songs get added. User can discover new music on the Nearby Users tab where it shows what other users are listening to on Bop. 

## Product Spec

### 1. User Stories

* User can log into account
* User can create an account
* User sees list of new releases on search screen if nothing has been searched
* User can search for songs
* User can play song
* User can search for artist 
* User can search for album
* User can see an artist details page with all of artists albums
* User can see an album details page with all of the songs in that album
* User can create playlists 
* User can double-tap to like songs which automatically get added to default Favorite Songs Playlist
* User can scroll through their playlists
* User can scroll through songs in their playlists
* User can view their profile

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
   * User can double-tap to like songs which automatically get added to default Favorite Songs Playlist
* Details
   * User can see an artist details page with all of artists albums
   * User can see an album details page with all of the songs in that album
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
* Stream - Nearby User
* Profile

**Flow Navigation** (Screen to Screen)

* Login
   * Stream (forward)
* Stream (2)
   * Profile stream or or Nearby Users stream or Search stream (switch)
   * Details (forward)
      * songs, artists, albums, and playlists
* Details
   * Stream (back to whichever one called details)
* Creation (2)
   *  Profile Stream from Playlist creation
   *  Playlist creation is more of a modal overlay
   *  Search Stream from Account Creation

## Wireframes
![wireframes](https://user-images.githubusercontent.com/73396101/125350477-65223300-e324-11eb-9e90-ffb92a1ac8fe.jpeg)

## Schema 
### Models
![models](https://user-images.githubusercontent.com/73396101/125358208-3c06a000-e32e-11eb-9dfb-ff319da84967.jpeg)

### Networking
- ![Networks and Database Calls](https://user-images.githubusercontent.com/73396101/125350403-520f6300-e324-11eb-8448-1ecf98a2de34.jpeg)

## Demos
### Premium Bop: 
![Premium Bop](https://github.com/FatemaNeemuch/Bop/blob/main/BopPremiumWalkthrough.gif)

### Free Bop: 
![Free Bop](https://github.com/FatemaNeemuch/Bop/blob/main/BopWalkthroughFree.gif)


