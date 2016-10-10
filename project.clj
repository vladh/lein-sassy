(defproject lein-sassy "1.0.8"
  :description "Use Sass with Clojure."
  :url "https://github.com/vladh/lein-sassy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :scm {:name "git"
        :url "https://github.com/vladh/lein-sassy"}

  :dependencies [[org.jruby/jruby-complete "1.7.16"]
                 [com.cemerick/pomegranate "0.2.0"]
                 [panoptic "0.2.1"]
                 [me.raynes/fs "1.4.6"]]

  :profiles {:dev {:dependencies [[org.rubygems/sass "3.2.14"]]
                   :repositories [["gem-jars" "http://deux.gemjars.org"]]}
             :example {:sass {:src "test/files-in"
                              :dst "test/files-out"}}}

  :eval-in-leiningen true)
