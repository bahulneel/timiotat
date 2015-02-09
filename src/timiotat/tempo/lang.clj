(ns timiotat.tempo.lang
  (:require [clojure.core.logic :as cl]))

(def +inf Double/POSITIVE_INFINITY)
(def -inf Double/NEGATIVE_INFINITY)

(declare init-state)

(defrecord Automata [state])

(defrecord Automaton [args where state trans traj invar]
  clojure.lang.IFn
  (invoke [_ arg-list]
    (let [in (count arg-list)
          req (count args)]
      (assert (= in req)
              (str "Incorrect number of args " in " != " req)))
    (let [a (zipmap args arg-list)
          s (init-state state)]
      (->Automata (merge a s)))))

(defn automaton
  [def]
  (map->Automaton def))

(defn automaton?
  [a]
  (instance? Automaton a))

(defn init-state
  [s]
  (let [parse-v (fn [v]
                  (if (symbol? v)
                    (if-let [v (resolve v)]
                      (if (var? v)
                        (var-get v)
                        v)
                      (cl/lvar v))
                    v))]
    (into {} (map (fn [[k & vs]]
                    (let [vs (map parse-v vs)]
                      [k vs]))
                  s))))

(defn state
  [a k & xs]
  (let [[v & vs] (get-in a [:state k])]
    (reduce (fn [cv [x v]]
              (cond
               (= cv x) v
               (cl/lvar? cv) v
               :else ::nil))
            v
            (zipmap xs vs))))
