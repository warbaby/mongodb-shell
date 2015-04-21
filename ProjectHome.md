# Overview #

The mongodb-shell application aims to be a handy and portable frontend for a [Mongo](http://www.mongodb.org) database written in java.

It comes with a connection manager, script editor, javascript engine compatible with default mongo shell and convenient query result presentation to allow you to work with your mongo database much more easily.

![http://i53.tinypic.com/sqs1sk.png](http://i53.tinypic.com/sqs1sk.png)

The UI layout is inspired by the [SQuirreL SQL](http://www.squirrelsql.org) with multiple db sessions support and historical query results as tabs.

It's written in java so it should run fine on any platform/os that support java runtime.

Initial release provides only the most useful subset of command available in default mongo shell (link to detailed list of available commands), but the list will be extended in the future releases.

# Getting Started #

## Running ##

Just double click the mongodb-shell-VERSION.jar.
In case it doesn't start up try `java -jar mongodb-shell-VERSION.jar`.

## First Steps ##

Make a connection to a database and start typing commands in the editor as you do in default mongo shell. For example.
```
use dummy;

db.mycol.save({a:1, b:"xyz"});

db.mycol.find();
```

To execute a command move the cursor to the line to be execute and press [ctrl+Enter]. The result of the command will be presented in the window below the editor.

You can try different commands by following the official [Mongo tutorial](http://www.mongodb.org/display/DOCS/Tutorial).

# Credits #

It uses the [MongoDB Rhino project](http://code.google.com/p/mongodb-rhino) for conversion to/from native JavaScript objects and MongoDB's BSON.

It uses the [Rhino](http://www.mozilla.org/rhino) as the JavaScript engine.

Syntax highlighter by http://ostermiller.org/syntax.

Icons by http://www.famfamfam.com.