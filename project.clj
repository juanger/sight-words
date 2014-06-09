(defproject sight-words "0.1.0-SNAPSHOT"
  :description "Sight words with flash cards. Web speech API powered."
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.5.0"]]

  :plugins [[lein-cljsbuild "1.0.2"]
            [lein-resource "0.3.6"]]

  :hooks [leiningen.cljsbuild leiningen.resource]

  :source-paths ["src"]

  :cljsbuild {
    :builds {:dev {:source-paths ["src"]
                   :compiler {
                     :output-to "_deploy/sight_words.debug.js"
                     :output-dir "_deploy/debug/out"
                     :optimizations :none
                     :pretty-print true
                     :source-map true}},
             :prod {:source-paths ["src"]
                   :compiler {
                     :output-to "_deploy/sight_words.js"
                     :output-dir "_deploy/out"
                     :optimizations :advanced
                     :pretty-print false
                     :preamble ["react/react.min.js"]
                     :externs ["react/externs/react.js"]}}}}
  :resource {
    :resource-paths ["src-resources"] ;; required or does nothing
    :target-path "_deploy" ;; optional default to the global one
    :update   false      ;; if true only process files with src newer than dest
    :skip-stencil [ #".*" ] ;; optionally skip stencil processing - default is an empty vector
})
