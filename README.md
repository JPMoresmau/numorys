# Numorys

Numorys is (or, more accurately, will maybe be one day) a strict, monadic functional language, similar in many ways in Haskell.
Its main target is WebAssembly.

- strict: I've been bitten too many times by Haskell lazyness with space leaks, etc.
- monadic: I would like to not have the distinction between "simple" functions and "monadic" functions, since the Identity monad can be used in the first case, so have only one syntax
- functional: rely on functions, immutable data structures, etc.


This is mainly a toy project to play around with WebAssembly and have fun building a language.