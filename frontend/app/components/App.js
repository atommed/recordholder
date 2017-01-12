import {connect} from "react-redux";

import VisibleApp from "./presentational/App"

export default connect(function mapStateToProps(state) {
    return {
        user: state.auth.user
    }
})(VisibleApp)