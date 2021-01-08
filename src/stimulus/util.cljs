(ns stimulus.util
  (:require
   [clojure.string :as string]))

(defn has-value? [this v]
  (aget this (str "has" (string/capitalize (name v)) "Value")))

(defn has-target? [this v]
  (aget this (str "has" (string/capitalize (name v)) "Target")))

(defn get-value [this v]
  (aget this (str (name v) "Value")))

(defn get-target [this v]
  (aget this (str (name v) "Target")))

(defn get-targets [this v]
  (aget this (str (name v) "Targets")))
