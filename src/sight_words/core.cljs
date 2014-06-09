(ns sight-words.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))

(enable-console-print!)

(def app-state (atom {:current-word ["Welcome!"]
                      :current-idx [-1]
                      :status-icon [""]
                      :words ["play", "go", "my", "and", "see", "me", "I"]}))

; The flash-card component
(defn card-view [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/div
         nil
         (dom/h1 #js {:className "text-center"} (first (:current-word app)))
         (dom/span #js {:className (str "glyphicon " (first (:status-icon app)))})))))


(om/root card-view app-state
  {:target (. js/document (getElementById "card"))})

; Game logic
(defn game-start [app]
  (let
    [recog (js/webkitSpeechRecognition.)]

    (defn current-word []
      (nth (:words @app) (first (:current-idx @app))))

    (defn next-word []
      ; TODO use transact! or update!
      (swap! app assoc :status-icon [""])
      (swap! app assoc :current-idx [(inc (first (:current-idx @app)))])
      (swap! app assoc :current-word [(current-word)])
      (.start recog))

    (defn say [word]
      (let [synthesis (js/SpeechSynthesisUtterance. word)]
        (. js/speechSynthesis (speak synthesis) )
        (js/setTimeout next-word 2000)))

    (defn is-correct [results]
      (reduce
        (fn [flag result]
          (or flag (= result (current-word))))
        false
        (map
          (fn [i]
            (. (. results (item i)) -transcript))
          (range 0 (dec (. results -length))))))

    (defn retry [event]
      (. js/console (log event))
      (js/setTimeout (fn [] (.start recog)) 2000))

    (defn handle-result [event]
      (let [alternatives (. (. event -results) (item 0) )]
        (if (is-correct alternatives)
          (do (swap! app assoc :status-icon ["glyphicon-ok"])
              (js/setTimeout next-word 2000))
          (do (swap! app assoc :status-icon ["glyphicon-remove"])
              (say (current-word))))))
    (doto recog
      (aset "lang" "en-US")
      (aset "onresult" handle-result)
      (aset "onerror" retry)
      (aset "onnomatch" retry)
      (aset "continuous" false)
      (aset "maxAlternatives" 6))

    (next-word)))


; Game On!
(game-start app-state)
