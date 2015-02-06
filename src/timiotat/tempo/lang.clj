(ns timiotat.tempo.lang)

(def +inf Double/POSITIVE_INFINITY)
(def -inf Double/NEGATIVE_INFINITY)

(defrecord Automaton [])

(defn automaton
  [args & body]
  (->Automaton))

(defn automaton?
  [a]
  (instance? Automaton a))

(defn state
  [a v]
  nil)
