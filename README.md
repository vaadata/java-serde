# Java serde

A Burp extension and CLI to encode and decode Java Object Stream into a JSON representation.

## See also

* [Object Serialization Stream Protocol](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/protocol.html)

## Guide

### Usage

The Jar file can be used as a Burp extension, in which case a new tab appear on requests and responses having the 
`Content-Type` set to `application/x-java-serialized-object`. While using the extension it might be interesting to keep
an eye on the logs. It's not easy to transmit information about what happens to the user. So we're logging 
inconsistencies and errors there. If you see an empty tab it often means an error during decoding.

The application can also be used in the terminal it can either convert a Java Object Stream to JSON (decoding) or the 
other way around (encoding).

```shell
$ java -jar ./java-serde.jar decode < ./mystream.bin
{
  ...
}

$ java -jar ./java-serde.jar encode < ./mystream.json
[...BINARY STREAM...]
```

### Testing

When writing a new unit test, a Java file must be created to generate the stream. This ensures we can tweak the content
easily in the future. The target `all` in the Makefile is used to generate the stream binary files. This is an easy way
to ensure that they each corresponds to their Java generator file.

The last thing to do is to write the JSON file, in some case we can directly take its content from the test's error 
message.

When everything is ready we can run the tests using Gradle.

## Known limitations

* To use a reference, the object must be declared before in the stream.
* `TC_PROXYCLASSDESC` Is not implemented and could require a lot of work.
* `TC_RESET` Is not implemented but should be easy to do.
* `TC_EXCEPTION` Is not implemented and requires `TC_RESET`.
* For convenience, there are no string reference in the JSON document. This means that the re-encoding might differ if 
  the same string is written multiple times without reference. This is the case for example if you write two
  `new String` of the same string in a stream.

## Improvements

* Object of known type (such as `Date`, etc.) could be represented in a primitive form. The hard 
  part may be with classes inheriting them.
* Toggleable representation. If we decide to implement the custom representation for known types or string reference we
  should be able to toggle if it's a behavior we want or not.