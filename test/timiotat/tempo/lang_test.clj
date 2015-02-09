(ns timiotat.tempo.lang-test
  (:require [timiotat.tempo.lang :refer :all]
            [midje.sweet :refer :all]))

(facts "About: Fischer’s Timed Mutual Exclusion Algorithm"
       (let [fischer
             (automaton '{:args [l-check u-set]
                          :where [(< u-set l-check)
                                  (>= u-set 0)
                                  (>= l-check 0)]
                          :state [(proc i)
                                  (turn nil)
                                  (pc i :pc/rem)
                                  (now 0)
                                  (last-set i +inf)
                                  (first-check i 0)]
                          :trans [[(<try i)
                                   [(proc i)
                                    (pc i :pc/rem)
                                    (pc' i :pc/test)]]
                                  [(=test i)
                                   [(proc i)
                                    (pc i :pc/test)
                                    (turn nil)
                                    (pc' i :pc/set)
                                    (now ?n)
                                    (last-set' i (+ ?n u-set))]]
                                  [(=set i)
                                   [(proc i)
                                    (pc i :pc/test)
                                    (pc' i :pc/check)
                                    (last-set' i +inf)
                                    (now ?n)
                                    (first-check' i (+ ?n u-set))]]
                                  [(=check i)
                                   [(proc i)
                                    (:pc i :pc/check)
                                    (first-check i ?t)
                                    (<= ?t now)
                                    (turn ?p)
                                    #{[(= i ?p) (pc' i :pc/leave-try)]
                                      [(!= i ?p) (pc' i :pc/test)]}
                                    (first-check' i 0)]]
                                  [(<crit i)
                                   [(proc i)
                                    (pc i :pc/leave-try)
                                    (pc' i :pc/crit)]]
                                  [(<exit i)
                                   [(proc i)
                                    (pc i :pc/crit)
                                    (pc' i :pc/reset)]]
                                  [(=reset i)
                                   [(proc i)
                                    (pc i :pc/reset)
                                    (pc' i :pc/leave-exit)
                                    (turn' nil)]]
                                  [(<rem i)
                                   [(proc i)
                                    (pc i :pc/leave-exit)
                                    (pc' i :pc/rem)]]]
                          :traj [(traj :clock
                                       [(proc i)
                                        (last-set i now)]
                                       [(delta now now' 1)])]
                          :invar [(proc i)
                                  (proc j)
                                  (!= i j)
                                  (pc i ?pc-i)
                                  (!= ?pc-i :pc/crit)
                                  (pc j ?pc-j)
                                  (!= ?pc-j :pc/crit)]})]
         (fact "We have an automaton"
               (automaton? fischer) => true)
         (fact "Can initialise an instance"
               (fischer [1]) => throws
               (fischer [1 2]) => anything
               (fischer [1 2 3]) => throws)
         (fact "The initial state is correct"
               (let [f (fischer [1 2])]
                 (state f 'turn) => nil
                 (state f 'now) => 0
                 (state f 'pc 1) => :pc/rem
                 (state f 'last-set 1) => +inf
                 (state f 'first-check 1) => 0))))
