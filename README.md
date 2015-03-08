# lein-sassy
Use Sass with Clojure.

* Suports both Sass (indent-based) and SCSS (regular) syntax
* Watches for file changes

## Prerequisites
lein-sassy uses JRuby, so you need to have Ruby installed. You probably already
do, though. JRuby is also why the plugin should only be used as a development
dependency, since you would always depend on it otherwise.

## Usage
Add the plugin to your `project.clj`, using the appropriate version:

```clj
(defproject narwhal-endearment-manager "0.1.0-SNAPSHOT"
  :plugins [[lein-haml-sass "version-from-below-goes-here"]])
```

[![Clojars Project](http://clojars.org/lein-sassy/latest-version.svg)](http://clojars.org/lein-sassy)

Then add the following, using your CSS folders as appropriate.
```clojure
:sass {:src "resources/app/stylesheets"
       :dst "resources/public/stylesheets"}
```

Finally, run `lein deps` to download everything.

To compile files once, use `lein sass once`.

To watch files for changes, use `lein sass watch`.

## Options
The sass section in `project.clj` takes various options:

* `:src`: The source folder, defaults to "resources/public/stylesheets"
* `:dst`: The destination folder, defaults to "resources/app/stylesheets"
* `:gem-name`: Defaults to "sass"
* `:gem-version`: Defaults to "3.2.14"
* `:style`: [`:nested`|`:expanded`|`:compact`] Output style. Defaults to
`:nested`.
* `:syntax`: [`:sass`|`:scss`] Force the syntax, which is normally inferred
from the file extension. Not present in defaults.

## What to do if it doesn't work
If you have any trouble using lein-sassy, especially if there are any Ruby
issues, please [open an issue](https://github.com/vladh/lein-sassy/issues/new).
This helps me out a lot, as I can get feedback from people using various
platforms in order to make lein-sassy better.

## To do
* Add Autoprefixer support.
* Add more integration tests.

## Credits and License
This plugin was greatly inspired by
[lein-haml-sass](https://github.com/rtircher/lein-haml-sass).

Copyright Vlad-Ștefan Harbuz and distributed under the Eclipse Public
License, the same as Clojure.
