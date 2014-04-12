AndroidPullToPong
=================

Pull-to-refresh for Android that plays pong

Installation
------------

While I learn more about how to use Gradle to make this more painless, here's the process:

1. Install the [ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh) library, following the instructions in its README
2. Include the files under ./PullToPong into their respective directories in your project

Usage
-----

First, follow the instructions from the [ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh) library to set up the pull behavior how you'd like it.

Once that's set up, configure your PullToRefreshLayout to use the PongHeaderTransformer and pong_header.xml for its layout:


```java
mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
ActionBarPullToRefresh.from(this)
  .options(Options.create()
    .headerLayout(R.layout.pong_header)
    .headerTransformer(new PongHeaderTransformer())
    .build())
  .allChildrenArePullable()
  .listener(this)
  .setup(mPullToRefreshLayout);
```


Gotchas
-------

To set the background of the header when Pong is being played, you need to go into pong_header.xml and set `android:background` manually. I've tried many things to get it to infer the background, so if you have any thoughts, that'd be awesome.


Known Issues/To Do
------------------

You have to manually specify the background color, it doesn't infer it
Make it work with Gradle/Maven to make using the library easier
Develop the library a bi to make it more user friendly, maybe hide the Android-PullToRefresh stuff
