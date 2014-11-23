(ns leiningen.lein-sass.ruby
  (:require [cemerick.pomegranate :refer [add-dependencies]])
  (:import [org.jruby.embed ScriptingContainer LocalContextScope LocalVariableBehavior]
           [org.jruby RubyHash RubySymbol RubyArray]))

(defn make-container []
  "Creates a Ruby scripting container, currently as a SINGLETON This ensures
  that context is preserved across threads, but may lead to issues. THREADSAFE
  means that the context must be recreated for every thread which is not
  what we want."
  (ScriptingContainer. LocalContextScope/SINGLETON LocalVariableBehavior/PERSISTENT))

(defn make-runtime [container]
  (-> (.getProvider container) .getRuntime))

(defn make-rb-symbol [runtime string]
  (RubySymbol/newSymbol runtime (name string)))

(defn make-rb-array [runtime coll]
  (let [array (RubyArray/newArray runtime)]
    (doseq [v coll] (.add array v))
    array))

(defn make-rb-hash [runtime clj-hash]
  (let [rb-hash (RubyHash. runtime)]
    (doseq [[k v] clj-hash]
      (let [key (make-rb-symbol runtime k)
            value (if (coll? v) (make-rb-array v) (make-rb-symbol runtime v))]
        (.put rb-hash key value)))
    rb-hash))

(defn- download-gem-using-gemjars [gem-name gem-version]
  (let [gem-id (symbol (str "org.rubygems/" gem-name))]
    (try
      (add-dependencies :coordinates [[gem-id gem-version]]
                        :repositories (merge cemerick.pomegranate.aether/maven-central
                                             {"gem-jars" "http://gemjars.org/maven"}))
      (catch Exception e false))))

(defn- download-gem-using-torquebox [gem-name gem-version]
  (let [gem-id (symbol (str "rubygems/" gem-name))]
    (try
      (add-dependencies :coordinates [[gem-id gem-version :extension "gem"]]
                        :repositories (merge cemerick.pomegranate.aether/maven-central
                                             {"torquebox" "http://rubygems-proxy.torquebox.org/releases"}))
      (catch Exception e
        (println (.getMessage e))
        false))))

(defn install-gem [gem-name gem-version]
  (if (or
       (download-gem-using-gemjars gem-name gem-version)
       (download-gem-using-torquebox gem-name gem-version))
    true
    false))

(defn run-ruby [container scriptlet]
  (.runScriptlet container scriptlet))

(defn call-ruby-method
  ([container object methodName returnType]
    (.callMethod container object methodName returnType))
  ([container object methodName args returnType]
    (.callMethod container object methodName args returnType)))

(defn require-gem [container gem-name]
  (run-ruby container (str "require 'rubygems'; require '" (name gem-name) "';")))
