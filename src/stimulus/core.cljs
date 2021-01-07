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

(defn start!
  "Starts the Stimulus application. Stimulus must be started before
  registering any controllers."
  []
  (reset! stimulus-application (.start Application)))

(defn register-controllers!
  "Registers `controllers` using namespaces maps. The namespaces
  represents the controller and the name represents the action. The
  value of the key should be a function which takes two arguments,
  `this` and `state`.


  `:my-controller/static` is a special keyword which a map as a value
  with the keys `:targets` and `:values`. `:targets` is a vector of
  strings and `:values` is a map of keyword / type pairs. For the type
  you can use regular Javascript types, or their keyword counterpart:

  ```
  :vector  : js/Array
  :boolean : js/Boolean
  :integer : js/Number
  :map     : js/Object
  :string  : js/String
  ```
  "
  [controllers]
  (when-not @stimulus-application
    (start!))
  (doseq [[controller-key m] (group-controllers controllers)]
    (.register @stimulus-application (name controller-key) (->stimulus-controller m))))
