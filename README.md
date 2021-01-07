# Clojure Stimulus

## Installation

### Versions

* [stimulus versions](http://repo.clojars.org/kwrooijen/stimulus/0.0.1-SNAPSHOT/)

```clojure
:dependencies [[kwrooijen/stimulus "0.0.1-xxxxxxxx.yyyyyy-z"]
               ,,,]
```

```javascript
{
    "dependencies": {
        "classtrophobic-es5": "0.2.1",
        "stimulus": "2.0.0",
    }
}
```

## Usage

Read the [Stimulus handboot](https://stimulus.hotwire.dev/handbook/introduction)


### Clojurescript

```clojure
(ns app.core
  (:require
   [stimulus.core :as stimulus]))

(defn update-output! [^js this state]
  (set! (.. this -outputTarget -innerText) (:counter @state)))

(defn initialize [^js this state]
  (reset! state {:counter 0
                 :amount (.-amountValue this)}))

(defn connect [this state]
  (update-output! this state))

(defn add-amount [this state]
  (println @state)
  (swap! state update :counter + (:amount @state))
  (update-output! this state))

(def controllers
  {:counter/static {:targets ["output"]
                    :values {:amount :integer}}
   :counter/initialize initialize
   :counter/connect connect
   :counter/add-amount add-amount})

(stimulus/register-controllers! controllers)
```

### Hiccup

```clojure
[:div
 {:data-controller :counter
  :data-counter-amount-value 2}
 [:div
  [:span "Counter: "]
  [:span {:data-counter-target :output}]]
 [:button
  {:data-action :click->counter#add-amount}
  "Add Amount"]]
```

## Author / License

Released under the [MIT License] by [Kevin William van Rooijen].

[Kevin William van Rooijen]: https://twitter.com/kwrooijen

[MIT License]: https://github.com/kwrooijen/stimulus/blob/master/LICENSE
