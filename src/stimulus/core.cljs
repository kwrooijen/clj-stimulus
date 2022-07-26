(ns stimulus.core
  (:require
   [stimulus.util :as util]
   ["@hotwired/stimulus" :as stimulus :refer [Controller Application]]
   ["classtrophobic" :as cs]))

(defonce stimulus-application (atom nil))

(def keyword->stimulus-value
  {:vector js/Array
   :boolean js/Boolean
   :integer js/Number
   :map js/Object
   :string js/String})

(defn- process-values [m]
  (some->> m
           (mapv (fn [[k t]] [k (keyword->stimulus-value t t)]))
           (into {})))

(defn- map-ctx [this has-f get-f coll]
  (->> coll
       (mapv (fn [v] [v (when (has-f this v) (get-f this v))]))
       (into {})))

(defn- ctx [this values targets]
  {:this this
   :value (->> (mapv first values)
               (map-ctx this stimulus.util/has-value? stimulus.util/get-value))
   :target (map-ctx this stimulus.util/has-target? stimulus.util/get-target targets)
   :targets (map-ctx this stimulus.util/has-target? stimulus.util/get-targets targets)})

(defn- wrap-callbacks [{:keys [values targets]} [k f]]
  (condp = (keyword (name k))
    :static [k (update f :values process-values)]
    :initialize
    [k #(this-as ^js this
          (set! (.-state this) (atom {}))
          (f (.-state this) (ctx this values targets)))]
    [k #(this-as ^js this
          (f (.-state this) (ctx this values targets)))]))

(defn- ->stimulus-controller [controller-key m]
  (-> (map (partial wrap-callbacks (get m (keyword controller-key "static"))) m)
      (->> (into {:new (fn [])}))
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
    (.register @stimulus-application (name controller-key) (->stimulus-controller controller-key m))))
