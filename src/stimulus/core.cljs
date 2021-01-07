(ns stimulus.core
  (:require
   ["stimulus" :as stimulus :refer [Controller Application]]
   ["classtrophobic-es5" :as cs]))

(defonce stimulus-application (atom nil))

(def keyword->stimulus-value
  {:vector js/Array
   :boolean js/Boolean
   :integer js/Number
   :map js/Object
   :string js/String})

(defn- process-values [m]
  (when m
    (into {}
          (for [[k t] m]
            [k (keyword->stimulus-value t t)]))))

(defn- wrap-this [[k f]]
  (condp = (keyword (name k))
    :static [k (update f :values process-values)]
    :initialize
    [k #(this-as ^js this
          (set! (.-state this) (atom {}))
          (f this (.-state this)))]
    [k #(this-as ^js this
          (f this (.-state this)))]))

(defn- ->stimulus-controller [m]
  (-> (map wrap-this m)
      (->> (into {}))
      (assoc :extends Controller)
      (clj->js)
      (cs)))

(defn- group-controllers [controllers]
  (->> controllers
       (group-by (comp namespace first))
       (mapv (fn [[k v]] [k (apply hash-map (flatten v))]))))

(defn start! []
  (reset! stimulus-application (.start Application)))

(defn register-controllers! [controllers]
  (when-not @stimulus-application
    (start!))
  (doseq [[controller-key m] (group-controllers controllers)]
    (.register @stimulus-application (name controller-key) (->stimulus-controller m))))
