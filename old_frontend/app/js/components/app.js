import {connect} from 'react-redux'
import App from './presentational/app'

function mapStateToProps(state){
    return {
        authentication: state.authentication
    };
}

export default connect(mapStateToProps)(App)