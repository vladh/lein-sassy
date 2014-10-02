(ns leiningen.lein-sass.renderer
  (:import [org.jruby.embed ScriptingContainer LocalContextScope]
           [org.jruby RubyHash RubySymbol RubyArray]))

(def ^:private container (ref nil))
(def ^:private runtime (ref nil))

(def ^:private rendering-engine (ref nil))
(def ^:private rendering-options (ref nil))

(defn- require-gem [gem-name]
  (.runScriptlet @container (str "require 'rubygems'; require '" (name gem-name) "';")))

(defn- make-rb-symbol [string]
  (RubySymbol/newSymbol @runtime (name string)))

(defn- make-rb-array [coll]
  (let [array (RubyArray/newArray @runtime)]
    (doseq [v coll] (.add array v))
    array))

(defn- make-rb-hash [clj-hash]
  (let [rb-hash (RubyHash. @runtime)]
    (doseq [[k v] clj-hash]
      (let [key (make-rb-symbol k)
            value (if (coll? v) (make-rb-array v) (make-rb-symbol v))]
        (.put rb-hash key value)))
    rb-hash))

(defn- ensure-engine-started! [options]
  (when-not @container
    (dosync
     (ref-set container (ScriptingContainer. LocalContextScope/THREADSAFE))
     (require-gem "sass")
     (ref-set runtime (-> (.getProvider @container) .getRuntime))
     (ref-set rendering-engine (.runScriptlet @container "Sass::Engine"))
     (ref-set rendering-options (make-rb-hash options)))))

(defn render [template]
  (try
    (let [args (to-array [template @rendering-options])
          engine (.callMethod @container @rendering-engine "new" args Object)]
      (.callMethod @container engine "render" String))
    (catch Exception e
      ;; ruby gem will print an error message
      (println "-> Compilation failed" ))))
