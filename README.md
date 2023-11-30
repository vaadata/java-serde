### Questions

* When defining a new class we can set the superclass as a reference to a previously defined class.
  * Is it possible to make a cyclic dependency ? Leading to maybe DoS ?

## See also

* [Object Serialization Stream Protocol](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/protocol.html)

## Known limitations

* `TC_PROXYCLASSDESC` Is not implemented and could require a lot of work.
* `TC_RESET` Is not implemented but should be easy to do.
* `TC_EXCEPTION` Is not implemented and requires `TC_RESET`.
* For convenience, there are no string reference in the JSON document. This means that the re-encoding might differ if 
  the same string is written multiple times without reference. This is the case for example if you write two
  `new String` of the same string in a stream.

## Improvements

* Object of known type (such as `Double`, `Integer`, `Date`, etc.) could be represented in a primitive form. The hard 
  part may be with classes inheriting them.
* Toggleable representation. If we decide to implement the custom representation for known types or string reference we
  should be able to toggle if it's a behavior we want or not.