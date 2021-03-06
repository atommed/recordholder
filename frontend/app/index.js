require('materialize-css/dist/css/materialize.css');
require('materialize-css/dist/js/materialize');
require('./style.scss');

import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider} from 'react-redux'
import {Router, Route, hashHistory} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'
import * as Cookies from "js-cookie"

import * as reducers from "./reducers"
import {signInSuccess} from "./actions"
import App from './components/App'
import Uploader from "./components/Uploader"

const reduxDevToolsHack = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();
const store = createStore(
    combineReducers({
        routing: routerReducer,
        ...reducers
    }),
    reduxDevToolsHack);
const history = syncHistoryWithStore(hashHistory, store);

$.ajaxPrefilter(function (options, originalOptions, jqXHR) {
    const token = Cookies.get('Csrf-Token');
    jqXHR.setRequestHeader('Csrf-Token', token);
});

$.postJson = function(url, data){
    return $.ajax({
        url,
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json',
        dataType: 'json'
    })
};

const auth = Cookies.getJSON('auth');
if(auth) store.dispatch(signInSuccess(auth.user));

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={App}>
                <Route path="uploader" component={Uploader}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);