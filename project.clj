(defproject lein-sassy "1.0.1"
  :description "Use Sass with Clojure."
  :url "https://github.com/vladh/lein-sassy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :scm {:name "git"
        :url "https://github.com/vladh/lein-sassy"}

  :dependencies [[org.jruby/jruby-complete "1.7.16"]
                 [com.cemerick/pomegranate "0.2.0"]
                 [org.rubygems/haml "3.1.7"]
                 [org.rubygems/sass "3.2.1"]
                 [panoptic "0.2.1"]]

  :repositories [["gem-jars" "http://gemjars.org/maven"]
                 ["torquebox" "http://rubygems-proxy.torquebox.org/releases"]]

  :profiles {:example {:sass {:src "test/files-in"
                              :dst "test/files-out"}}}

  :eval-in-leiningen true)
