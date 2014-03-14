(ns webch16.core)

(use '[ring.adapter.jetty :only (run-jetty)])
(use '[ring.middleware.params :only (wrap-params)])    ;optional
(use '[compojure.core])                                ;optional
(require 'compojure.route)                             ;optional
(require '[clojure.string :as str])
(require '[noir.response :as resp])
(require '[clojure.data.json :as json] )


(def initial-board

     [[:R :N :B :Q :K :B :N :R]

      [:P :P :P :P :P :P :P :P]

      [:- :- :- :- :- :- :- :-]

      [:- :- :- :- :- :- :- :-]

      [:- :- :- :- :- :- :- :-]

      [:- :- :- :- :- :- :- :-]

      [:p :p :p :p :p :p :p :p]

      [:r :n :b :q :k :b :n :r]])

(defn board-map [f board]
(vec (map #(vec (for [s %] (f s))) board)))

(def board (board-map ref initial-board))


(defn app2 [request] (println request){:body (str request)})




(defn get-board-ref [pos];pos
      ((board (pos 0))(pos 1)))


(defn change-cell [from to] to)

(defn make-move [piece to];from =[r c]
       (dosync
       (let [to-ref(get-board-ref to)]
                      ;(alter ref fun & args) => (apply fun current-value-fun args)
                      (alter to-ref change-cell piece)
                      )))

; (make-move :w [0 1])


(defn win-checker[r c]

  (or
  ;row
  (try (= (deref(get-board-ref[(- r 2) c])) (deref(get-board-ref[(- r 1) c])) (deref(get-board-ref[r c]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[(- r 1) c])) (deref(get-board-ref[r c]))       (deref(get-board-ref[(+ r 1) c]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[ r c])) (deref(get-board-ref[(+ r 1) c]))      (deref(get-board-ref[(+ r 2) c]))) (catch Exception e (println e)))
  ;column
  (try (= (deref(get-board-ref[r (- c 2)])) (deref(get-board-ref[r (- c 1)])) (deref(get-board-ref[r c]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[r (- c 1)])) (deref(get-board-ref[r  c]))      (deref(get-board-ref[r (+ c 1)]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[r  c])) (deref(get-board-ref[r (+ c 1)]))      (deref(get-board-ref[r (+ c 2)]))) (catch Exception e (println e)))
   ;diagonal 0 0 to 55
  (try (= (deref(get-board-ref[(- r 2) (- c 2)])) (deref(get-board-ref[(- r 1) (- c 1)])) (deref(get-board-ref[r c]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[(- r 1) (- c 1)])) (deref(get-board-ref[ r  c])) (deref(get-board-ref[(+ r 1) (+ c 1)]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[r c]))            (deref(get-board-ref[(+ r 1) (+ c 1)])) (deref(get-board-ref[(+ r 2) (+ c 2)]))) (catch Exception e (println e)))
   ;diagonal 05  to 50
   (try (= (deref(get-board-ref[(+ r 2) (- c 2)])) (deref(get-board-ref[(+ r 1) (- c 1)])) (deref(get-board-ref[r c]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[(+ r 1) (- c 1)])) (deref(get-board-ref[ r  c])) (deref(get-board-ref[(- r 1) (+ c 1)]))) (catch Exception e (println e)))
  (try (= (deref(get-board-ref[r c]))            (deref(get-board-ref[(- r 1) (+ c 1)])) (deref(get-board-ref[(- r 2) (+ c 2)]))) (catch Exception e (println e)))

   )
  )


 (defn commove [size]
  (def r (rand-int size))
  (def c (rand-int size))
   (println r c "ran")
  (if(= (deref(get-board-ref[r c])) :-)
  (do
   (make-move :O [r c])
    {:type "commove" :r r :c c}
    )
  ;else
  (commove size)
  )
)

 ;

(defn checktie [size r c]
;  (println r c)
  (if (= (deref(get-board-ref[r c])) :-)
   (do
    ;(println r c)
      false
      )
    ;else
    (if (< c  (- size 1) )
      (checktie size r (+ c 1))
      ;else
      (if(< r  (- size 1))
        (checktie size (+ r 1) 0)
        ;else
        true
        )
      )
)
)

(defn post-handler[type r c size]
  (println"get request" type)
  (if(= type "newgame")
  (do
  (def board (board-map ref initial-board))
  (println"board created" )
      {:type "newgame"}
      )
   ;else
    (if(= type "move")
      (do
      (make-move :X [r c])
       (if(win-checker r c)
        {:type "userwon" :r 0 :c 0}
          ;else
           (if (checktie size 0 0)
             {:type "tie" :r 0 :c 0}
            ; else commove
            (do
           (def position (commove size))
           (if (win-checker (position :r) (position :c))
             {:type "comwon" :r (position :r) :c (position :c)}
             ;else
             (if (checktie size 0 0)
                {:type "tie" :r 0 :c 0}
               ;else
               {:type "commove" :r (position :r) :c (position :c)}
               )))))))))

(def app2+

  (routes
     (HEAD "/" [] "")
     (GET "/" [] (ring.util.response/resource-response "tictactoe2.html"))
    (POST  "/" [type r c size] (do  (json/write-str (post-handler type (Integer/parseInt r) (Integer/parseInt c) (Integer/parseInt size))) ))
   )
 )


(def app2 (wrap-params app2+)) ; wrap-params parse html posted form (and query-string)

;server
(def server (run-jetty #'app2 {:port 8080 :join? false}))

