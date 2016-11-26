import React from 'react'
import {Link, IndexLink} from 'react-router'
import Player from '../player'
import TrackInfo from './track-info'
import AuthForm from '../auth-form'

class NavLink extends React.Component {
    render() {
        let isActive = this.context.router.isActive(this.props.to, true),
            className = isActive ? "active" : "";

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
    getMain(){
        if(this.props.authentication.token !== undefined)
            return this.props.children;
        else return <AuthForm/>
    }

    render() {
        return (
            <div id="page">
                <header>
                    <nav>
                        <div className="nav-wrapper">
                            <IndexLink to="" className="brand-logo right">
                                <i className="mi mi-library-music"/>
                                RecordHolder
                            </IndexLink>
                            <ul className="left hide-on-med-and-down">
                                <NavLink to="a">Ahrenet</NavLink>
                                <NavLink to="b">Udevlen</NavLink>
                                <NavLink to="trackInfo">Track Info</NavLink>
                                <NavLink to="uploader">Upload</NavLink>
                            </ul>
                        </div>
                    </nav>
                </header>
                <main className="container">{this.getMain()}</main>
                <footer>
                    <Player src="storage/tracks/44.ogg"/>
                </footer>
            </div>
        );
    }
}

export default App;