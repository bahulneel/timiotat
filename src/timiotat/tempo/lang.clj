(ns timiotat.tempo.lang)

(def +inf Double/POSITIVE_INFINITY)
(def -inf Double/NEGATIVE_INFINITY)

(defrecord Automaton [])

(defmacro automaton
  [& args]
  `(->Automaton))

(defn automaton?
  [a]
  (instance? Automaton a))
