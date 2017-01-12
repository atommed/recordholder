import React from 'react'
import {IndexLink, Link} from 'react-router'

import AuthForm from "../AuthForm"
import Player from "../Player"


class NavLink extends React.Component {
    render() {
        const isActive = this.context.router.isActive(this.props.to, true);
        const className = isActive ? "active" : "";

        return (
            <li className={className}>
                <Link style={{outline: "none"}} {...this.props}>
                    {this.props.children}
                </Link>
            </li>
        );
    }
}
NavLink.contextTypes = {
    router: React.PropTypes.object
};

class App extends React.Component {
    getMain() {
        if(this.props.user)return this.props.children;
        else return <AuthForm/>
    }

    render() {
        return (
            <div>
                <div id="page">
                    <header>
                        <nav>
                            <div className="nav-wrapper">
                                <IndexLink to="" className="brand-logo right">
                                    <i className="material-icons">library_music</i>
                                    RecordHolder
                                </IndexLink>
                                <ul className="left">
                                    <NavLink to={`collection/${(this.props.user || {}).id}/albums`} >Albums</NavLink>
                                    <NavLink to="trackInfo">Track Info</NavLink>
                                    <NavLink to="uploader">Upload</NavLink>
                                </ul>
                            </div>
                        </nav>
                    </header>
                    <main className="container">{this.getMain()}</main>
                </div>
                <Player />
            </div>
        );
    }
}
App.propTypes = {
    user: React.PropTypes.object,
    children: React.PropTypes.element
};

export default App;