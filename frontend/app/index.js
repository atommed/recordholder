require("./styles/style.scss");

import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider, connect} from 'react-redux'
import {Link, Router, Route, browserHistory} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'
import Uploader from './js/components/uploader'
import * as reducers from './js/reducers'

const reduxDevToolsHack = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();
const store = createStore(
    combineReducers({
        ...reducers,
        routing: routerReducer
    }), reduxDevToolsHack);
const history = syncHistoryWithStore(browserHistory, store);



@connect(state=>{return {text: state.trackUploadResult}})
class Display extends React.Component{
    render() {return <div>{this.props.text}</div>;}
}

ReactDOM.render(
    <Provider store={store} >
        <div>
            <Display />
            <Uploader targetURL="api/tracks/upload"/>
        </div>
    </Provider>,
    document.getElementById("app_mount")
);

/*
ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={App}>
                <Route path="foo" component={UploadForm}/>
                <Route path="bar" component={Bar}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);
*/
