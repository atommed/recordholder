import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider} from 'react-redux'
import {Router, Route, browserHistory, Link} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'

const store = createStore(combineReducers({
    routing: routerReducer
}),window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

const history = syncHistoryWithStore(browserHistory, store);

class A extends React.Component{
    render(){
        return <div>
            <Link to="/path1">Go left</Link><br />
            <Link to="/path2">Go right</Link>
            {this.props.children}
        </div>;
    }
}

function Kek(params) {
    return <div>Keeek</div>;
}

function Lel(params) {
    return <div>Leeeeeel</div>;
}

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={A}>
                <Route path="path1" component={Kek} />
                <Route path="path2" component={Lel} />
            </Route>
        </Router>
    </Provider>,
    document.getElementById("app_mount"));

/*
ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={A}>
                <Route path="foo" component={A}/>
                <Route path="bar" component={B}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);*/
/*
ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={vApp}>
                <Route path="kek" component={A}/>
                <Route path="lel" component={B}/>
            </Route>
        </Router>
    </Provider>,
	document.getElementById("app_mount"));
*/