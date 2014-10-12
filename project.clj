(defproject lein-sass "0.1.0-SNAPSHOT"
  :description "Use Sass with Clojure. Includes support for Autoprefixer."
  :url "https://github.com/vladh/lein-sass"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.jruby/jruby-complete "1.7.16"]
                 [com.cemerick/pomegranate "0.2.0"]
                 [org.rubygems/haml "3.1.7"]
                 [org.rubygems/sass "3.2.1"]]

  :repositories [["gem-jars" "http://gemjars.org/maven"]
                 ["torquebox" "http://rubygems-proxy.torquebox.org/releases"]]

  :eval-in-leiningen true)
