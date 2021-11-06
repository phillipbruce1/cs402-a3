# Assignment 3

Phillip Bruce CS 402

## Overview

This app is designed to be a simple, intuitive todo list. It was tested on a virtual Pixel 4 with
API level 30.

## Function

This app was built off of the previous todo list from assignment 2. The notable difference is this
this version progressively loads a preset list from a static JSON file on startup. It starts by
getting the size of the list, then creating a temporary list of "Loading..." entries as placeholders
until the rest of the items are received. Then, items are loaded from the JSON file in chunks of 10.

## Challenges

This project ended up being a bit more challenging than it should have been due to my
over-complicated design implemented in assignment 2. I ran into challenges trying to figure out how
to load the data into a list in one thread, then update the recycle view in the main thread. I
eventually got it to a point where it now works though.

## Notes/Bugs

When testing with a large list, the app will sometimes crash when using split/join on tasks not
visible on the screen. I was unfortunately unable to remedy this issue in time for submission.