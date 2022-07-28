# Clojure Stimulus

[![Clojars Project](https://img.shields.io/clojars/v/kwrooijen/stimulus.svg)](https://clojars.org/kwrooijen/stimulus)

## Installation

`classtrophobic` is a necessary dependency since stimulus uses classes.

### package.json

```javascript
{
    "dependencies": {
        "classtrophobic": "0.1.2",
        "stimulus": "3.1.0",
    }
}
```

## Usage

Read the [Stimulus handbook](https://stimulus.hotwire.dev/handbook/introduction)


### Clojurescript

```clojure
(ns app.core
  (:require
   [stimulus.core :as stimulus]))

(defn set-counter! [output counter]
  (set! (.-innerText output) counter))

(defn initialize [state _ctx]
  (reset! state {:counter 0}))

(defn connect [_state {:keys [target]}]
  (set-counter! (:output target) 0))

(defn add-amount [state {:keys [target value]}]
  (swap! state update :counter + (:amount value))
  (set-counter! (:output target) (:counter @state)))

(def controllers
  {:counter/static {:targets [:output]
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

## Controllers

Controller actions take two arguments:

* `state` which holds an atom during the controllers lifetime.
* `ctx` which is a map containing the following keys:

| Field    | Description                |
|:---------|:---------------------------|
| :this    | The controllers object     |
| :value   | A map of single values     |
| :target  | A map of the first targets |
| :targets | A map of multiple targets  |

## author / License

Released under the [MIT License] by [Kevin William van Rooijen].

[Kevin William van Rooijen]: https://twitter.com/kwrooijen

[MIT License]: https://github.com/kwrooijen/stimulus/blob/master/LICENSE
