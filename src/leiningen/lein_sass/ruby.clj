(ns leiningen.lein-sass.ruby
  (:use [cemerick.pomegranate :only (add-dependencies)])
  (:import [org.jruby.embed ScriptingContainer LocalContextScope]
           [org.jruby RubyHash RubySymbol RubyArray]))

(def ^:private container (ScriptingContainer. LocalContextScope/THREADSAFE))
(def ^:private runtime (-> (.getProvider container) .getRuntime))

(defn- download-gem-using-gemjars [gem-name gem-version]
  (let [gem-id (symbol (str "org.rubygems/" gem-name))]
    (try
      (add-dependencies :coordinates [[gem-id gem-version]]
                        :repositories (merge cemerick.pomegranate.aether/maven-central
                                             {"gem-jars" "http://deux.gemjars.org"}))
      (catch Exception e
        (do (println (.getMessage e))
            false)))))

(defn ensure-gem-installed! [options]
  (let [gem-name (:gem-name options)
        gem-version (:gem-version options)]
    (download-gem-using-gemjars gem-name gem-version)))

(defn run-ruby [scriptlet]
  (.runScriptlet container scriptlet))

(defn call-ruby-method [object & args]
  (.callMethod container object args))

(defn require-gem [gem-name]
  (run-ruby (str "require 'rubygems'; require '" (name gem-name) "';")))

(defn- make-rb-symbol [string]
  (RubySymbol/newSymbol runtime (name string)))

(defn- make-rb-array [coll]
  (let [array (RubyArray/newArray runtime)]
    (doseq [v coll] (.add array v))
    array))

(defn- make-rb-hash [clj-hash]
  (let [rb-hash (RubyHash. runtime)]
    (doseq [[k v] clj-hash]
      (let [key (make-rb-symbol k)
            value (if (coll? v) (make-rb-array v) (make-rb-symbol v))]
        (.put rb-hash key value)))
    rb-hash))
