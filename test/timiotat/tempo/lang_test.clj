(ns timiotat.tempo.lang-test
  (:require [timiotat.tempo.lang :refer :all]
            [midje.sweet :refer :all]))

(facts "About: Fischer’s Timed Mutual Exclusion Algorithm"
       (let [fischer
             (automaton [l-check u-set]
                        (where (< u-set l-check)
                               (>= u-set 0)
                               (>= l-check 0))
                        (sig
                         (output :try i)
                         (output :crit i)
                         (output :exit i)
                         (output :rem i)
                         (intern :test i)
                         (intern :set i)
                         (intern :check i)
                         (intern :reset i))
                        (states
                         (state turn nil)
                         (state [:pc i] :pc/rem)
                         (state now 0)
                         (state [:last-set i] inf)
                         (state [:first-check i] 0))
                        (transitions
                         (output :try i
                                 (pre (= [:pc i] :pc/rem))
                                 (eff =>
                                      (= [:pc i] :pc/test)))
                         (intern :test i
                                 (pre (= [:pc i] :pc/test))
                                 (eff (= turn nil) =>
                                      (= [:pc i] :pc/set)
                                      (= [:last-set i] (+ now u-set))))
                         (intern :set i
                                 (pre (= [:pc i] :pc/test))
                                 (eff =>
                                      (= [:pc i] :pc/check)
                                      (= [:last-set i] inf)
                                      (= [:first-check i]) (+ now u-set)))
                         (intern :check i
                                 (pre (= [:pc i] :pc/check)
                                      (<= [:first-check i] now))
                                 (eff (= turn i) =>
                                      (= [:pc i] :pc/leave-try))
                                 (eff (!= turn i) =>
                                      (= [:pc i] :pc/test))
                                 (eff =>
                                      (= [:first-check i] 0)))
                         (output :crit i
                                 (pre (= [:pc i] :pc/leave-try))
                                 (eff =>
                                      (= [:pc i] :pc/crit)))
                         (output :exit i
                                 (pre (= [:pc i] :pc/crit))
                                 (eff =>
                                      (= [:pc i] :pc/reset)))
                         (intern :reset i
                                 (pre (= [:pc i] :pc/reset))
                                 (eff =>
                                      (= [:pc i] :pc/leave-exit)
                                      (= turn nil)))
                         (output :rem i
                                 (pre (= [:pc i] :pc/leave-exit))
                                 (eff =>
                                      (= [:pc i] :pc/rem))))
                        (trajectories
                         (traject traj
                                  (stop [(= [:last-set i] now)])
                                  (evolve [:delta now 1]))))]
         (fact "We have an automaton"
               (automaton? fischer) => true)))
