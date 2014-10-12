(ns leiningen.lein-sass.ruby
  (:use [cemerick.pomegranate :only [add-dependencies]])
  (:import [org.jruby.embed ScriptingContainer LocalContextScope]
           [org.jruby RubyHash RubySymbol RubyArray]))

(def ^:private container (ScriptingContainer. LocalContextScope/THREADSAFE))
(def ^:private runtime (-> (.getProvider container) .getRuntime))

(defn make-rb-symbol [string]
  (RubySymbol/newSymbol runtime (name string)))

(defn make-rb-array [coll]
  (let [array (RubyArray/newArray runtime)]
    (doseq [v coll] (.add array v))
    array))

(defn make-rb-hash [clj-hash]
  (let [rb-hash (RubyHash. runtime)]
    (doseq [[k v] clj-hash]
      (let [key (make-rb-symbol k)
            value (if (coll? v) (make-rb-array v) (make-rb-symbol v))]
        (.put rb-hash key value)))
    rb-hash))

(def ^:private sass-options (make-rb-hash {
  :src-type :sass
  :style :compressed
  :load_paths ["./" "./"]
}))

(defn get-sass-options [] sass-options)

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

(defn run-ruby [scriptlet]
  (.runScriptlet container scriptlet))

(defn call-ruby-method
  ([object methodName returnType]
    (.callMethod container object methodName returnType))
  ([object methodName args returnType]
    (.callMethod container object methodName args returnType)))

(defn require-gem [gem-name]
  (run-ruby (str "require 'rubygems'; require '" (name gem-name) "';")))
