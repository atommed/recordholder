require("./styles/style.scss")

import React from 'react'
import ReactDOM from 'react-dom'
import { createStore, combineReducers } from 'redux'
import { Provider } from 'react-redux'
import { Link, Router, Route, browserHistory } from 'react-router'
import { syncHistoryWithStore, routerReducer } from 'react-router-redux'
 
 
// Add the reducer to your store on the `routing` key 
const store = createStore(
  combineReducers({
    routing: routerReducer
  }), window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__())
 
// Create an enhanced history that syncs navigation events with the store 
const history = syncHistoryWithStore(browserHistory, store)

function App(props){
return <div>Moi detki <br/> {props.children} <br/>
         <Link to="/foo">Nalevo</Link><br />
         <Link to="/bar">Napravo</Link>
       </div>
}

function Bar(){return <div>Yeah! Bar!</div>}
function Foo(){return <div>Oh no( fffuuu!</div>}
 
ReactDOM.render(
  <Provider store={store}>
    { /* Tell the Router to use our enhanced history */ }
    <Router history={history}>
      <Route path="/" component={App}>
        <Route path="foo" component={Foo}/>
        <Route path="bar" component={Bar}/>
      </Route>
    </Router>
  </Provider>,
  document.getElementById('app_mount')
)
